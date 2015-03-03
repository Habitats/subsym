package subsym.genetics;

import subsym.Log;

/**
 * Created by anon on 21.02.2015.
 */
public class GeneticEngine {

  private static final String TAG = GeneticEngine.class.getSimpleName();
  private static boolean shouldRun;

  public static void solveInBackground(GeneticProblem problem, boolean loggingEnabled, Genetics genetics) {
    new Thread(() -> {
      solve(problem, loggingEnabled);
      genetics.onSolved(problem);
    }).start();
  }

  public static GeneticProblem solve(GeneticProblem problem, boolean loggingEnabled) {
    shouldRun = true;
    problem.initPopulation();
    int count = 0;
    long start = System.currentTimeMillis();
    while (shouldRun && !problem.solution()) {
      problem.select();
      problem.crossOver();
      problem.mutate();
      problem.cleanUp();
      if (loggingEnabled) {
        problem.log();
      }
      count++;
      if (count % 1000 == 0) {
        Log.i(TAG, "Gen ... " + count);
      }
    }
    if (loggingEnabled) {
      Log.v(TAG, "Search took: " + (System.currentTimeMillis() - start) / 1000. + " s");
    }
    return problem;
  }

  public static void kill() {
    shouldRun = false;
  }
}
