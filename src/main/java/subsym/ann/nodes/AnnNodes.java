package subsym.ann.nodes;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Created by anon on 20.03.2015.
 */
public class AnnNodes {

  private final List<AnnNode> values;

  public AnnNodes(List<AnnNode> values) {
    this.values = values;
  }

  public static AnnNodes createOutput(int numberOfNodes) {
    return new AnnNodes(IntStream.range(0, numberOfNodes).mapToObj(i -> AnnNode.createOutput()).collect(Collectors.toList()));
  }

  public static AnnNodes createHidden(int numberOfNodes) {
    return new AnnNodes(IntStream.range(0, numberOfNodes).mapToObj(i -> AnnNode.createHidden()).collect(Collectors.toList()));
  }

  public static AnnNodes createInput(Double... values) {
    return createInput(Arrays.asList(values));
  }

  public static AnnNodes createInput(List<Double> values) {
    return new AnnNodes(values.stream().map(AnnNode::createInput).collect(Collectors.toList()));
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
    return values.stream().map(AnnNode::toString).collect(Collectors.joining(" --- ", "", ""));
  }


}
