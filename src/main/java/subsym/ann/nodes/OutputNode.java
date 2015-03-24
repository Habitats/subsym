package subsym.ann.nodes;

import java.util.Random;

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
}
