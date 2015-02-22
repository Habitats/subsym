package subsym.ga;

import java.util.stream.IntStream;

import subsym.onemax.OneMax;

/**
 * Created by anon on 21.02.2015.
 */
public class GeneticEngine {

  private static final String TAG = GeneticEngine.class.getSimpleName();

  public static GeneticProblem solve(GeneticProblem problem) {
    problem.initPopulation();
    while (!problem.solution()) {
      problem.select();
      problem.crossOver();
      problem.cleanUp();
      problem.mutate();
      problem.log();
    }
    return problem;
  }

  public static double solve(OneMax oneMax, int rounds) {
    return IntStream.range(0, rounds).map(i -> solve(oneMax).generations()).average().getAsDouble();
  }
}
