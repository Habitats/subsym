package subsym.ann;

/**
 * Created by anon on 20.03.2015.
 */
public class Sigmoid implements ActivationFunction {

  private double fireThreshold = 0.6;

  @Override
  public double evaluate(double inputSum) {
    double v = 1 / (1 + Math.exp(-inputSum));
    return v > fireThreshold ? v : 0;
  }
}
