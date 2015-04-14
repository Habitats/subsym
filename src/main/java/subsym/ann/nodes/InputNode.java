package subsym.ann.nodes;

import java.util.Random;

import subsym.ann.WeightBound;

/**
 * Created by anon on 24.03.2015.
 */
public class InputNode extends AnnNode {

  private Double value;

  protected InputNode(Double value, Random random, WeightBound bound) {
    super(random, bound);
    this.value = value;
  }

  @Override
  public String toString() {
    String weights = getFormattedWeights();
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
