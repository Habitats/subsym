package subsym.surprisingsequence;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import subsym.Log;
import subsym.Main;
import subsym.genetics.GeneticPreferences;
import subsym.genetics.GeneticProblem;

/**
 * Created by anon on 23.02.2015.
 */
public class SurprisingSequences extends GeneticProblem {

  private static final String TAG = SurprisingSequences.class.getSimpleName();
  private final List<Integer> alphabet;
  private final boolean global;
  private int length;

  public SurprisingSequences(GeneticPreferences prefs, int alphabetSize, int length, boolean global) {
    super(prefs);
    this.global = global;
    this.length = length;
    alphabet = IntStream.range(0, alphabetSize).boxed().collect(Collectors.toList());
  }

  private List<Integer> createPermutation(List<Integer> alphabet, int length) {
    Random random = new Random();
    return IntStream.range(0, length)//
        .map(v -> alphabet.get(random.nextInt(alphabet.size()))).boxed().collect(Collectors.toList());
  }

  @Override
  protected double getCrossoverCut() {
    return Main.random().nextDouble();
  }

  @Override
  public void initPopulation() {
    IntStream.range(0, getPopulationSize()).forEach(i -> {
      List<Integer> permutation = createPermutation(alphabet, getSurprisingLength());
      getPopulation().add(new SurprisingGenotype(permutation, alphabet, global, getPreferences().shouldGrayCode()));
    });
  }

  @Override
  public boolean solution() {
    return getPopulation().getBestGenotype().fitness() == 1;
  }

  public int getSurprisingLength() {
    return length;
  }

  public int getAlphabetSize() {
    return alphabet.size();
  }

  @Override
  public GeneticProblem newInstance() {
    return new SurprisingSequences(getPreferences(), alphabet.size(), getSurprisingLength(), global);
  }

  @Override
  public GeneticProblem newInstance(GeneticPreferences prefs) {
    return new SurprisingSequences(prefs, alphabet.size(), getSurprisingLength(), global);
  }

  @Override
  public void increment(int increment) {
    length += increment;
  }

  @Override
  public void onSolved() {
    Log.v(TAG, this);
  }
}
