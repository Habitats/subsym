package subsym.ann;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by anon on 20.03.2015.
 */
public class AnnNodes {

  private List<AnnNode> values;

  public AnnNodes(Double... values) {
    this.values = Arrays.asList(values).stream().map(AnnNode::create).collect(Collectors.toList());
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
}
