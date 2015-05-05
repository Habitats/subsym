package subsym.ailife;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import subsym.Main;
import subsym.ailife.entity.Empty;
import subsym.ailife.entity.Food;
import subsym.ailife.entity.Poison;
import subsym.ailife.entity.Robot;
import subsym.gui.Direction;
import subsym.models.Board;
import subsym.models.Vec;
import subsym.models.entity.TileEntity;
import subsym.q.QAction;
import subsym.q.QGame;
import subsym.q.QLearningEngine;
import subsym.q.QState;

/**
 * Created by mail on 04.05.2015.
 */
public class AiLifeReinforcementSimulator implements AiLifeSimulator, QGame<AiLifeReinforcementSimulator.AiLifeState> {

  private static final String TAG = AiLifeReinforcementSimulator.class.getSimpleName();
  private AiLifeGui gui;
  private Map<AiLifeState, Map<QAction, Double>> qMap;
  private Board<TileEntity> board;
  private int startX;
  private int startY;
  private Robot robot;
  private int numFood;
  private List<List<Integer>> content;
  private Map<QAction, Direction> actions;

  public AiLifeReinforcementSimulator() {
    board = fromFile("1-simple.txt");
//    board = fromFile("2-still-simple.txt");
//    board = fromFile("3-dont-be-greedy.txt");
//    board = fromFile("4-big-one.txt");
//    board = fromFile("5-even-bigger.txt");

    actions = Arrays.asList(Direction.values()).stream() //
        .collect(Collectors.toMap(dir -> QAction.create(dir.name()), Function.identity()));
    qMap = QLearningEngine.learn(100, this);

    board = initBoard(this.board.getWidth(), this.board.getHeight(), content);
    gui = new AiLifeGui(board, this, robot);
    gui.setAdapter(board);
//    gui.simulate(() -> Log.v(TAG, "Yolo"));

    drawBestActions();
  }

  private void drawBestActions() {
    board.getItems().forEach(i -> i.setDescription(""));
    Set<AiLifeState> states = qMap.keySet();
    AiLifeState currentState = new AiLifeState(board);
    // states with the same food config
    List<AiLifeState> matchingStates = getStatesMatchingFood(states, currentState);

    // find the best action for any given state
    Map<AiLifeState, QAction> bestActions = matchingStates.stream() //
        .collect(Collectors.toMap(s -> s, s -> getBestAction(qMap.get(s))));
    bestActions.keySet().forEach(s -> board.get(s.getRobotLocation()).setDescription(bestActions.get(s).toString()));
//    gui.setAdapter(board);
    board.notifyDataChanged();
//    AiLifeGui.demo(board, robot);
  }

  public Board<TileEntity> fromFile(String fileName) {
    try {
      Path path = FileSystems.getDefault().getPath("q", fileName);
      content = Files.readAllLines(path).stream()//
          .map(strLst -> Arrays.asList(strLst.split("\\s")).stream() //
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
            robot = new Robot(x, y, board, false);
            tile = robot;
            break;
          default:
            tile = new Food(x, y, board);
        }
        board.set(tile);
      }
    }

    return board;
  }

  public List<QAction> getActions() {
    return new ArrayList<>(actions.keySet());
  }

  @Override
  public void move(Robot robot) {
    AiLifeState state = computeState();
    QAction bestAction;
    if (qMap.containsKey(state)) {
      Map<QAction, Double> actions = qMap.get(state);
      bestAction = Collections.max(actions.keySet(), (a1, a2) -> Double.compare(actions.get(a1), actions.get(a2)));
    } else {
      bestAction = getActions().get(Main.random().nextInt(getActions().size()));
//      return;
    }
    execute(bestAction);
  }

  @Override
  public int getMaxSteps() {
    return Integer.MAX_VALUE;
  }

  @Override
  public void restart() {
    board = initBoard(board.getWidth(), board.getHeight(), content);
  }

  @Override
  public boolean solution() {
    return robot.getFoodCount() == numFood && robot.getX() == startX && robot.getY() == startY;
  }

  @Override
  public void execute(QAction a) {
    robot.move(actions.get(a).getId());
  }

  @Override
  public AiLifeState computeState() {
    return new AiLifeState(board);
  }

  @Override
  public double getReward() {
//    return 1. / (1 + robot.getTravelDistance()) +
    return robot.getFoodCount() * 10 - robot.getPoisonCount() * 2 - robot.getTravelDistance();
  }

  @Override
  public void iterationDone(Map<AiLifeState, Map<QAction, Double>> map) {
//    Map<QAction, Double> state = map.get(board.getId());
  }

  private QAction getBestAction(Map<QAction, Double> actionMap) {
    return actionMap.keySet().stream().max((o1, o2) -> Double.compare(actionMap.get(o1), actionMap.get(o2))).get();
  }

  private List<AiLifeState> getStatesMatchingFood(Set<AiLifeState> states, AiLifeState currentState) {
    List<Vec> foodLocations = currentState.getFoodLocations();
    return states.stream().filter(state -> state.getFoodLocations().containsAll(foodLocations)  //
                                           && foodLocations.containsAll(state.getFoodLocations()))
        .collect(Collectors.toList());
  }

  @Override
  public void updateGui() {
    drawBestActions();
  }

  public static class AiLifeState implements QState {

    private final String id;
    private final List<Vec> foodLocations;
    private final Vec robotLocation;

    public AiLifeState(Board<TileEntity> board) {
      id = board.getId();
      foodLocations = board.getItems().stream() //
          .filter(i -> i instanceof Food).map(food -> Vec.create(food.getX(), food.getY()))
          .collect(Collectors.toList());
      robotLocation = board.getItems().stream() //
          .filter(i -> i instanceof Robot).map(robot -> Vec.create(robot.getX(), robot.getY())).findFirst().get();
    }

    @Override
    public int hashCode() {
      return id.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
      return id.equals(obj.toString());
    }

    @Override
    public String toString() {
      return id;
    }

    public List<Vec> getFoodLocations() {
      return foodLocations;
    }

    public Vec getRobotLocation() {
      return robotLocation;
    }
  }
}
