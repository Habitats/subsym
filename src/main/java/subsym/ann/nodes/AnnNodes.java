package subsym.ann.nodes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import subsym.ann.WeightBound;

/**
 * Created by anon on 20.03.2015.
 */
public class AnnNodes {

  private final List<AnnNode> values;

  public AnnNodes(List<AnnNode> values) {
    this.values = values;
  }

  public AnnNodes(AnnNode... values) {
    this.values = new ArrayList<>(Arrays.asList(values));
  }

  public static AnnNodes createOutput(WeightBound bound, int numberOfNodes) {
    return new AnnNodes(IntStream.range(0, numberOfNodes).mapToObj(i -> AnnNode.createOutput(bound)).collect(Collectors.toList()));
  }

  public static AnnNodes createInput(WeightBound bound, Double... values) {
    return createInput(bound, Arrays.asList(values));
  }

  public static AnnNodes createInput(WeightBound bound, List<Double> values) {
    return new AnnNodes(values.stream().map(value -> AnnNode.createInput(bound, value)).collect(Collectors.toList()));
  }

  public List<Double> getValues() {
    return values.stream().mapToDouble(v -> v.getValue()).boxed().collect(Collectors.toList());
  }

  public Stream<AnnNode> stream() {
    return values.stream();
  }

  public void setRandomWeights() {
    values.stream().forEach(n -> n.setRandomWeights());
  }

  public void add(AnnNode annNode) {
    values.add(annNode);
  }

  public int size() {
    return values.size();
  }

  @Override
  public String toString() {
    return values.stream().map(AnnNode::getId).collect(Collectors.joining("], [", " > [", "]"));
  }

}
