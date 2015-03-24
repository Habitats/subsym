package subsym.ann.nodes;

import java.util.Random;

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
    if (outputs.size() > 0) {
//      weights = outputs.stream().map(n -> String.format("%.3f", outputWeights.get(n))).collect(Collectors.joining(", ", "- W: ", ""));
    }
    return String.format("V: %.3f %s", getValue(), weights);
  }

  @Override
  public double getValue() {
    return value;
  }

  public void setValue(Double value) {
    this.value = value;
  }
}
