package subsym.lolz;

import java.util.stream.IntStream;

import subsym.genetics.GeneticPreferences;
import subsym.genetics.GeneticProblem;

/**
 * Created by anon on 23.02.2015.
 */
public class Lolz extends GeneticProblem {

  private final GeneticPreferences prefs;
  private final int bitVectorSize;
  private final int zeroThreshold;

  public Lolz(GeneticPreferences prefs, int bitVectorSize, int zeroThreshold) {
    super(prefs);
    this.prefs = prefs;
    this.bitVectorSize = bitVectorSize;
    this.zeroThreshold = zeroThreshold;
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
        .forEach(v -> getPopulation().add(new LolzGenotype(zeroThreshold).setRandom(bitVectorSize)));
  }
}
