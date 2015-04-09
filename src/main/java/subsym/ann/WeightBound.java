package subsym.ann;

/**
 * Created by anon on 09.04.2015.
 */
public class WeightBound {

  private final double lower;
  private final double upper;

  public WeightBound(double lower, double upper) {
    this.lower = lower;
    this.upper = upper;
  }

  public double fromNormal(double weight) {
    return (weight * (upper - lower)) + lower;
  }
}
