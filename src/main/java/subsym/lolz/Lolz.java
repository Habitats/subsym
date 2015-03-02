package subsym.lolz;

import java.util.stream.IntStream;

import subsym.genetics.GeneticProblem;
import subsym.genetics.adultselection.AdultSelection;
import subsym.genetics.matingselection.MatingSelection;

/**
 * Created by anon on 23.02.2015.
 */
public class Lolz extends GeneticProblem {

  private final int bitVectorSize;

  public Lolz(int populationSize, int bitVectorSize, double crossOverRate, double populationMutationRate,
              double genotypeMutationRate, AdultSelection adultSelectMode, MatingSelection matingMode,
              boolean ensureUnique) {
    super(populationSize, crossOverRate, populationMutationRate, genotypeMutationRate, adultSelectMode, matingMode,
          ensureUnique);
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
