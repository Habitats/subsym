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

/**
 * Created by mail on 04.05.2015.
 */
public class FlatlandQSimulator implements FlatlandSimulator, QGame, Runnable {

  private static final String TAG = FlatlandQSimulator.class.getSimpleName();
  private Flatland flatland;
  private Map<BitSet, Map<QAction, Float>> qMap;

  private Map<QAction, Direction> actions;
  private Map<BitSet, QAction> bestActions;

  private Map<BitSet, QAction> stateHistoryActions;
  private Deque<BitSet> stateHistory;
  private double lastReward;
  private boolean isRunning;

  @Override
  public void run() {
    QPreferences.SHOULD_TERMINATE = false;
    double learningRate = .9;
    double discountRate = .9;
    run(QPreferences.SCENARIO, learningRate, discountRate, QPreferences.MAX_ITERATION);
  }

  private void run(String scenario, double learningRate, double discountRate, int maxIterations) {
    if (QPreferences.SHOULD_TERMINATE) {
      flatland.terminate();
      return;
    }
    isRunning = true;
    stateHistory = new LinkedList<>();
    stateHistoryActions = new HashMap<>();

    flatland = new Flatland(this, true);
    flatland.loadFromFile(scenario);

    actions = Arrays.asList(Direction.values()).stream() //
        .collect(Collectors.toMap(dir -> QAction.get(dir), Function.identity()));

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
      } else {
        isRunning = false;
      }
    }
    if (flatland.getTravelDistance() > 1000) {
      Log.v(TAG, "Stuck :( ... " + FlatlandQState.getFoodLocations(computeState()).size() + " foods left");
      flatland.terminate();
      if (QPreferences.RUN_FOREVER) {
        run(QPreferences.SCENARIO, .9, .9, QPreferences.MAX_ITERATION);
      }
    }

  }

  private void drawBestActions(Map<BitSet, Map<QAction, Float>> qMap) {
    // find the best action for any given state
    if (bestActions == null || lastReward > 0 || QPreferences.DEBUG) {
      // states with the same food config
      Set<BitSet> states = qMap.keySet();
      BitSet currentState = computeState();
      List<BitSet> matchingStates = getStatesMatchingFood(states, currentState);
      bestActions = matchingStates.stream().collect(Collectors.toMap(s -> s, s -> getBestAction(qMap.get(s))));
    }

    if (QPreferences.DEBUG) {
      drawDetailedBestAction(qMap, bestActions);
    } else if (QPreferences.DRAW_ARROWS) {
      drawBestActionArrows(bestActions);
    }
  }

  private void drawBestActionArrows(Map<BitSet, QAction> bestActions) {
    flatland.getItems().forEach(i -> i.setDirection(null));
    bestActions.keySet().forEach(s -> {
      QAction bestAction = bestActions.get(s);
      Vec location = Robot.getLocationFromBits(FlatlandQState.getRobotLocation(s), flatland.getWidth(), flatland.getHeight());
      flatland.get(location).setDirection(this.actions.get(bestAction));
    });
  }

  private void drawDetailedBestAction(Map<BitSet, Map<QAction, Float>> qMap, Map<BitSet, QAction> bestActions) {
    flatland.getItems().forEach(i -> i.setDescription(""));
    bestActions.keySet().forEach(s -> {
      QAction bestAction = bestActions.get(s);
      Map<QAction, Float> actions = qMap.get(s);
      String actionValues = actions.keySet().stream() //
          .sorted((o1, o2) -> o1.toString().compareTo(o2.toString())) //
          .map(a -> String.format("%s %5.2f", a.toString().charAt(0), actions.get(a))).collect(Collectors.joining("\n", "\n\n", ""));
      Vec location = Robot.getLocationFromBits(FlatlandQState.getRobotLocation(s), flatland.getWidth(), flatland.getHeight());
      flatland.get(location).setDescription(String.format("%s %s", bestAction.toString(), actionValues));
    });
  }

  @Override
  public List<QAction> getActions() {
    return new ArrayList<>(actions.keySet());
  }

  @Override
  public void move(Robot robot) {
    BitSet state = computeState();
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
  public BitSet computeState() {
    return FlatlandQState.from(flatland);
  }

  @Override
  public void onStep(Map<BitSet, Map<QAction, Float>> qMap) {
    if (QPreferences.DEBUG) {
      this.qMap = qMap;
      drawBestActions(qMap);
    }
  }

  @Override
  public void addHisory(BitSet lastState, QAction a) {
    stateHistory.addFirst(lastState);
    stateHistoryActions.put(lastState, a);
    if (stateHistory.size() > QPreferences.BACKUP_THRESHOLD) {
      stateHistory.removeLast();
    }
  }

  @Override
  public Deque<BitSet> getHistory() {
    return stateHistory;
  }

  @Override
  public QAction getHistoryAction(BitSet state) {
    return stateHistoryActions.get(state);
  }

  private QAction getBestAction(Map<QAction, Float> actionMap) {
    return actionMap.keySet().stream().max((o1, o2) -> Double.compare(actionMap.get(o1), actionMap.get(o2))).get();
  }

  private List<BitSet> getStatesMatchingFood(Set<BitSet> states, BitSet currentState) {
    BitSet current = FlatlandQState.getFoodLocations(currentState);
    return states.stream().filter(state -> {
      BitSet other = FlatlandQState.getFoodLocations(state);
      return other.equals(current);
    }).collect(Collectors.toList());
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

  public boolean isRunning() {
    return isRunning;
  }

  public void clear() {
    qMap.clear();
  }
}
