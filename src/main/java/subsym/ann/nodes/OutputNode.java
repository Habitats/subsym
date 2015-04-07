package subsym.ann.nodes;

import java.util.Random;

/**
 * Created by anon on 24.03.2015.
 */
public class OutputNode extends AnnNode {

  private double y = 0;
  private double t = 1;
  private double gain = 1;

  protected OutputNode(Random random, WeightBound bound) {
    super(random, bound);
  }

  @Override
  public void incrementTime() {
    super.incrementTime();
    y += getInternalChange();
  }

  protected double getInputSum() {
    return inputs.stream().mapToDouble(n -> n.getValue() * inputWeights.get(n)).sum();
  }

  public double getValue() {
    currentValue = activationFunction.evaluate(hasState() ? getInternalState() * getGain() : getInputSum());
    return currentValue;
  }

  private double getGain() {
    return gain;
  }

  public void setGain(double gain) {
    this.gain = gain;
  }

  public void setTimeConstant(double t) {
    this.t = t;
  }

  private double getInternalState() {
    return y;
  }

  private double getInternalChange() {
    return (-y + getInputSum()) / t;
  }

  @Override
  public String toString() {
    return super.toString() + String.format("S = %.3f > O = %.3f %s", getInputSum(), getValue(), getFormattedWeights());
  }
}
