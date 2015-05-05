package subsym.q;

import java.util.ArrayList;
import java.util.Collections;
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

  public static <T extends QState> Map<T, Map<QAction, Double>> learn(int iterations, QGame<T> game) {
    Q q = new Q();
    double learningRate = 0.1;
    for (int i = 0; i < iterations; i++) {
      game.restart();
      while (!game.solution()) {
        T state = game.computeState();
//        Log.v(TAG, state);
        QAction a = q.selectAction(state, game);
        game.execute(a);
        T newState = game.computeState();
//        Log.v(TAG, newState);
        double r = game.getReward();

        update(q, state, newState, a, r, learningRate, 1. / iterations);

      }
      game.iterationDone(q.map);
      Log.v(TAG, "Iteration " + (i + 1) + "/" + iterations);
    }

    return q.map;
  }

  private static <T extends QState> void update(Q q, T s, T next, QAction a, double r, double learningRate,
                                                double discountRate) {
    double score = q.get(s, a) + learningRate * (r + discountRate * q.bestNextGivenAction(next, a) - q.get(s, a));
    q.set(s, a, score);
  }

  public static class Q<T extends QState> {

    private Map<T, Map<QAction, Double>> map;

    public Q() {
      map = new HashMap<>();
    }

    public Double get(T s, QAction a) {
      if (map.containsKey(s) && map.get(s).containsKey(a)) {
        return map.get(s).get(a);
      } else {
        set(s, a, 1);
        return map.get(s).get(a);
      }
    }

    public double bestNextGivenAction(T s, QAction a) {
      try {
        return Collections.max(map.get(s).values());
      } catch (Exception e) {
        return 0;
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

    public QAction selectAction(T state, QGame game) {
      Map<QAction, Double> actionMap = map.get(state);
      boolean pickRandom = Main.random().nextDouble() < .1;
      if (actionMap != null && !pickRandom) {
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
