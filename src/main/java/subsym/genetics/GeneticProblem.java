package subsym.genetics;

import subsym.Log;
import subsym.gui.Plot;

/**
 * Created by anon on 21.02.2015.
 */
public abstract class GeneticProblem {

  private static final String TAG = GeneticProblem.class.getSimpleName();

  private final GeneticPreferences prefs;
  private Population population;

  public GeneticProblem(GeneticPreferences prefs) {
    this.prefs = prefs;
    population = new Population(prefs.getPopulationSize());
  }

  public int generations() {
    return population.getCurrentGeneration();
  }

  public int getPopulationSize() {
    return prefs.getPopulationSize();
  }

  public void cleanUp() {
    getPopulation().cleanUp();
  }

  protected Population getPopulation() {
    return population;
  }

  public void select() {
    population.selectAdults(prefs.getAdultSelectionMode());
  }

  public void crossOver() {
    population.crossOver(prefs.getCrossOverRate(), prefs.getMateSelectionMode());
  }

  protected abstract double getCrossoverCut();

  public void mutate() {
    population.mutate(prefs.getPopulationMutationRate(), prefs.getGenomeMutationRate());
  }

  public void log() {
    Log.v(TAG, population);
    Plot.addValue("avg", population.getCurrentGeneration(), population.getCurrentAverageFitness());
    Plot.addValue("max", population.getCurrentGeneration(), population.getCurrentMaxFitness());
    Plot.addValue("sd", population.getCurrentGeneration(), population.getCurrentStandardDeviation());

  }

  public abstract void initPopulation();

  public abstract boolean solution();

  public String getId() {
    return prefs.toString();
  }

  @Override
  public String toString() {
//    int l1 = Arrays.stream(AdultSelection.values()).mapToInt(v -> v.name().length()).max().getAsInt();
//    int l2 = Arrays.stream(MateSelection.values()).mapToInt(v -> v.name().length()).max().getAsInt();
    String l1 = "";
    String l2 = "";
    return String.format("%s > Population > %s", prefs, population);
  }
}
