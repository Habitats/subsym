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
import java.util.function.Function;
import java.util.stream.Collectors;

import subsym.Log;
import subsym.Main;
import subsym.flatland.entity.Robot;
import subsym.gui.Direction;
import subsym.models.Vec;
import subsym.q.QAction;
import subsym.q.QCallback;
import subsym.q.QGame;
import subsym.q.QLearningEngine;
import subsym.q.QPreferences;

/**
 * Created by mail on 04.05.2015.
 */
public class FlatlandQSimulator implements FlatlandSimulator, QGame, Runnable {

  private static final String TAG = FlatlandQSimulator.class.getSimpleName();
  private Flatland flatland;
  private Flatland tempFlatland;

  private Map<QAction, Direction> actions;
  private Map<BitSet, QAction> bestActions;
  private Map<BitSet, QAction> stateHistoryActions;

  private Deque<BitSet> stateHistory;
  private double lastReward;
  private boolean isRunning;
  private int shortest = Integer.MAX_VALUE;

  private Map<BitSet, Map<QAction, Float>> qMap;
  private Map<BitSet, Map<QAction, Float>> best;
  private String scenario;

  @Override
  public void run() {
    QPreferences.SHOULD_TERMINATE = false;
    actions = Arrays.asList(Direction.values()).stream().collect(Collectors.toMap(dir -> QAction.get(dir), Function.identity()));
    run(QPreferences.LEARNING_RATE, QPreferences.DISCOUNT_RATE, QPreferences.MAX_ITERATION);
  }

  private void run(double learningRate, double discountRate, int maxIterations) {
    bestActions = null;

    if (QPreferences.SHOULD_TERMINATE) {
      flatland.terminate();
      return;
    }
    isRunning = true;
    stateHistory = new LinkedList<>();
    stateHistoryActions = new HashMap<>();

    flatland = createFlatland();

    QLearningEngine.train(maxIterations, this, learningRate, discountRate, new QCallback() {

      @Override
      public void onIteration(int i, Map<BitSet, Map<QAction, Float>> map) {
        if ((i % 100) == 0 && QPreferences.INTERMEDIATE_SIMULATIONS) {
          qMap = map;
          simulateCurrentState(false);
        }
      }

      @Override
      public void onFinished(Map<BitSet, Map<QAction, Float>> map) {
        qMap = map;
        int current = simulateCurrentState(false);
        updateBest(map, current);

        flatland.simulate(true);

        if (QPreferences.RUN_FOREVER) {
          run();
        } else {
          isRunning = false;
        }
      }

      @Override
      public void onTerminate() {
        isRunning = false;
      }
    });
  }

  private void updateBest(Map<BitSet, Map<QAction, Float>> map, int current) {
    if (current < shortest || best == null) {
      shortest = current;
      if (best != null) {
        best.values().forEach(Map::clear);
        best.clear();
      }
      best = map;
    }
  }

  public void simulateBestState() {
    qMap = best;
    simulateCurrentState(true);
  }

  public int simulateCurrentState(boolean showGui) {
    tempFlatland = flatland;
    flatland = createFlatland();
    int distance = flatland.simulate(showGui);
    distance = (flatland.getPoisonCount() == 0) ? distance : Integer.MAX_VALUE;
    flatland = tempFlatland;
    return distance;
  }

  private Flatland createFlatland() {
    Flatland flatland = new Flatland(this, true);
    if (scenario != QPreferences.SCENARIO && best != null) {
      best.clear();
      best = null;
    }
    scenario = QPreferences.SCENARIO;
    flatland.loadFromFile(scenario);
    return flatland;
  }

  @Override
  public void onTick() {
    if (flatland.isVisible()) {
      drawBestActions(qMap);
    }
    if (solution()) {
      flatland.terminate();
      Log.v(TAG, "Simulation finished in " + flatland.getTravelDistance() + " steps! Poison eaten: " + flatland.getPoisonCount()
                 + " - Food eaten: " + flatland.getFoodCount());
//      Log.v(TAG, "Time: " + flatland.getTravelDistance() + " > Poison: " + flatland.getPoisonCount());
    }
    if (flatland.getTravelDistance() > 1000) {
      Log.v(TAG, "Stuck :( ... " + FlatlandQState.getFoodLocations(computeState()).size() + " foods left");
      flatland.terminate();
    }
  }

  private void drawBestActions(Map<BitSet, Map<QAction, Float>> qMap) {
    // find the best action for any given state
    if (QPreferences.DEBUG) {
      getBestActions(qMap);
      drawDetailedBestAction(qMap, bestActions);
    } else if (QPreferences.DRAW_ARROWS) {
      if (bestActions == null || lastReward > 0) {
        bestActions = getBestActions(qMap);
      }
      drawBestActionArrows(bestActions);
    }
  }

  private Map<BitSet, QAction> getBestActions(Map<BitSet, Map<QAction, Float>> qMap) {
    // states with the same food config
    BitSet currentState = computeState();
    BitSet currentFoodState = FlatlandQState.getFoodLocations(currentState);
    return qMap.keySet().stream() //
        .filter(state -> currentFoodState.equals(FlatlandQState.getFoodLocations(state))) //
        .collect(Collectors.toMap(s -> s, s -> getBestAction(qMap.get(s))));
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
    qMap = null;
  }
}
