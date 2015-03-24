package subsym.ann;

/**
 * Created by anon on 24.03.2015.
 */
public class AnnPreferences {

  private int hiddenLayerCount;
  private int hiddenNeuronCount;
  private ActivationFunction activationFunction;

  public AnnPreferences(int hiddenLayerCount, int hiddenNeuronCount, ActivationFunction activationFunction) {
    this.hiddenLayerCount = hiddenLayerCount;
    this.hiddenNeuronCount = hiddenNeuronCount;
    this.activationFunction = activationFunction;
  }

  public int getHiddenLayerCount() {
    return hiddenLayerCount;
  }

  public void setHiddenLayerCount(int hiddenLayerCount) {
    this.hiddenLayerCount = hiddenLayerCount;
  }

  public int getHiddenNeuronCount() {
    return hiddenNeuronCount;
  }

  public void setHiddenNeuronCount(int hiddenNeuronCount) {
    this.hiddenNeuronCount = hiddenNeuronCount;
  }

  public ActivationFunction getActivationFunction() {
    return activationFunction;
  }

  public void setActivationFunction(ActivationFunction activationFunction) {
    this.activationFunction = activationFunction;
  }

  public static AnnPreferences getDefault() {
    return new AnnPreferences(1, 6, new Sigmoid());
  }
}
