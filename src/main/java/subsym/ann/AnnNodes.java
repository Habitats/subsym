package subsym.ann;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Created by anon on 20.03.2015.
 */
public class AnnNodes {

  private List<AnnNode> values;

  private AnnNodes(Double... values) {
    this.values = Arrays.asList(values).stream().map(AnnNode::create).collect(Collectors.toList());
  }

  private AnnNodes(int numberOfNodes) {
    this.values = IntStream.range(0, numberOfNodes).mapToObj(i -> AnnNode.create()).collect(Collectors.toList());
  }

  public static AnnNodes createOutput(int numberOfNodes) {
    return new AnnNodes(numberOfNodes);
  }

  public static AnnNodes createInput(Double... values) {
    return new AnnNodes(values);
  }
  public static AnnNodes createEmpty() {
    return new AnnNodes();
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
