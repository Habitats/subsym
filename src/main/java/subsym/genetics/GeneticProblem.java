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

  public int generations() {
    return population.getCurrentGeneration();
  }

  public int getPopulationSize() {
    return prefs.getPopulationSize();
  }

  protected Population getPopulation() {
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
    Log.i(TAG, String.format("(%4d / %4d) ms - %s",//
                             delta, (int) (avg / count), population));
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
    addValue("avg", population.getCurrentGeneration(), population.getCurrentAverageFitness());
    addValue("max", population.getCurrentGeneration(), population.getCurrentMaxFitness());
    addValue("sd", population.getCurrentGeneration(), population.getCurrentStandardDeviation());
  }

  private void addValue(String max, int currentGeneration, double currentMaxFitness) {
    if (plotter != null) {
      plotter.addValue(max, currentGeneration, currentMaxFitness);
    }
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


}
