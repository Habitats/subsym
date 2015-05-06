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

import subsym.Log;
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
public class AiLifeQSimulator implements AiLifeSimulator, QGame<AiLifeQSimulator.AiLifeState> {

  private static final String TAG = AiLifeQSimulator.class.getSimpleName();
  private AiLifeGui gui;
  private Map<AiLifeState, Map<QAction, Double>> qMap;
  private Board<TileEntity> board;
  private int startX;
  private int startY;
  private Robot robot;
  private int numFood;
  private List<List<Integer>> content;
  private Map<QAction, Direction> actions;

  public AiLifeQSimulator() {
//    board = fromFile("1-simple.txt");
//    board = fromFile("2-still-simple.txt");
//    board = fromFile("3-dont-be-greedy.txt");
//    board = fromFile("4-big-one.txt");
    board = fromFile("5-even-bigger.txt");

//    gui.simulate(() -> Log.v(TAG, "Yolo"));
    actions = Arrays.asList(Direction.values()).stream() //
        .collect(Collectors.toMap(dir -> QAction.create(dir.name()), Function.identity()));

    board = initBoard(this.board.getWidth(), this.board.getHeight(), content);
    qMap = QLearningEngine.learn(10000, this);

    board = initBoard(this.board.getWidth(), this.board.getHeight(), content);
    gui = new AiLifeGui(board, this, robot);
    gui.simulate(() -> Log.v(TAG, "yolo"));
  }

  @Override
  public void onTick() {
    drawBestActions(qMap);
    if (solution()) {
      gui.terminate();
    }
  }
//
//  private  void simulate() {
//    board = initBoard(board.getWidth(), board.getHeight(), content);
//    gui.setAdapter(board);
//    while (!solution()) {
//      move(robot);
//      drawBestActions(qMap);
//      try {
//        Thread.sleep(gui.getSimulationSpeed());
//      } catch (InterruptedException e) {
//      }
//    }
//  }

  private void drawBestActions(Map<AiLifeState, Map<QAction, Double>> qMap) {
    board.getItems().forEach(i -> i.setDescription(""));
    board.getItems().forEach(i -> i.setDirection(null));
    Set<AiLifeState> states = qMap.keySet();
    AiLifeState currentState = computeState();
    // states with the same food config
    List<AiLifeState> matchingStates = getStatesMatchingFood(states, currentState);

    // find the best action for any given state
    Map<AiLifeState, QAction> bestActions = matchingStates.stream() //
        .collect(Collectors.toMap(s -> s, s -> getBestAction(qMap.get(s))));
    bestActions.keySet().forEach(s -> {
      QAction bestAction = bestActions.get(s);
      Map<QAction, Double> actions = qMap.get(s);
      drawDetailed(s, bestAction, actions);
      board.get(s.getRobotLocation()).setDirection(this.actions.get(bestAction));
    });
    gui.setAdapter(board);
    board.notifyDataChanged();
  }

  private void drawDetailed(AiLifeState s, QAction bestAction, Map<QAction, Double> actions) {
    String actionValues = actions.keySet().stream() //
        .sorted((o1, o2) -> o1.toString().compareTo(o2.toString())) //
        .map(a -> String.format("%s %5.2f", a.toString().charAt(0), actions.get(a))).collect(Collectors.joining("\n", "\n\n", ""));
    board.get(s.getRobotLocation()).setDescription(String.format("%s %s", bestAction.toString(), actionValues));
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

  @Override
  public void reset() {
    board = initBoard(board.getWidth(), board.getHeight(), content);
    gui.setAdapter(board);
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
    Direction id = actions.get(a);
    robot.move(id);
  }

  @Override
  public AiLifeState computeState() {
    List<Integer[]> foodLocations = board.getItems().stream() //
        .filter(i -> i instanceof Food).map(food -> new Integer[]{food.getX(), food.getY()}).collect(Collectors.toList());
    Vec robotLocation = board.getItems().stream() //
        .filter(i -> i instanceof Robot).map(robot -> Vec.create(robot.getX(), robot.getY())).findFirst().get();
    return new AiLifeState(foodLocations, robotLocation);
  }

  @Override
  public double getReward() {
//    return 1. / (1 + robot.getTravelDistance()) +
    return robot.getLastStepReward();
  }

  @Override
  public void onStep(Map<AiLifeState, Map<QAction, Double>> qMap) {
    if (gui != null) {
      drawBestActions(qMap);
      board.notifyDataChanged();
      try {
        Thread.sleep(gui.getSimulationSpeed());
      } catch (InterruptedException e) {
      }
    }
  }

  private QAction getBestAction(Map<QAction, Double> actionMap) {
    return actionMap.keySet().stream().max((o1, o2) -> Double.compare(actionMap.get(o1), actionMap.get(o2))).get();
  }

  private List<AiLifeState> getStatesMatchingFood(Set<AiLifeState> states, AiLifeState currentState) {
    List<Integer[]> foodLocations = currentState.getFoodLocations();
    return states.stream().filter(state -> state.getFoodLocations().containsAll(foodLocations)  //
                                           && foodLocations.containsAll(state.getFoodLocations())).collect(Collectors.toList());
  }

  @Override
  public void updateGui() {
    drawBestActions(qMap);
  }

  @Override
  public AiLifeState nextState(QAction a, AiLifeState newState) {
    Direction id = actions.get(a);
    List<Vec> foodLocations = new ArrayList<>(newState.getFoodLocations());
    Vec robotLocation = newState.getRobotLocation().copy();
    switch (id) {
      case UP:
        robotLocation.y = (robotLocation.y + 1) % board.getHeight();
        break;
      case RIGHT:
        robotLocation.x = (robotLocation.x + 1) % board.getWidth();
        break;
      case DOWN:
        robotLocation.y = (robotLocation.y * -1 + board.getHeight()) % board.getHeight();
        break;
      case LEFT:
        robotLocation.x = (robotLocation.x - 1 + board.getWidth()) % board.getWidth();
        break;
    }
    AiLifeState nextState = new AiLifeState(foodLocations, robotLocation);
    return nextState;
  }

  public static class AiLifeState implements QState {

    private final List<Integer[]> foodLocations;
    private final Vec robotLocation;
    private final String id;

    public AiLifeState(List<Integer[]> foodLocations, Vec robotLocation) {
      this.foodLocations = foodLocations;
      this.robotLocation = robotLocation;
      id = new StringBuilder().append(foodLocations.stream() //
                                          .map(v -> new StringBuilder().append("F").append(v[0]).append(":").append(v[1]).toString()) //
                                          .collect(Collectors.joining())).append("R").append((int) robotLocation.x).append(":")
          .append((int) robotLocation.y).toString();
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

    public List<Integer[]> getFoodLocations() {
      return foodLocations;
    }

    public Vec getRobotLocation() {
      return robotLocation;
    }
  }
}