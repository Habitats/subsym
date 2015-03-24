package subsym.ann;

/**
 * Created by anon on 20.03.2015.
 */
public class Sigmoid implements ActivationFunction {

  private double strenght = 10;

  @Override
  public double evaluate(double inputSum) {
    return 1 / (1 + Math.exp(-inputSum * strenght));
  }
}
