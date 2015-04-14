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
  private long delta;
  private long avg;
  private double count = 1;
  private Plot plotter;

  public GeneticProblem(GeneticPreferences prefs) {
    this.prefs = prefs;
    population = new Population(prefs);
    delta = System.currentTimeMillis();
  }

  public GeneticPreferences getPreferences() {
    return prefs;
  }

  public int generations() {
    return population.getCurrentGeneration();
  }

  public int getPopulationSize() {
    return prefs.getPopulationSize();
  }

  public Population getPopulation() {
    return population;
  }

  public void select() {
    population.selectAdults();
  }

  public void crossOver() {
    population.crossOver(prefs.getCrossOverRate(), prefs.getMateSelectionMode());
  }

  protected abstract double getCrossoverCut();

  public void mutate() {
    population.mutate(prefs.getPopulationMutationRate(), prefs.getGenomeMutationRate());
  }

  public void log() {
    delta = System.currentTimeMillis() - delta;
    avg += delta;
//    Log.i(TAG, String.format("(%4d / %4d) ms - %s",//
//                             delta, (int) (avg / count), population));
    Log.i(TAG, getPopulation().getBestGenotype().getPhenotype());
    delta = System.currentTimeMillis();
    count++;
  }

  public void addSomePlots() {
    int currentGeneration = getPopulation().getCurrentGeneration();

    if ((currentGeneration > 4000) && (currentGeneration / 16) % 2 == 0) {
      return;
    } else if ((currentGeneration > 3000) && (currentGeneration / 8) % 2 == 0) {
      return;
    } else if ((currentGeneration > 2000) && (currentGeneration / 4) % 2 == 0) {
      return;
    } else if ((currentGeneration > 1000) && (currentGeneration / 2) % 2 == 0) {
      return;
    } else if (currentGeneration > 500 && currentGeneration % 2 == 0) {
      return;
    }

    addPlots();
  }

  public void addPlots() {
    if (plotter != null) {
      plotter.addSingleRunValue("avg", population.getCurrentGeneration(), population.getCurrentAverageFitness());
      plotter.addSingleRunValue("max", population.getCurrentGeneration(), population.getCurrentMaxFitness());
      plotter.addSingleRunValue("sd", population.getCurrentGeneration(), population.getCurrentStandardDeviation());
    }
  }

  private void addValue(String max, int currentGeneration, double currentMaxFitness) {
    plotter.addSingleRunValue(max, currentGeneration, currentMaxFitness);
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

  public void setPlotter(Plot plotter) {
    this.plotter = plotter;
  }


  public abstract GeneticProblem newInstance(GeneticPreferences prefs);

  public abstract GeneticProblem newInstance();

  public abstract void increment(int increment);

  public void plotResult() {
    plotter.addMultipleRunsValue("cr", prefs.getCrossOverRate(), getPopulation().getCurrentGeneration());
    plotter.addMultipleRunsValue("pmr", prefs.getPopulationMutationRate(), getPopulation().getCurrentGeneration());
    plotter.addMultipleRunsValue("mr", prefs.getGenomeMutationRate(), getPopulation().getCurrentGeneration());
  }

  public void addPlotsForAveraging() {
    if (plotter != null) {
      plotter.addAverageRunValue("avg", population.getCurrentGeneration(), population.getCurrentAverageFitness());
      plotter.addAverageRunValue("max", population.getCurrentGeneration(), population.getCurrentMaxFitness());
      plotter.addAverageRunValue("sd", population.getCurrentGeneration(), population.getCurrentStandardDeviation());
    }
  }

  public abstract void onSolved();
}
