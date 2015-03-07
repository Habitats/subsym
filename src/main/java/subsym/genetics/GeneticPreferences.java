package subsym.genetics;

import java.util.HashMap;
import java.util.Map;

import subsym.genetics.adultselection.AdultSelection;
import subsym.genetics.adultselection.FullTurnover;
import subsym.genetics.adultselection.Mixing;
import subsym.genetics.matingselection.FitnessProportiate;
import subsym.genetics.matingselection.MatingSelection;
import subsym.genetics.matingselection.Tournament;
import subsym.lolz.Lolz;
import subsym.onemax.OneMax;
import subsym.surprisingsequence.SurprisingSequences;

/**
 * Created by anon on 02.03.2015.
 */
public class GeneticPreferences {


  private int populationSize;
  private double crossOverRate;
  private double populationMutationRate;
  private double genomeMutationRate;
  private AdultSelection adultSelectionMode;
  private MatingSelection mateSelectionMode;
  private GeneticProblem puzzle;
  private int bitVectorSize;
  private int surprisingLength;
  private int alphabetSize;
  private int zeroThreashold;
  private boolean loggingEnabled;
  private int runCount;
  private boolean shouldIncrement;
  private int increment;

  public GeneticPreferences(int populationSize, double crossOverRate, double populationMutationRate,
                            double genomeMutationRate, AdultSelection adultSelectionMode,
                            MatingSelection mateSelectionMode) {
    this.populationSize = populationSize;
    this.crossOverRate = crossOverRate;
    this.populationMutationRate = populationMutationRate;
    this.genomeMutationRate = genomeMutationRate;
    this.adultSelectionMode = adultSelectionMode;
    this.mateSelectionMode = mateSelectionMode;
  }

  public AdultSelection getAdultSelectionMode() {
    return adultSelectionMode;
  }

  public double getCrossOverRate() {
    return crossOverRate;
  }

  public double getGenomeMutationRate() {
    return genomeMutationRate;
  }

  public double getPopulationMutationRate() {
    return populationMutationRate;
  }

  public int getPopulationSize() {
    return populationSize;
  }

  public MatingSelection getMateSelectionMode() {
    return mateSelectionMode;
  }

  public void setAdultSelectionMode(AdultSelection adultSelectionMode) {
    this.adultSelectionMode = adultSelectionMode;
  }

  public void setCrossOverRate(double crossOverRate) {
    this.crossOverRate = crossOverRate;
  }

  public void setGenomeMutationRate(double genomeMutationRate) {
    this.genomeMutationRate = genomeMutationRate;
  }

  public void setMateSelectionMode(MatingSelection mateSelectionMode) {
    this.mateSelectionMode = mateSelectionMode;
  }

  public void setPopulationMutationRate(double populationMutationRate) {
    this.populationMutationRate = populationMutationRate;
  }

  public void setPopulationSize(int populationSize) {
    this.populationSize = populationSize;
  }

  @Override
  public String toString() {
    return String.format("CR: %.2f - GMR: %.2f IMR: %.2f - AS: %s - MS: %s", //
                         crossOverRate, populationMutationRate, genomeMutationRate,
                         adultSelectionMode.getClass().getSimpleName(), mateSelectionMode.getClass().getSimpleName());
  }

  public static GeneticPreferences getDefault() {
    return new GeneticPreferences(40, 0.95, 0.9, 0.04, new Mixing(0.2), new Tournament(4, 0.05));
  }

  public static GeneticPreferences getTest() {
    return new GeneticPreferences(10, 1, 1, 1, new Mixing(1), new Tournament(10, 0.00));
  }

  public static GeneticPreferences getOneMaxTest() {
    GeneticPreferences prefs = new GeneticPreferences(3, 1, 1, 0.5, new FullTurnover(), new FitnessProportiate());
    OneMax oneMax = new OneMax(prefs, 5);
    prefs.setPuzzle(oneMax);
    return prefs;
  }

  public static GeneticPreferences getSurprisingSequences() {
    GeneticPreferences prefs = new GeneticPreferences(40, 0.1, 0.9, 0.0017, new Mixing(0.5), new Tournament(10, 0.05));
    GeneticProblem problem = new SurprisingSequences(prefs, 40, 90, true);
    prefs.setPuzzle(problem);
    return prefs;
  }

  public static GeneticPreferences getLolzTest() {
    GeneticPreferences prefs = new GeneticPreferences(3, 1, 1, 0.2, new FullTurnover(), new FitnessProportiate());
    GeneticProblem problem = new Lolz(prefs, 5, 2);
    prefs.setPuzzle(problem);
    return prefs;
  }

  public static GeneticPreferences getLolz2() {
    GeneticPreferences prefs = new GeneticPreferences(40, 1, 1, 0.2, new FullTurnover(), new FitnessProportiate());
    GeneticProblem problem = new Lolz(prefs, 40, 21);
    prefs.setPuzzle(problem);
    return prefs;
  }

  public static GeneticPreferences getOneMaxBelow100() {
    GeneticPreferences prefs = new GeneticPreferences(40, 1, 1, 0.2, new FullTurnover(), new FitnessProportiate());
    GeneticProblem problem = new OneMax(prefs, 40);
    prefs.setPuzzle(problem);
    return prefs;
  }


  public static Map<String, GeneticPreferences> getPresets() {
    Map<String, GeneticPreferences> presets = new HashMap<>();
    presets.put("Surprising 90-40", getSurprisingSequences());
    presets.put("OneMax 3-5", getOneMaxTest());
    presets.put("Lolz 3-5", getLolzTest());
    presets.put("Lolz 40-21", getLolz2());
    presets.put("OneMax 40-21 below 100", getOneMaxBelow100());
    return presets;
  }

  public int getBitVectorSize() {
    return bitVectorSize;
  }

  public void setBitVectorSize(int bitVectorSize) {
    this.bitVectorSize = bitVectorSize;
  }

  public void setAlphabetSize(int alphabetSize) {
    this.alphabetSize = alphabetSize;
  }

  public int getAlphabetSize() {
    return alphabetSize;
  }

  public void setSurprisingLength(int surprisingLength) {
    this.surprisingLength = surprisingLength;
  }

  public int getSurprisingLength() {
    return surprisingLength;
  }

  public int getZeroThreashold() {
    return zeroThreashold;
  }

  public void setZeroThreashold(int zeroThreashold) {
    this.zeroThreashold = zeroThreashold;
  }


  public void setPuzzle(GeneticProblem puzzle) {
    this.puzzle = puzzle;
  }

  public GeneticProblem getPuzzle() {
    return puzzle.newInstance();
  }

  public void logginEnabled(boolean loggingEnabled) {
    GeneticEngine.enableLogging(loggingEnabled);
    this.loggingEnabled = loggingEnabled;
  }

  public boolean isLoggingEnabled() {
    return loggingEnabled;
  }

  public void setRunCount(int runCount) {
    this.runCount = runCount;
  }

  public int getRunCount() {
    return runCount;
  }

  public void setShouldIncrement(boolean shouldIncrement) {
    this.shouldIncrement = shouldIncrement;
  }

  public boolean shouldIncrement() {
    return shouldIncrement;
  }

  public int getIncrement() {
    return increment;
  }

  public void increment() {
    increment++;
  }

  public void reset() {
    increment = 0;
  }
}
