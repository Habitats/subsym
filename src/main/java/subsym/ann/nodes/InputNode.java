package subsym.ann.nodes;

import java.util.Random;
import java.util.stream.Collectors;

/**
 * Created by anon on 24.03.2015.
 */
public class InputNode extends AnnNode {

  private Double value;

  protected InputNode(Double value, Random random) {
    super(random);
    this.value = value;
  }

  @Override
  public String toString() {
    String weights = "";
    if (inputs.size() > 0) {
      weights = inputs.stream()//
          .map(n -> String.format("(%s - %.3f)", n.getId(), inputWeights.get(n))).collect(Collectors.joining(", ", " > W = [", "]"));
    }
    return super.toString() + String.format("V = %.3f %s", getValue(), weights);
  }

  @Override
  public double getValue() {
    return value;
  }

  public void setValue(Double value) {
    this.value = value;
  }
}
