package subsym.ann.nodes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import subsym.ann.ArtificialNeuralNetwork;
import subsym.ann.WeightBound;

/**
 * Created by anon on 20.03.2015.
 */
public class AnnNodes implements Comparable<AnnNodes>,Iterable<AnnNode> {

  private final List<AnnNode> values;
  private final int id;

  public AnnNodes(List<AnnNode> values) {
    this.values = values;
    id = ArtificialNeuralNetwork.nextGlobalId();
  }

  public AnnNodes(AnnNode... values) {
    this(new ArrayList<>(Arrays.asList(values)));
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
    return values.stream().map(AnnNode::toString).collect(Collectors.joining("],\n   [", " > [", "]"));
  }

  @Override
  public int compareTo(AnnNodes o) {
    return Integer.compare(id, o.id);
  }

  @Override
  public Iterator<AnnNode> iterator() {
    return values.iterator();
  }
}
