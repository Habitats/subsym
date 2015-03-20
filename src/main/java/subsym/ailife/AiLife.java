package subsym.ailife;

import subsym.ann.AnnNodes;
import subsym.ann.ArtificialNeuralNetwork;
import subsym.ann.Sigmoid;

/**
 * Created by anon on 20.03.2015.
 */
public class AiLife {

  public AiLife() {
    ArtificialNeuralNetwork ann = new ArtificialNeuralNetwork(0, 0, new AnnNodes(0.3, 0.1, 0.4, 0.2), new AnnNodes(0.1, .2), new Sigmoid());
    ann.setRandomWeights();
  }

}
