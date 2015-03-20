package subsym.ann;

/**
 * Created by anon on 20.03.2015.
 */
public class Sigmoid implements ActivationFunction {

  private double fireThreshold = 0.9;

  @Override
  public boolean evaluate(double inputSum) {
    return 1 / (1 + Math.exp(-inputSum)) > fireThreshold;
  }
}
