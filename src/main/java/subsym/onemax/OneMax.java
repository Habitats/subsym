package subsym.onemax;

import java.util.stream.IntStream;

import subsym.Log;
import subsym.genetics.GeneticPreferences;
import subsym.genetics.GeneticProblem;

/**
 * Created by anon on 21.02.2015.
 */
public class OneMax extends GeneticProblem {

  private static final String TAG = GeneticProblem.class.getSimpleName();
  private int bitVectorSize;

  public OneMax(GeneticPreferences prefs, int bitVectorSize) {
    super(prefs);
    this.bitVectorSize = bitVectorSize;
  }

  @Override
  public boolean solution() {
    return getPopulation().getBestGenotype().fitness() == 1;
  }

  @Override
  protected double getCrossoverCut() {
    return Main.random().nextDouble();
  }

  @Override
  public void initPopulation() {
    IntStream.range(0, getPopulationSize()).forEach(i -> getPopulation().add(new OneMaxGenotype().setRandom(bitVectorSize)));
  }

  public int getBitVecotorSize() {
    return bitVectorSize;
  }

  @Override
  public GeneticProblem newInstance() {
    return new OneMax(getPreferences(), bitVectorSize);
  }

  @Override
  public GeneticProblem newInstance(GeneticPreferences prefs) {
    return new OneMax(prefs, bitVectorSize);
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
