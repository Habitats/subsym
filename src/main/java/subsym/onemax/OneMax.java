package subsym.onemax;

import java.util.stream.IntStream;

import subsym.ga.GeneticProblem;

/**
 * Created by anon on 21.02.2015.
 */
public class OneMax extends GeneticProblem {

  private static final String TAG = GeneticProblem.class.getSimpleName();
  private int bitVectorSize;

  public OneMax(int populationSize, int bitVectorSize, double crossOverRate, double genomeMutationRate,
                double genotypeMutationRate, AdultSelection adultSelectMode, MateSelection matingMode) {
    super(populationSize, crossOverRate, genomeMutationRate, genotypeMutationRate, adultSelectMode, matingMode);
    this.bitVectorSize = bitVectorSize;
  }

  @Override
  public boolean solution() {
    return getPopulation().getBestGenotype().fitness() == bitVectorSize;
  }

  @Override
  public void initPopulation() {
    IntStream.range(0, getPopulationSize())
        .forEach(i -> getPopulation().add(new OneMaxGenotype(bitVectorSize).setRandom()));
  }

  @Override
  public String toString() {
    return "Best: " + getPopulation().getBestGenotype().toString();
  }

}
