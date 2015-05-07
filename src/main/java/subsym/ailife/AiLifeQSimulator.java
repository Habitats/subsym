package subsym.ailife;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
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
  private Map<AiLifeState, QAction> bestActions;

  public static final String scenario1 = "1-simple.txt";
  public static final String scenario2 = "2-still-simple.txt";
  public static final String scenario3 = "3-dont-be-greedy.txt";
  public static final String scenario4 = "4-big-one.txt";
  public static final String scenario5 = "5-even-bigger.txt";
  private final String scenario;

  private Map<QState, QAction> stateHistoryActions;
  private Deque<AiLifeState> stateHistory;

  public AiLifeQSimulator() {
    double learningRate = .9;
    double discountRate = .9;
    scenario = scenario4;
    run(scenario, learningRate, discountRate, QLearningEngine.MAX_ITERATION);
  }

  private void simulate() {
    board = initBoard(this.board.getWidth(), this.board.getHeight(), content);
    gui = new AiLifeGui(board, this, robot);
    gui.simulate(() -> Log.v(TAG, ""));
  }

  private void run(String scenario, double learningRate, double discountRate, int maxIterations) {
    stateHistory = new LinkedList<>();
    stateHistoryActions = new HashMap<>();

    board = fromFile(scenario);

    actions = Arrays.asList(Direction.values()).stream() //
        .collect(Collectors.toMap(dir -> QAction.create(dir.name()), Function.identity()));

    if (QLearningEngine.DEBUG) {
      gui = new AiLifeGui(board, this, robot);
      board = initBoard(this.board.getWidth(), this.board.getHeight(), content);
      gui.setAdapter(board);
    }
    qMap = QLearningEngine.learn(maxIterations, this, learningRate, discountRate);

    simulate();
  }

  @Override
  public void onTick() {
    drawBestActions(qMap);
    if (solution()) {
      gui.terminate();
      Log.v(TAG, "Time: " + robot.getTravelDistance() + " > Poison: " + robot.getPoisonCount());
      run(scenario, .9, .9, QLearningEngine.MAX_ITERATION);
    }
    if (robot.getTravelDistance() > 1000) {
      Log.v(TAG, "Stuck :( ... " + computeState().getFoodLocations().size() + " foods left");
      gui.terminate();
      run(scenario, .9, .9, QLearningEngine.MAX_ITERATION);
    }
  }

  private void drawBestActions(Map<AiLifeState, Map<QAction, Double>> qMap) {
    if (gui == null) {
      return;
    }

    // find the best action for any given state
//    if (bestActions == null || robot.getLastStepReward() > 0 || QLearningEngine.DEBUG) {
//      // states with the same food config
//      Set<AiLifeState> states = qMap.keySet();
//      AiLifeState currentState = computeState();
//      List<AiLifeState> matchingStates = getStatesMatchingFood(states, currentState);
//      bestActions = matchingStates.stream().collect(Collectors.toMap(s -> s, s -> getBestAction(qMap.get(s))));
//    }

    if (QLearningEngine.DEBUG) {
      drawDetailedBestAction(qMap, bestActions);
    } else {
//      drawBestActionArrows(bestActions);
    }

    gui.setAdapter(board);
    board.notifyDataChanged();
  }

  private void drawBestActionArrows(Map<AiLifeState, QAction> bestActions) {
    board.getItems().forEach(i -> i.setDirection(null));
    bestActions.keySet().forEach(s -> {
      QAction bestAction = bestActions.get(s);
      Vec location = s.getRobotLocation();
      board.get(location).setDirection(this.actions.get(bestAction));
    });
  }

  private void drawDetailedBestAction(Map<AiLifeState, Map<QAction, Double>> qMap, Map<AiLifeState, QAction> bestActions) {
    board.getItems().forEach(i -> i.setDescription(""));
    bestActions.keySet().forEach(s -> {
      QAction bestAction = bestActions.get(s);
      Map<QAction, Double> actions = qMap.get(s);
      String actionValues = actions.keySet().stream() //
          .sorted((o1, o2) -> o1.toString().compareTo(o2.toString())) //
          .map(a -> String.format("%s %5.2f", a.toString().charAt(0), actions.get(a))).collect(Collectors.joining("\n", "\n\n", ""));
      Vec location = s.getRobotLocation();
      board.get(location).setDescription(String.format("%s %s", bestAction.toString(), actionValues));
    });
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
    robot.init();

    return board;
  }

  @Override
  public void reset() {
    board = initBoard(board.getWidth(), board.getHeight(), content);
    gui.setAdapter(board);
  }

  @Override
  public List<QAction> getActions() {
    return new ArrayList<>(actions.keySet());
  }

  @Override
  public void move(Robot robot) {
    AiLifeState state = computeState();
    this.robot = robot;
    QAction bestAction;
    if (qMap.containsKey(state)) {
      Map<QAction, Double> actions = qMap.get(state);
      List<QAction> randomized = new ArrayList<>(actions.keySet());
      Collections.shuffle(randomized, Main.random());
      bestAction = Collections.max(randomized, (a1, a2) -> Double.compare(actions.get(a1), actions.get(a2)));
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
    Collection<Vec> foodLocations = new ArrayList<>(robot.getFood().keySet());
    Vec robotLocation = Vec.create(robot.getX(), robot.getY());
    return new AiLifeState(foodLocations, robotLocation, robot.getFood().size());
  }

  @Override
  public double getReward() {
    return robot.getLastStepReward();
  }

  @Override
  public void onStep(Map<AiLifeState, Map<QAction, Double>> qMap) {
    if (QLearningEngine.DEBUG) {
      this.qMap = qMap;
      drawBestActions(qMap);
    }
  }

  @Override
  public void addHisory(AiLifeState lastState, QAction a) {
    stateHistory.addFirst(lastState);
    stateHistoryActions.put(lastState, a);
    if (stateHistory.size() > QLearningEngine.STATE_HISTORY_THRESHOLD) {
      stateHistory.removeLast();
    }
  }

  @Override
  public Deque<AiLifeState> getHistory() {
    return stateHistory;
  }

  @Override
  public QAction getHistoryAction(AiLifeState state) {
    return stateHistoryActions.get(state);
  }

  private QAction getBestAction(Map<QAction, Double> actionMap) {
    return actionMap.keySet().stream().max((o1, o2) -> Double.compare(actionMap.get(o1), actionMap.get(o2))).get();
  }

  private List<AiLifeState> getStatesMatchingFood(Set<AiLifeState> states, AiLifeState currentState) {
    Collection<Vec> foodLocations = currentState.getFoodLocations();
    return states.stream().filter(state -> state.getFoodLocations().equals(foodLocations)).collect(Collectors.toList());
  }

  @Override
  public void updateGui() {
    drawBestActions(qMap);
  }

  public static class AiLifeState implements QState {

    private final int id;
    private static final Map<Integer, Collection<Vec>> foodCache = new HashMap<>();
    private static final Map<Integer, Vec> robotCache = new HashMap<>();
    private static int states = 0;
    private final int foodKey;
    private final int robotKey;
//    private final String s;

    public AiLifeState(Collection<Vec> foodLocations, Vec robotLocation, int size) {
      foodKey = foodLocations.stream().map(Vec::getId) //
//          .sorted(Comparator.<String>naturalOrder()) //
          .collect(Collectors.joining("&")).hashCode();
      robotKey = robotLocation.hashCode();
      foodCache.putIfAbsent(foodKey, foodLocations);
      robotCache.putIfAbsent(robotKey, robotLocation);
      states++;
      String s1 = "F:" + foodKey + " R:" + robotKey;
//      String s1 = foodLocations.stream().map(v -> "F:" + (int) v.getX() + ":" + (int) v.getY()) //
//                      .collect(Collectors.joining(" ")) + " R:" + (int) robotLocation.getX() + ":" + (int) robotLocation.getY();
      id = s1.hashCode();

//      s = String.valueOf(System.currentTimeMillis());
//      Log.v(TAG, String.format("%0$-30s \t %s", s1, s2));
    }

    @Override
    public int hashCode() {
      return id;
    }

    @Override
    public boolean equals(Object obj) {
      return hashCode() == obj.hashCode();
    }

//    @Override
//    public String toString() {
////      return getFoodLocations().stream().map(v -> "F:" + (int) v.x + ":" + (int) v.y) //
////                 .collect(Collectors.joining(" ")) + " R:" + (int) getRobotLocation().x + ":" + (int) getRobotLocation().y;
//      return s;
//    }

    public Collection<Vec> getFoodLocations() {
      return foodCache.get(foodKey);
    }

    public Vec getRobotLocation() {
      return robotCache.get(robotKey);
    }
  }
}
