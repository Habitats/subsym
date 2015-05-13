package subsym.flatland;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.BitSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import subsym.Log;
import subsym.Main;
import subsym.flatland.entity.Empty;
import subsym.flatland.entity.Food;
import subsym.flatland.entity.Poison;
import subsym.flatland.entity.Robot;
import subsym.gui.Direction;
import subsym.models.Board;
import subsym.models.Vec;
import subsym.models.entity.TileEntity;
import subsym.q.QPreferences;

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
  private boolean showGui;
  private int numPoison;
  private int startX;
  private int startY;
  private int numFood;
  private List<List<Integer>> content;

  public Flatland(FlatlandSimulator simulator, boolean showGui) {
    this.simulator = simulator;
    this.showGui = showGui;

    if (showGui) {
      setGui();
    }
  }

  public void loadFromFile(String fileName) {
    board = readBoardFromFile(fileName);
  }

  private Board<TileEntity> readBoardFromFile(String fileName) {
    try {
      Path path = FileSystems.getDefault().getPath(QPreferences.PATH, fileName);
      content = Files.readAllLines(path).stream()//
          .map(strLst -> Arrays.asList(strLst.trim().split("\\s+")).stream() //
              .mapToInt(Integer::parseInt).boxed() //
              .collect(Collectors.toList())).collect(Collectors.toList());
      List<Integer> specs = content.remove(0);
      int width = specs.get(0);
      int height = specs.get(1);
      startX = specs.get(2);
      startY = specs.get(3);
      numFood = specs.get(4);
      return initBoard(width, height, content);
    } catch (IOException e) {
      throw new IllegalStateException("Unable to read AiLife map from file!");
    }
  }

  private Board<TileEntity> initBoard(int width, int height, List<List<Integer>> content) {
    Board<TileEntity> board = new Board<>(width, height);

    Map<TileEntity, Integer> foods = new HashMap<>();
    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        int s = content.get(y).get(x);
        TileEntity tile;
        switch (s) {
          case 0:
            tile = new Empty(x, y, board);
            break;
          case -1:
            tile = new Poison(x, y, board);
            break;
          case -2:
            robot = new Robot(x, y, board, false, this);
            tile = robot;
            break;
          default:
            tile = new Food(x, y, board);
            foods.put(tile, s - 1);
        }
        board.set(tile);
      }
    }
    robot.init(foods);

    return board;
  }

  private void setGui() {
    gui = FlatlandGui.get(this);
  }

  public void setBoard(Board<TileEntity> board) {
    this.board = board;
    if (showGui) {
      gui.setAdapter(board);
      gui.setVisible(showGui);
    }
  }

  public int simulate(boolean showGui, Runnable callback) {
    shouldStop = false;
    reset();
    this.showGui = showGui;
    initBoard(board);
    setBoard(board);
    for (int i = 1; i <= simulator.getMaxSteps(); i++) {
      simulator.move(robot);
      onSimulationTick(i);
      if (shouldStop || QPreferences.SHOULD_TERMINATE) {
//          Log.v(TAG, "Terminating simulation ...");
        break;
      }
    }
    callback.run();
    return getTravelDistance();
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

  public void onSimulationTick(int step) {
    simulator.onTick();
    if (showGui) {
      gui.onTick(step);
      try {
        Thread.sleep(gui.getSimulationSpeed());
      } catch (InterruptedException e) {
      }
    }
  }

  public void reset() {
    board = initBoard(getWidth(), getHeight(), content);
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

  public int getTravelDistance() {
    return robot.getTravelDistance();
  }

  // ############ BOARD STUFF ################

  public List<TileEntity> getItems() {
    return board.getItems();
  }

  public int getHeight() {
    return board.getHeight();
  }

  public int getWidth() {
    return board.getWidth();
  }

  public boolean robotAtStart() {
    int x = robot.getX();
    int y = robot.getY();
    return x == startX && y == startY;
  }

  public void onPoisonConsumed(TileEntity oldTile) {
    simulator.onPoisonConsumed();
  }

  public void onFoodConsumed(TileEntity oldTile) {
    simulator.onFoodConsumed();
  }

  public void onNormalMove(TileEntity oldTile) {
    simulator.onNormalMove();
  }

  public BitSet getFoodId() {
    return robot.getFoodId();
  }

  public BitSet getRobotId() {
    return robot.getRobotId();
  }

  public TileEntity get(Vec location) {
    return board.get(location);
  }

  public int simulate(boolean b) {
    return simulate(b, () -> {
    });
  }
}
