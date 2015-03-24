import org.junit.Test;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import subsym.ailife.AiLife;
import subsym.ailife.AiLifeRobot;
import subsym.ann.nodes.AnnNodes;
import subsym.ann.ArtificialNeuralNetwork;
import subsym.ann.Sigmoid;
import subsym.gui.AIGridCanvas;
import subsym.models.Board;
import subsym.models.TileEntity;
import subsym.models.Vec;

import static org.junit.Assert.assertEquals;

/**
 * Created by anon on 24.03.2015.
 */
public class test_ailife {

  private static final String TAG = test_ailife.class.getSimpleName();
  private Random random = new Random(0);

  @Test
  public void test_board() {
    Board<TileEntity> board = new Board<>(5, 5);
    IntStream.range(0, 5).forEach(x -> IntStream.range(0, 5).forEach(y -> board.set(new AiLife.Empty(x, y, board))));
    board.set(new AiLife.Poison(1, 1, board));
    board.set(new AiLife.Poison(2, 2, board));
    board.set(new AiLife.Poison(2, 3, board));
    board.set(new AiLife.Poison(1, 3, board));
    board.set(new AiLife.Food(0, 2, board));
    board.set(new AiLife.Food(1, 2, board));
    AiLifeRobot robot = new AiLifeRobot(0, 0, board);
    board.set(robot);

    assertEquals(robot.getFoodSensorInput(), Arrays.asList(0, 0, 0));
    robot.move(1);
    assertEquals(robot.getFoodSensorInput(), Arrays.asList(0, 1, 0));
    assertEquals(robot.getPoisonSensorInput(), Arrays.asList(0, 0, 1));
    assertEquals(robot.getPosition(), Vec.create(0, 1));
    robot.move(1);
    assertEquals(robot.getFoodSensorInput(), Arrays.asList(0, 0, 1));
    assertEquals(robot.getPoisonSensorInput(), Arrays.asList(0, 0, 0));
    assertEquals(robot.getPosition(), Vec.create(0, 2));
    robot.move(2);
    assertEquals(robot.getFoodSensorInput(), Arrays.asList(0, 0, 0));
    assertEquals(robot.getPoisonSensorInput(), Arrays.asList(1, 1, 1));
    assertEquals(robot.getPosition(), Vec.create(1, 2));
    robot.move(1);
    assertEquals(robot.getPosition(), Vec.create(2, 2));
    robot.move(0);
    assertEquals(robot.getPosition(), Vec.create(2, 3));
    robot.move(0);
    assertEquals(robot.getPosition(), Vec.create(1, 3));

    displayGui(board, robot);
  }

  @Test
  public void test_ann() {
    AnnNodes inputs = AnnNodes.createInput(0.3, 0.1, 0.7);
    assertEquals(inputs.getValues(), Arrays.asList(0.3, 0.1, 0.7));
    AnnNodes outputs = AnnNodes.createOutput(2);
    ArtificialNeuralNetwork ann = new ArtificialNeuralNetwork(1, 2, inputs, outputs, new Sigmoid());
    ann.updateInput(0.8, 0.9, 0.2);
    assertEquals(inputs.getValues(), Arrays.asList(0.8, 0.9, 0.2));
    assertEquals(ann.getInputs(), Arrays.asList(0.8, 0.9, 0.2));
    ann.updateInput(0.0, 0.0, 0.0);
    assertEquals(ann.getInputs(), Arrays.asList(0.0, 0.0, 0.0));
    assertEquals(ann.getOutputs(), Arrays.asList(0.0, 0.0));

    List<Double> testInputs = Arrays.asList(0.7, 0.8, 0.9);
    ann.updateInput(testInputs);
    List<Double> weights = IntStream.range(0, ann.getNumWeights())//
        .mapToDouble(i -> (double) i / ann.getNumWeights()).boxed().collect(Collectors.toList());
//        .mapToDouble(i -> random.nextDouble()).boxed().collect(Collectors.toList());
    ann.setWeights(weights);
    double h1 = (weights.get(0) + weights.get(2) + weights.get(4)) * testInputs.get(0);
    double h2 = (weights.get(1) + weights.get(3) + weights.get(5)) * testInputs.get(1);
    Sigmoid sig = new Sigmoid();
    double o1 = (weights.get(6) + weights.get(8)) * sig.evaluate(h1);
    double o2 = (weights.get(7) + weights.get(9)) * sig.evaluate(h2);

    double out1 = sig.evaluate(o1);
    double out2 = sig.evaluate(o2);
    assertEquals(Arrays.asList(out1, out2), ann.getOutputs());
  }

  private void displayGui(Board<TileEntity> board, final AiLifeRobot robot) {
    AIGridCanvas<TileEntity> canvas = new AIGridCanvas<>();
    canvas.setAdapter(board);
    canvas.addKeyListener(new KeyAdapter() {
      @Override
      public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
          case KeyEvent.VK_A:
            robot.move(0);
            break;
          case KeyEvent.VK_W:
            robot.move(1);
            break;
          case KeyEvent.VK_D:
            robot.move(2);
            break;
        }
      }
    });

    canvas.requestFocus();
    AiLife.displayGui(canvas);
  }
}