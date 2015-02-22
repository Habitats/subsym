package subsym.ga;

import subsym.Log;

/**
 * Created by anon on 21.02.2015.
 */
public class GA {

  private static final String TAG = GA.class.getSimpleName();

  public static GeneticProblem solve(GeneticProblem problem) {
    problem.initPopulation();
    int gen = 0;
    while (!problem.solution()) {
      problem.evaluate();
      problem.select();
      problem.crossOver();
      problem.cleanUp();
      problem.mutate();
      gen++;
    }
    Log.v(TAG, "Generation:" + gen);
    return problem;
  }
}
