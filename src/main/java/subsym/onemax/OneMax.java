package subsym.onemax;

import java.util.stream.IntStream;

import subsym.genetics.GeneticProblem;

/**
 * Created by anon on 21.02.2015.
 */
public class OneMax extends GeneticProblem {

  private static final String TAG = GeneticProblem.class.getSimpleName();
  private int bitVectorSize;

  public OneMax(int populationSize, int bitVectorSize, double crossOverRate, double populationMutationRate,
                double genotypeMutationRate, AdultSelection adultSelectMode, MateSelection matingMode) {
    super(populationSize, crossOverRate, populationMutationRate, genotypeMutationRate, adultSelectMode, matingMode);
    this.bitVectorSize = bitVectorSize;
  }

  @Override
  public boolean solution() {
    return getPopulation().getBestGenotype().fitness() == bitVectorSize;
  }

  @Override
  protected double getCrossoverCut() {
    return Math.random();
  }

  @Override
  public void initPopulation() {
    IntStream.range(0, getPopulationSize())
        .forEach(i -> getPopulation().add(new OneMaxGenotype().setRandom(bitVectorSize)));
  }

}
