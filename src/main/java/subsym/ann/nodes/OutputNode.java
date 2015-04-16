package subsym.ann.nodes;

import java.util.Random;

import subsym.ann.WeightBound;

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
    double deltaState = -y + getInputSum();
    return deltaState / t;
  }

  public double getTimeConstant() {
    return t;
  }

  @Override
  public String toString() {
    double exp = hasState() ? getInternalState() * getGain() : getInputSum();
    boolean includeWeights = true;
    return super.toString() + String
        .format("S = %6.3f > O = %.3f > G = %.3f > T = %.3f > Y = %6.3f > dY = %6.3f " + (includeWeights ? getFormattedWeights() : ""),//
                getInputSum(), getValue(), getGain(), getTimeConstant(), getInternalState(), getInternalChange());
  }

  public void resetInternalState() {
    y = 0;
  }
}
