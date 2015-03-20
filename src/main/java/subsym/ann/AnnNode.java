package subsym.ann;


import java.util.Random;

/**
 * Created by anon on 20.03.2015.
 */
public class AnnNode {

  private AnnNodes outputs;
  private final Double value;
  private AnnNodes inputs;
  private ActivationFunction activationFunction;
  private Double weight;
  private Random random = new Random();

  private AnnNode(Double value, Random random) {
    this.value = value;
    this.random = random;
    inputs = new AnnNodes();
    outputs = new AnnNodes();
    weight = random.nextDouble();
  }

  public static AnnNode create(Double value) {
    return new AnnNode(value, new Random());
  }

  public static AnnNode create(Double value, long seed) {
    return new AnnNode(value, new Random(seed));
  }

  public void connect(AnnNodes outputs) {
    outputs.stream().forEach(this::connect);
  }

  private void connect(AnnNode annNode) {
    outputs.add(annNode);
    annNode.addInput(this);
  }

  private void addInput(AnnNode annNode) {
    inputs.add(annNode);
  }

  public void setActivationFunction(ActivationFunction activationFunction) {
    this.activationFunction = activationFunction;
  }

  public boolean shouldFire() {
    double inputSum = inputs.stream().mapToDouble(n -> n.value * n.weight).reduce((n1, n2) -> n1 + n2).getAsDouble();
    return activationFunction.evaluate(inputSum);
  }

  public void setRandomWeights() {
    weight = random.nextDouble();
    outputs.setRandomWeights();
  }
}
