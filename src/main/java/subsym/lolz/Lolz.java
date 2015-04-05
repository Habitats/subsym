package subsym.lolz;

import java.util.stream.IntStream;

import subsym.Log;
import subsym.genetics.GeneticPreferences;
import subsym.genetics.GeneticProblem;

/**
 * Created by anon on 23.02.2015.
 */
public class Lolz extends GeneticProblem {

  private static final String TAG = Lolz.class.getSimpleName();
  private int bitVectorSize;
  private final int zeroThreshold;

  public Lolz(GeneticPreferences prefs, int bitVectorSize, int zeroThreshold) {
    super(prefs);
    this.bitVectorSize = bitVectorSize;
    this.zeroThreshold = zeroThreshold;
  }

  @Override
  public boolean solution() {
    return getPopulation().getBestGenotype().fitness() == 1 || getPopulation().getCurrentGeneration() > bitVectorSize * 100;
  }

  @Override
  protected double getCrossoverCut() {
    return Math.random();
  }

  @Override
  public void initPopulation() {
    IntStream.range(0, getPopulationSize()).forEach(v -> getPopulation().add(new LolzGenotype(zeroThreshold).setRandom(bitVectorSize)));
  }

  public int getZeroThreshold() {
    return zeroThreshold;
  }

  public int getBitVecotorSize() {
    return bitVectorSize;
  }

  @Override
  public GeneticProblem newInstance() {
    return new Lolz(getPreferences(), bitVectorSize, zeroThreshold);
  }

  @Override
  public GeneticProblem newInstance(GeneticPreferences prefs) {
    return new Lolz(prefs, bitVectorSize, zeroThreshold);
  }

  @Override
  public void increment(int increment) {
    bitVectorSize += increment;
  }

  @Override
  public void onSolved() {
    Log.i(TAG, this);
  }
}
