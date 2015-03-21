package subsym.ailife;

import subsym.Log;
import subsym.ann.AnnNodes;
import subsym.ann.ArtificialNeuralNetwork;
import subsym.ann.Sigmoid;

/**
 * Created by anon on 20.03.2015.
 */
public class AiLife {

  private static final String TAG = AiLife.class.getSimpleName();

  public AiLife() {
    AnnNodes inputs = AnnNodes.createInput(0.3, 0.1, 0.7);
    AnnNodes outputs = AnnNodes.createOutput(2);
    ArtificialNeuralNetwork ann = new ArtificialNeuralNetwork(1, 4, inputs, outputs, new Sigmoid());

    Log.v(TAG, ann);
    ann.setRandomWeights();
    Log.v(TAG, ann);
    ann.setRandomWeights();
    Log.v(TAG, ann);
    ann.setRandomWeights();
    Log.v(TAG, ann);
  }
}
