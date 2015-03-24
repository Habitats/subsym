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
    return inputs.stream().mapToDouble(n -> n.getValue() * n.outputWeights.get(this)).reduce((n1, n2) -> n1 + n2).getAsDouble();
  }

  public double getValue() {
    return activationFunction.evaluate(getInputSum());
  }

  @Override
  public String toString() {
    String weights = "";
    if (outputs.size() > 0) {
      weights = outputs.stream().map(n -> String.format("%.3f", outputWeights.get(n))).collect(Collectors.joining(", ", "- W: ", ""));
    }
    return String.format("S: %.3f - O: %.3f %s", getInputSum(), getValue(), weights);
  }
}
