package subsym.ga;

/**
 * Created by anon on 21.02.2015.
 */
public abstract class GeneticProblem {

  public abstract int generations();

  public enum AdultSelection {
    FULL_TURNOVER, OVER_PRODUCTION, MIXING
  }

  public enum MateSelection {
    FITNESS_PROPORTIONATE, SIGMA_SCALING, TOURNAMENT, UNKNOWN
  }

  public abstract boolean solution();

  public void cleanUp() {
    getPopulation().cleanUp();
  }

  protected abstract Population getPopulation();

  public abstract void initPopulation();

  public abstract void select();

  public abstract void crossOver();

  public abstract void mutate();

}
