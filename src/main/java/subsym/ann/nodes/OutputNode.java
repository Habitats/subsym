package subsym.ann.nodes;

import java.util.Random;
import java.util.stream.Collectors;

/**
 * Created by anon on 24.03.2015.
 */
public class OutputNode extends AnnNode {


  protected OutputNode(Random random) {
    super(random);
  }

  protected double getInputSum() {
    return inputs.stream().mapToDouble(n -> n.getValue() * inputWeights.get(n)).sum();
  }

  public double getValue() {
    currentValue = activationFunction.evaluate(getInputSum());
    return currentValue;
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
