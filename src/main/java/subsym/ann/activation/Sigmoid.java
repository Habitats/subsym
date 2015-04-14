package subsym.ann.activation;

/**
 * Created by anon on 20.03.2015.
 */
public class Sigmoid implements ActivationFunction {

  @Override
  public double evaluate(double exp) {
    return 1 / (1 + Math.exp(-exp));
  }
}
