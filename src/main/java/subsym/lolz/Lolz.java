package subsym.lolz;

import java.util.stream.IntStream;

import subsym.ga.GeneticProblem;

/**
 * Created by anon on 23.02.2015.
 */
public class Lolz extends GeneticProblem {

  private final int bitVectorSize;

  public Lolz(int populationSize, int bitVectorSize, double crossOverRate, double genomeMutationRate,
              double genotypeMutationRate, AdultSelection adultSelectMode, MateSelection matingMode) {
    super(populationSize, crossOverRate, genomeMutationRate, genotypeMutationRate, adultSelectMode, matingMode);
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
        .forEach(v -> getPopulation().add(new LolzGenotype().setRandom(bitVectorSize)));
  }
}
