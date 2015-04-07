package subsym.ann.activation;

/**
 * Created by anon on 06.04.2015.
 */
public class Linear implements ActivationFunction {

  @Override
  public double evaluate(double inputSum) {
    return inputSum;
  }
}
