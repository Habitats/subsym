package subsym.flatland;

import java.util.List;

import subsym.Log;
import subsym.Main;
import subsym.flatland.entity.Food;
import subsym.flatland.entity.Poison;
import subsym.flatland.entity.Robot;
import subsym.gui.Direction;
import subsym.models.Board;
import subsym.models.entity.TileEntity;

/**
 * Created by mail on 11.05.2015.
 */
public class Flatland {

  private static FlatlandGui gui;
  private static final String TAG = Flatland.class.getSimpleName();
  private final FlatlandSimulator simulator;
  private Board<TileEntity> board;
  private boolean shouldStop = false;
  private Robot robot;
  private final boolean showGui;
  private int numFood;
  private int numPoison;

  public Flatland(Board<TileEntity> board, FlatlandSimulator simulator, Robot robot, boolean showGui) {
    this.board = board;
    this.simulator = simulator;
    this.robot = robot;
    this.showGui = showGui;
    if (showGui) {
      setGui();
    }
  }

  private void setGui() {
    gui = FlatlandGui.get(this);
    gui.setAdapter(board);
    gui.setVisible(showGui);
  }

  public void setBoard(Board<TileEntity> board) {
    this.board = board;
    if (showGui) {
      gui.setVisible(true);
      gui.setAdapter(board);
    }
  }

  public static void simulate(List<Board<TileEntity>> boards, FlatlandSimulator ann, Runnable callback, Robot robot) {
    Flatland flatland = new Flatland(boards.get(0), ann, robot, true);
    simulate(boards, callback, flatland);
  }

  private static void simulate(List<Board<TileEntity>> boards, Runnable callback, Flatland flatland) {
    if (boards.isEmpty()) {
      callback.run();
      return;
    }
    flatland.board = boards.remove(0);
    flatland.simulate(() -> simulate(boards, callback, flatland));
  }

  private void initBoard(Board<TileEntity> board) {
    numFood = (int) board.getItems().stream().filter(i -> i instanceof Food).count();
    numPoison = (int) board.getItems().stream().filter(i -> i instanceof Poison).count();
    board.set(robot);
    board.notifyDataChanged();
    if (showGui) {
      gui.onTick(0);
      gui.setAdapter(board);
    }
  }

  public void terminate() {
    this.shouldStop = true;
  }

  public void simulate() {
    simulate(() -> Log.v(TAG, "Simulation done!"));
  }

  public void printBoard() {
    Log.v(TAG, board.getFormattedBoard());
  }

  public void generateRandomBoard() {
    int seed = Main.random().nextInt();
    Log.v(TAG, "Board seed: " + seed);
    board = FlatlandAnnSimulator.createAiLifeBoard(seed);
    initBoard(board);
    Log.v(TAG, board.getFormattedBoard());
    gui.onTick(0);
  }

  public void simulate(Runnable callback) {
    new Thread(() -> {
      initBoard(board);
      for (int i = 1; i <= simulator.getMaxSteps(); i++) {
        simulator.move(robot);
        onTick(i);
      }
      callback.run();
    }).start();
  }

  public void onTick(int step) {
    simulator.onTick();
    if (showGui) {
      gui.onTick(step);
      try {
        Thread.sleep(gui.getSimulationSpeed());
      } catch (InterruptedException e) {
      }
      if (shouldStop) {
//          Log.v(TAG, "Exiting ...");
        return;
      }
    }
  }

  public void reset() {
    simulator.reset();
  }

  public int getMaxFoodCount() {
    return numFood;
  }

  public int getMaxPoisonCount() {
    return numPoison;
  }

  public boolean isVisible() {
    return showGui;
  }

  //########### ROBOT STUFF ################

  public int getFoodCount() {
    return robot.getFoodCount();
  }

  public int getPoisonCount() {
    return robot.getPoisonCount();
  }

  public double getScore() {
    return robot.getScore();
  }

  public void move(Direction dir) {
    robot.move(dir);
  }
}
