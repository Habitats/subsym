package subsym.ann;

import subsym.ann.nodes.AnnNodes;

/**
 * Created by anon on 24.03.2015.
 */
public class AnnPreferences {

  private int hiddenLayerCount;
  private int hiddenNeuronCount;
  private ActivationFunction activationFunction;
  private AnnNodes outputs;
  private AnnNodes inputs;

  public AnnPreferences(int hiddenLayerCount, int hiddenNeuronCount, AnnNodes inputs, AnnNodes outputs,
                        ActivationFunction activationFunction) {
    this.hiddenLayerCount = hiddenLayerCount;
    this.hiddenNeuronCount = hiddenNeuronCount;
    this.inputs = inputs;
    this.outputs = outputs;
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

  public AnnNodes getOutputs() {
    return outputs;
  }

  public void setOutputs(AnnNodes outputs) {
    this.outputs = outputs;
  }

  public AnnNodes getInputs() {
    return inputs;
  }

  public void setInputs(AnnNodes inputs) {
    this.inputs = inputs;
  }

  public static AnnPreferences getDefault() {
    AnnNodes inputs = AnnNodes.createInput(0., 0., 0., 0., 0., 0.);
    AnnNodes outputs = AnnNodes.createOutput(3);
    return new AnnPreferences(1, 3, inputs, outputs, new Sigmoid());
  }
}
