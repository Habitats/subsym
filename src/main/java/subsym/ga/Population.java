package subsym.ga;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by anon on 21.02.2015.
 */
public class Population {

  private Queue<Genotype> population;

  public Population(int populationSize, int bitVectorSize) {
    population = new PriorityQueue<>((o1, o2) -> o2.numOnes() - o1.numOnes());
    IntStream.range(0, populationSize).forEach(i -> population.add(Genotype.getRandom(bitVectorSize)));
  }

  public Genotype getBestGenotype() {
    return population.peek();
  }

  public void selectAdults(GeneticProblem.AdultSelection selectionMode) {
    switch (selectionMode) {
      case TOP_HALF:
        population.removeIf(v -> v.numOnes() < population.peek().numOnes());
        break;
      case FULL_TURNOVER:
        population.forEach(v -> v.tagForRemoval());
        break;
      case OVER_PRODUCTION:
        break;
      case MIXING:
        break;
    }
  }

  public void crossOver(double crossOverRate, double cut) {
    if (crossOverRate < Math.random()) {
      return;
    }

    List<Genotype> populationList = new ArrayList<>(population);
    Genotype p1 = getCrossOverCandidate(populationList);
    Genotype p2 = getCrossOverCandidate(populationList);

    Genotype child = Genotype.crossOver(p1, p2, cut);
    population.add(child);
  }

  public Genotype getCrossOverCandidate(List<Genotype> populationList) {
    int sum = populationList.stream().mapToInt(v -> v.numOnes()).sum();
    List<Double> weights = populationList.stream() //
        .map(v -> Math.random() * v.numOnes() / sum) //
        .collect(Collectors.toList());
    int index = weights.indexOf(Collections.max(weights));
    return populationList.remove(index);
  }

  public void mutate(double genomeMutationRate, double genomeComponentRate) {
    List<Genotype> children = new ArrayList<>(population);
    Collections.shuffle(children);
    int numBitsToMutate = (int) (genomeMutationRate * population.peek().size());
    int genotypesToMutate = (int) (genomeComponentRate * population.size());
    children.stream() //
        .limit(genotypesToMutate) //
        .forEach(v -> v.mutate(numBitsToMutate));
  }

  public void cleanUp() {
    population.removeIf(v -> v.shouldDie());
  }
}
