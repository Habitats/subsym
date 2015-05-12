package subsym.q;

import gnu.trove.map.hash.THashMap;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import subsym.Log;
import subsym.Main;

/**
 * Created by mail on 05.05.2015.
 */
public class QLearningEngine {

  private static final String TAG = QLearningEngine.class.getSimpleName();

  public static void train(int iterations, QGame game, double learningRate, double discountRate, QCallback callback) {
    Q q = new Q();

    Log.v(TAG, "Training ... ");
    long start = System.currentTimeMillis();
    for (int i = 0; i < iterations; i++) {
      game.restart();
      BitSet lastState = game.computeState();
      while (!game.solution()) {
        if (QPreferences.SHOULD_TERMINATE) {
          Log.v(TAG, "Terminating training ...");
          return;
        }
        if (q.map.get(lastState) == null) {
          List<QAction> actions = game.getActions();
          Map<QAction, Float> actionMap = actions.stream().collect(Collectors.toMap(a -> a, a -> 0.f));
          q.map.put(game.computeState(), actionMap);
        }
        double iterationRate = getIterationRate(iterations, i);
        QAction a = q.selectAction(game, iterationRate);
        if (QPreferences.BACKUP_THRESHOLD > 1) {
          game.addHisory(lastState, a);
        }
        game.execute(a);
        game.onStep(q.map);
        BitSet currentState = game.computeState();
        double r = game.getReward();
        if (r <= 0 || QPreferences.BACKUP_THRESHOLD == 1) {
          float oldScore = getLastScore(q, lastState, a);
          float newScore = update(q, currentState, r, learningRate, discountRate, oldScore);
          q.set(lastState, a, newScore);
        } else {
          for (BitSet state : game.getHistory()) {
            float oldScore = getLastScore(q, lastState, a);
            float newScore = update(q, currentState, r, learningRate, discountRate, oldScore);
            q.set(lastState, a, newScore);
            currentState = state;
            if (newScore < oldScore) {
              break;
            }
          }
        }
        if (QPreferences.BACKUP_THRESHOLD == 1) {
          lastState = currentState;
        } else {
          lastState = game.computeState();
        }

        if (QPreferences.DEBUG) {
          try {
            Thread.sleep(200);
            Log.v(TAG, "Sleep ...");
          } catch (InterruptedException e) {
          }
        }
      }

      //      Log.v(TAG, "Iteration " + (i + 1) + "/" + iterations);
      if ((i % 100) == 0) {
//        System.out.print("#");
        QPreferences.setProgress(i, iterations);
      }
      callback.onIteration(i, q.map);
    }
    QPreferences.setProgress(0, iterations);
    Log.v(TAG,
          String.format("Training completed in %d s > States: %d", (int) ((System.currentTimeMillis() - start) / 1000.), q.map.size()));
    System.out.println();

    callback.onFinished(q.map);
  }

  private static double getIterationRate(int iterations, int i) {
    if (iterations > QPreferences.RANDOM_ITERATION_THRESHOLD) {
      iterations = Math.min(iterations, QPreferences.RANDOM_ITERATION_THRESHOLD);
    }
    return Math.max(0, iterations - i) / (double) iterations;
  }

  private static float update(Q q, BitSet currentState, double r, double learningRate, double discountRate, double oldScore) {
    double maxScoreIfBestAction = q.bestNextGivenAction(currentState);
    double delta = learningRate * (r + discountRate * maxScoreIfBestAction - oldScore);
    double score = oldScore + delta;
    return (float) score;
  }

  private static float getLastScore(Q q, BitSet lastState, QAction a) {
    return q.get(lastState, a);
  }

  public static class Q {

    private Map<BitSet, Map<QAction, Float>> map = new THashMap<>(1_000_000);

    public float get(BitSet s, QAction a) {
      float score;
      if (map.containsKey(s) && map.get(s).containsKey(a)) {
        score = map.get(s).get(a);
      } else {
        set(s, a, 0);
        score = map.get(s).get(a);
      }
      return score;
    }

    public double bestNextGivenAction(BitSet s) {
      Map<QAction, Float> actionMap = map.get(s);
      if (actionMap == null) {
        return 0;
      } else {
        Comparator<QAction> actionComparator = (a1, a2) -> Double.compare(actionMap.get(a1), actionMap.get(a2));
        QAction bestAction = Collections.max(actionMap.keySet(), actionComparator);
        return actionMap.get(bestAction);
      }
    }

    public void set(BitSet s, QAction a, float value) {
      Map<QAction, Float> actions;
      if (map.containsKey(s)) {
        actions = map.get(s);
      } else {
        actions = new HashMap<>();
        map.put(s, actions);
      }
      actions.put(a, value);
    }

    public QAction selectAction(QGame game, double iterationRate) {
      BitSet key = game.computeState();
      Map<QAction, Float> actionMap = map.get(key);

      boolean
          pickRandom =
          Main.random().nextDouble() < QPreferences.LOWER_RANDOM_THRESHOLD + QPreferences.UPPER_RANDOM_THRESHOLD * iterationRate;
//      Log.v(TAG, v);
      if (!pickRandom && actionMap.size() >= 0) {
        return getBestAction(actionMap);
      } else {
        List<QAction> actions = game.getActions();
        return actions.get(Main.random().nextInt(actions.size()));
      }
    }

    private QAction getRandomAction(Map<QAction, Float> actionMap) {
      List<QAction> actions = new ArrayList<>(actionMap.keySet());
      return actions.get(Main.random().nextInt(actionMap.size()));
    }

    private QAction getBestAction(Map<QAction, Float> actionMap) {
      return actionMap.keySet().stream().max((a1, a2) -> Float.compare(actionMap.get(a1), actionMap.get(a2))).get();
    }
  }
}
