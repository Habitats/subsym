package subsym.ann;

import subsym.ann.activation.ActivationFunction;
import subsym.ann.activation.Sigmoid;
import subsym.beertracker.BeerScenario;

/**
 * Created by anon on 24.03.2015.
 */
public class AnnPreferences {

  private int hiddenLayerCount;
  private int hiddenNeuronCount;
  private ActivationFunction activationFunction;
  private boolean dynamic;
  private boolean single;
  private BeerScenario beerScenario;

  public AnnPreferences(int hiddenLayerCount, int hiddenNeuronCount, ActivationFunction activationFunction, BeerScenario beerScenario) {
    this.hiddenLayerCount = hiddenLayerCount;
    this.hiddenNeuronCount = hiddenNeuronCount;
    this.activationFunction = activationFunction;
    this.beerScenario = beerScenario;
  }

  public static AnnPreferences getAiLifeDefault() {
    return new AnnPreferences(0, 3, new Sigmoid(), BeerScenario.WRAP);
  }

  public static AnnPreferences getBeerDefault() {
    return new AnnPreferences(1, 2, new Sigmoid(), BeerScenario.WRAP);
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

  public boolean isSingle() {
    return single;
  }

  public void setSingle(boolean single) {
    this.single = single;
  }

  public boolean isDynamic() {
    return dynamic;
  }

  public void setDynamic(boolean dynamic) {
    this.dynamic = dynamic;
  }

  public BeerScenario getBeerScenario() {
    return beerScenario;
  }

  public void setBeerScenario(BeerScenario beerScenario) {
    this.beerScenario = beerScenario;
  }
}
