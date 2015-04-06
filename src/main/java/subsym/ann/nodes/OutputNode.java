package subsym.ann.nodes;

import java.util.Random;
import java.util.stream.Collectors;

/**
 * Created by anon on 24.03.2015.
 */
public class OutputNode extends AnnNode {

  private double y = 0;
  private double t = 1;
  private double gain = 1;

  protected OutputNode(Random random) {
    super(random);
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
  public void incrementTime() {
    super.incrementTime();
    y += getInternalChange();
  }

  @Override
  public String toString() {
    String weights = "";
    if (inputs.size() > 0) {
      weights = inputs.stream() //
          .map(n -> String.format("(%s - %.3f)", n.getId(), inputWeights.get(n))).collect(Collectors.joining(", ", " > W = [", "]"));
    }
    return super.toString() + String.format("S = %.3f > O = %.3f %s", getInputSum(), getValue(), weights);
  }
}
