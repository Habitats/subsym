package subsym.flatland;

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
import subsym.flatland.entity.Robot;
import subsym.gui.Direction;
import subsym.models.Vec;
import subsym.q.QAction;
import subsym.q.QGame;
import subsym.q.QLearningEngine;
import subsym.q.QPreferences;
import subsym.q.QState;

/**
 * Created by mail on 04.05.2015.
 */
public class FlatlandQSimulator implements FlatlandSimulator, QGame<FlatlandQState> {

  private static final String TAG = FlatlandQSimulator.class.getSimpleName();
  private Flatland flatland;
  private Map<FlatlandQState, Map<QAction, Float>> qMap;

  private Map<QAction, Direction> actions;
  private Map<FlatlandQState, QAction> bestActions;

  private Map<QState, QAction> stateHistoryActions;
  private Deque<FlatlandQState> stateHistory;
  private double lastReward;

  public FlatlandQSimulator() {
    double learningRate = .9;
    double discountRate = .9;
    run(QPreferences.SCENARIO, learningRate, discountRate, QPreferences.MAX_ITERATION);
  }

  private void run(String scenario, double learningRate, double discountRate, int maxIterations) {
    stateHistory = new LinkedList<>();
    stateHistoryActions = new HashMap<>();

    flatland = new Flatland(this, true);
    flatland.loadFromFile(scenario);

    actions = Arrays.asList(Direction.values()).stream() //
        .collect(Collectors.toMap(dir -> QAction.create(dir.name()), Function.identity()));

    qMap = QLearningEngine.train(maxIterations, this, learningRate, discountRate);

    flatland.simulate(true);
  }

  @Override
  public void onTick() {
    if (flatland.isVisible()) {
      drawBestActions(qMap);
    }
    if (solution()) {
      flatland.terminate();
      Log.v(TAG, "Time: " + flatland.getTravelDistance() + " > Poison: " + flatland.getPoisonCount());
      if (QPreferences.RUN_FOREVER) {
        run(QPreferences.SCENARIO, .9, .9, QPreferences.MAX_ITERATION);
      }
    }
    if (flatland.getTravelDistance() > 1000) {
      Log.v(TAG, "Stuck :( ... " + FlatlandQState.getFoodLocations(computeState().id).size() + " foods left");
      flatland.terminate();
      if (QPreferences.RUN_FOREVER) {
        run(QPreferences.SCENARIO, .9, .9, QPreferences.MAX_ITERATION);
      }
    }

  }

  private void drawBestActions(Map<FlatlandQState, Map<QAction, Float>> qMap) {
    // find the best action for any given state
    if (bestActions == null || lastReward > 0 || QPreferences.DEBUG) {
      // states with the same food config
      Set<FlatlandQState> states = qMap.keySet();
      FlatlandQState currentState = computeState();
      List<FlatlandQState> matchingStates = getStatesMatchingFood(states, currentState);
      bestActions = matchingStates.stream().collect(Collectors.toMap(s -> s, s -> getBestAction(qMap.get(s))));
    }

    if (QPreferences.DEBUG) {
      drawDetailedBestAction(qMap, bestActions);
    } else if (QPreferences.DRAW_ARROWS) {
      drawBestActionArrows(bestActions);
    }
  }

  private void drawBestActionArrows(Map<FlatlandQState, QAction> bestActions) {
    flatland.getItems().forEach(i -> i.setDirection(null));
    bestActions.keySet().forEach(s -> {
      QAction bestAction = bestActions.get(s);
      Vec location = Robot.getLocationFromBits(FlatlandQState.getRobotLocation(s.id), flatland.getWidth(), flatland.getHeight());
      flatland.get(location).setDirection(this.actions.get(bestAction));
    });
  }

  private void drawDetailedBestAction(Map<FlatlandQState, Map<QAction, Float>> qMap, Map<FlatlandQState, QAction> bestActions) {
    flatland.getItems().forEach(i -> i.setDescription(""));
    bestActions.keySet().forEach(s -> {
      QAction bestAction = bestActions.get(s);
      Map<QAction, Float> actions = qMap.get(s);
      String actionValues = actions.keySet().stream() //
          .sorted((o1, o2) -> o1.toString().compareTo(o2.toString())) //
          .map(a -> String.format("%s %5.2f", a.toString().charAt(0), actions.get(a))).collect(Collectors.joining("\n", "\n\n", ""));
      Vec location = Robot.getLocationFromBits(FlatlandQState.getRobotLocation(s.id), flatland.getWidth(), flatland.getHeight());
      flatland.get(location).setDescription(String.format("%s %s", bestAction.toString(), actionValues));
    });
  }

  @Override
  public List<QAction> getActions() {
    return new ArrayList<>(actions.keySet());
  }

  @Override
  public void move(Robot robot) {
    FlatlandQState state = computeState();
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
    flatland.reset();
  }

  @Override
  public boolean solution() {
    return flatland.getFoodCount() == flatland.getMaxFoodCount() && flatland.robotAtStart();
  }

  @Override
  public void execute(QAction a) {
    Direction id = actions.get(a);
    flatland.move(id);
  }

  @Override
  public FlatlandQState computeState() {
    return new FlatlandQState(flatland);
  }

  @Override
  public void onStep(Map<FlatlandQState, Map<QAction, Float>> qMap) {
    if (QPreferences.DEBUG) {
      this.qMap = qMap;
      drawBestActions(qMap);
    }
  }

  @Override
  public void addHisory(FlatlandQState lastState, QAction a) {
    stateHistory.addFirst(lastState);
    stateHistoryActions.put(lastState, a);
    if (stateHistory.size() > QPreferences.BACKUP_THRESHOLD) {
      stateHistory.removeLast();
    }
  }

  @Override
  public Deque<FlatlandQState> getHistory() {
    return stateHistory;
  }

  @Override
  public QAction getHistoryAction(FlatlandQState state) {
    return stateHistoryActions.get(state);
  }

  private QAction getBestAction(Map<QAction, Float> actionMap) {
    return actionMap.keySet().stream().max((o1, o2) -> Double.compare(actionMap.get(o1), actionMap.get(o2))).get();
  }

  private List<FlatlandQState> getStatesMatchingFood(Set<FlatlandQState> states, FlatlandQState currentState) {
    BitSet foodLocations = FlatlandQState.getFoodLocations(currentState.id);
    return states.stream().filter(state -> FlatlandQState.getFoodLocations(state.id).equals(foodLocations)).collect(Collectors.toList());
  }

  @Override
  public double getReward() {
    return lastReward;
  }

  @Override
  public void onFoodConsumed() {
    lastReward = QPreferences.FOOD_REWARD;
  }

  @Override
  public void onNormalMove() {
    lastReward = QPreferences.STEP_PENALTY;
  }

  @Override
  public void onPoisonConsumed() {
    lastReward = QPreferences.POISON_PENALTY;
  }
}
