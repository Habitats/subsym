package subsym.q;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import subsym.Log;
import subsym.Main;

/**
 * Created by mail on 05.05.2015.
 */
public class QLearningEngine {

  private static final String TAG = QLearningEngine.class.getSimpleName();

  public static <T extends QState> Map<T, Map<QAction, Double>> learn(int iterations, QGame<T> game, double learningRate,
                                                                      double discountRate) {
    Q q = new Q();

    long start = System.currentTimeMillis();
    T currentState = null;
    for (int i = 0; i < iterations; i++) {
      game.restart();
      while (!game.solution()) {
//        Log.v(TAG, state);
        T lastState = currentState == null ? game.computeState() : currentState;
        game.addHisory(lastState);
        QAction a = q.selectAction(game);
        game.execute(a);
        game.onStep(q.map);
        currentState = game.computeState();
//        Log.v(TAG, newState);
        double r = game.getReward();

        update(q, lastState, currentState, a, r, learningRate, discountRate);

      }

      Log.v(TAG, "Iteration " + (i + 1) + "/" + iterations);
    }
    Log.v(TAG, String.format("Training completed in %d s", (int) ((System.currentTimeMillis() - start) / 1000.)));

    return q.map;
  }

  private static <T extends QState> void update(Q q, T lastState, T currentState, QAction a, double r, double learningRate,
                                                double discountRate) {
    double maxScoreIfBestAction = q.bestNextGivenAction(currentState);
    double oldScore = q.get(lastState, a);
    double delta = learningRate * (r + discountRate * maxScoreIfBestAction - oldScore);
    double score = oldScore + delta;
    q.set(lastState, a, score);
  }

  public static class Q<T extends QState> {

    private Map<T, Map<QAction, Double>> map;

    public Q() {
      map = new HashMap<>();
    }

    public double get(T s, QAction a) {
      Double score;
      if (map.containsKey(s) && map.get(s).containsKey(a)) {
        score = map.get(s).get(a);
      } else {
        set(s, a, 0);
        score = map.get(s).get(a);
      }
      return score;
    }

    public double bestNextGivenAction(T s) {
      Map<QAction, Double> actionMap = map.get(s);
      if (actionMap == null) {
        return 0;
      } else {
        Comparator<QAction> actionComparator = (a1, a2) -> Double.compare(actionMap.get(a1), actionMap.get(a2));
        QAction bestAction = Collections.max(actionMap.keySet(), actionComparator);
        return actionMap.get(bestAction);
      }
    }

    public void set(T s, QAction a, double newState) {
      Map<QAction, Double> actions;
      if (map.containsKey(s)) {
        actions = map.get(s);
      } else {
        actions = new HashMap<>();
        map.put(s, actions);
      }
      actions.put(a, newState);
    }

    public QAction selectAction(QGame game) {
      Map<QAction, Double> actionMap = map.get(game.computeState());
      boolean pickRandom = Main.random().nextDouble() < .1;
      if (actionMap != null && !pickRandom && actionMap.size() == 4) {
        return getBestAction(actionMap);
      } else {
        List<QAction> actions = game.getActions();
        return actions.get(Main.random().nextInt(actions.size()));
      }
    }

    private QAction getRandomAction(Map<QAction, Double> actionMap) {
      List<QAction> actions = new ArrayList<>(actionMap.keySet());
      return actions.get(Main.random().nextInt(actionMap.size()));
    }

    private QAction getBestAction(Map<QAction, Double> actionMap) {
      return actionMap.keySet().stream().max((a1, a2) -> Double.compare(actionMap.get(a1), actionMap.get(a2))).get();
    }
  }
}
