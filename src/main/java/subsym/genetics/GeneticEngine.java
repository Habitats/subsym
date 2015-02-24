package subsym.genetics;

import java.util.stream.IntStream;

import subsym.Log;
import subsym.onemax.OneMax;

/**
 * Created by anon on 21.02.2015.
 */
public class GeneticEngine {

  private static final String TAG = GeneticEngine.class.getSimpleName();

  public static GeneticProblem solve(GeneticProblem problem, boolean loggingEnabled) {
    problem.initPopulation();
    int count = 0;
    long start = System.currentTimeMillis();
    while (!problem.solution()) {
      problem.select();
      problem.crossOver();
      problem.cleanUp();
      problem.mutate();
      if (loggingEnabled) {
        problem.log();
      }
      count++;
      if (count % 1000 == 0) {
        Log.v(TAG, "Gen ... " + count);
      }
    }
    if (loggingEnabled) {
      Log.v(TAG, "Search took: " + (System.currentTimeMillis() - start) / 1000. + " s");
    }
    return problem;
  }

  public static double solve(OneMax oneMax, int rounds) {
    return IntStream.range(0, rounds).map(i -> solve(oneMax,false).generations()).average().getAsDouble();
  }
}
