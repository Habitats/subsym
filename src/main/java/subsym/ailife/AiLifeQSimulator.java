package subsym.ailife;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
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
import subsym.q.QPreferences;
import subsym.q.QState;

/**
 * Created by mail on 04.05.2015.
 */
public class AiLifeQSimulator implements AiLifeSimulator, QGame<AiLifeQSimulator.AiLifeState> {

  private static final String TAG = AiLifeQSimulator.class.getSimpleName();
  private AiLifeGui gui;
  private Map<AiLifeState, Map<QAction, Float>> qMap;
  private Board<TileEntity> board;
  private int startX;
  private int startY;
  private Robot robot;
  private int numFood;
  private List<List<Integer>> content;
  private Map<QAction, Direction> actions;
  private Map<AiLifeState, QAction> bestActions;

  private Map<QState, QAction> stateHistoryActions;
  private Deque<AiLifeState> stateHistory;

  public AiLifeQSimulator() {
    double learningRate = .9;
    double discountRate = .9;
    run(QPreferences.SCENARIO, learningRate, discountRate, QPreferences.MAX_ITERATION);
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

    if (QPreferences.DEBUG) {
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
      if (QPreferences.RUN_FOREVER) {
        run(QPreferences.SCENARIO, .9, .9, QPreferences.MAX_ITERATION);
      }
    }
    if (robot.getTravelDistance() > 1000) {
      Log.v(TAG, "Stuck :( ... " + AiLifeState.getFoodLocations(computeState().id).size() + " foods left");
      gui.terminate();
      if (QPreferences.RUN_FOREVER) {
        run(QPreferences.SCENARIO, .9, .9, QPreferences.MAX_ITERATION);
      }
    }
  }

  private void drawBestActions(Map<AiLifeState, Map<QAction, Float>> qMap) {
    if (gui == null) {
      return;
    }

    // find the best action for any given state
    if (bestActions == null || robot.getLastStepReward() > 0 || QPreferences.DEBUG) {
      // states with the same food config
      Set<AiLifeState> states = qMap.keySet();
      AiLifeState currentState = computeState();
      List<AiLifeState> matchingStates = getStatesMatchingFood(states, currentState);
      bestActions = matchingStates.stream().collect(Collectors.toMap(s -> s, s -> getBestAction(qMap.get(s))));
    }

    if (QPreferences.DEBUG) {
      drawDetailedBestAction(qMap, bestActions);
    } else if(QPreferences.DRAW_ARROWS){
      drawBestActionArrows(bestActions);
    }

    gui.setAdapter(board);
    board.notifyDataChanged();
  }

  private void drawBestActionArrows(Map<AiLifeState, QAction> bestActions) {
    board.getItems().forEach(i -> i.setDirection(null));
    bestActions.keySet().forEach(s -> {
      QAction bestAction = bestActions.get(s);
      Vec location = Robot.getLocationFromBits(AiLifeState.getRobotLocation(s.id), board.getWidth(), board.getHeight());
      board.get(location).setDirection(this.actions.get(bestAction));
    });
  }

  private void drawDetailedBestAction(Map<AiLifeState, Map<QAction, Float>> qMap, Map<AiLifeState, QAction> bestActions) {
    board.getItems().forEach(i -> i.setDescription(""));
    bestActions.keySet().forEach(s -> {
      QAction bestAction = bestActions.get(s);
      Map<QAction, Float> actions = qMap.get(s);
      String actionValues = actions.keySet().stream() //
          .sorted((o1, o2) -> o1.toString().compareTo(o2.toString())) //
          .map(a -> String.format("%s %5.2f", a.toString().charAt(0), actions.get(a))).collect(Collectors.joining("\n", "\n\n", ""));
      Vec location = Robot.getLocationFromBits(AiLifeState.getRobotLocation(s.id), board.getWidth(), board.getHeight());
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
            robot = new Robot(x, y, board, false);
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
      Map<QAction, Float> actions = qMap.get(state);
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
    return new AiLifeState(robot);
  }

  @Override
  public double getReward() {
    return robot.getLastStepReward();
  }

  @Override
  public void onStep(Map<AiLifeState, Map<QAction, Float>> qMap) {
    if (QPreferences.DEBUG) {
      this.qMap = qMap;
      drawBestActions(qMap);
    }
  }

  @Override
  public void addHisory(AiLifeState lastState, QAction a) {
    stateHistory.addFirst(lastState);
    stateHistoryActions.put(lastState, a);
    if (stateHistory.size() > QPreferences.BACKUP_THRESHOLD) {
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

  private QAction getBestAction(Map<QAction, Float> actionMap) {
    return actionMap.keySet().stream().max((o1, o2) -> Double.compare(actionMap.get(o1), actionMap.get(o2))).get();
  }

  private List<AiLifeState> getStatesMatchingFood(Set<AiLifeState> states, AiLifeState currentState) {
    BitSet foodLocations = AiLifeState.getFoodLocations(currentState.id);
    return states.stream().filter(state -> AiLifeState.getFoodLocations(state.id).equals(foodLocations)).collect(Collectors.toList());
  }

  @Override
  public void updateGui() {
    drawBestActions(qMap);
  }

  public static class AiLifeState implements QState {

    private final BitSet id;
    private static int states = 0;
    private static int TOTAL_FOOD;

    public AiLifeState(Robot robot) {
      BitSet foodLocations = robot.getFoodId();
      BitSet robotLocation = robot.getRobotId();
      int size = robot.getFood().size();
      TOTAL_FOOD = robot.getFood().size();

      id = (BitSet) foodLocations.clone();
      id.set(size + robotLocation.nextSetBit(0));
      states++;
    }

    @Override
    public int hashCode() {
      return id.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
      if (obj instanceof AiLifeState) {
        return ((AiLifeState) obj).id.equals(this.id);
      }
      return false;
    }

    public static BitSet getFoodLocations(BitSet id) {
      return id.get(0, TOTAL_FOOD);
    }

    public static BitSet getRobotLocation(BitSet id) {
      return id.get(TOTAL_FOOD, TOTAL_FOOD + id.nextSetBit(TOTAL_FOOD));
    }
  }
}
