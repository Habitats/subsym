package subsym.genetics;

import subsym.Log;

/**
 * Created by anon on 21.02.2015.
 */
public class GeneticEngine {

  private static final String TAG = GeneticEngine.class.getSimpleName();
  private static boolean shouldRun;
  private static boolean enableLogging;

  public static GeneticProblem solve(GeneticProblem problem, boolean loggingEnabled) {
    GeneticEngine.enableLogging = loggingEnabled;
    shouldRun = true;
    problem.initPopulation();
    int count = 0;
    long start = System.currentTimeMillis();
    while (shouldRun && !problem.solution()) {
      problem.crossOver();
      problem.mutate();
      problem.select();
      if (enableLogging) {
//        Log.v(TAG, String.format("Current population - Size: %3d:", problem.getPopulation().getCurrent().size()));
//        problem.getPopulation().getCurrent().stream().sorted(Comparator.<Genotype>naturalOrder())
//            .forEach(n -> Log.v(TAG, n.getPhenotype() + " > Fitness > " + String.format("%.3f ", n.fitness())));
        problem.addSomePlots();
        problem.log();
      }
      problem.addPlotsForAveraging();
      count++;
      if (count % 10000 == 0) {
        Log.i(TAG, "Gen ... " + count);
      }
    }
    if (loggingEnabled) {
      Log.v(TAG, "Search took: " + (System.currentTimeMillis() - start) / 1000. + " s");
    }
    return problem;
  }

  public static void solveInBackground(Genetics.GeneticRun runs, boolean loggingEnabled, Genetics genetics) {
    shouldRun = true;
    new Thread(() -> {
      runs.stream().forEach(p -> {
        if (shouldRun) {
          if (runs.size() == 1) {
            genetics.clear();
          }
          solve(p, runs.size() > 1 && loggingEnabled ? false : loggingEnabled);
          genetics.onSolved(p);
          if (runs.size() == 1) {
            p.addPlots();
          }
        }
      });
      genetics.onSolved(runs);
    }).start();
  }

  public static void kill() {
    shouldRun = false;
  }

  public static void enableLogging(boolean enableLogging) {
    GeneticEngine.enableLogging = enableLogging;
  }
}
