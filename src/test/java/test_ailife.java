import org.junit.Test;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.stream.IntStream;

import subsym.ailife.AiLife;
import subsym.ailife.AiLifeRobot;
import subsym.gui.AIGridCanvas;
import subsym.models.Board;
import subsym.models.TileEntity;
import subsym.models.Vec;

import static org.junit.Assert.assertEquals;

/**
 * Created by anon on 24.03.2015.
 */
public class test_ailife {

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
    System.out.println("asd");
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
