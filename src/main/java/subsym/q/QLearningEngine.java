package subsym.q;

import gnu.trove.map.hash.THashMap;

import java.util.ArrayList;
import java.util.BitSet;
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

//    Log.v(TAG, "Training ... ");
    long start = System.currentTimeMillis();
    List<QAction> actions = game.getActions();
    for (int i = 0; i < iterations; i++) {
      game.restart();
      BitSet lastState = game.computeState();
      while (!game.solution()) {
        if (QPreferences.SHOULD_TERMINATE) {
          Log.v(TAG, "Terminating training ...");
          callback.onTerminate();
          return;
        }
        if (q.map.get(lastState) == null) {
          q.map.put(game.computeState(), new HashMap<>(4));
        }
        QAction a = q.selectAction(game, actions, i);
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
    Log.v(TAG, String.format("Training completed in %d s > States: %d > Actions: %d",  //
                             (int) ((System.currentTimeMillis() - start) / 1000.), q.map.size(),
                             q.map.values().stream().mapToInt(Map::size).sum()));
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
//        QAction bestAction = Collections.max(actionMap.keySet(), actionComparator);
        QAction bestAction = actionMap.keySet().stream().max(getActionComparator(actionMap)).get();
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

    public QAction selectAction(QGame game, List<QAction> actions, int currentIteration) {
      BitSet key = game.computeState();
      Map<QAction, Float> actionMap = this.map.get(key);

      double threshold;
      if (currentIteration < 5000) {
        threshold = 0.002;
      } else if (currentIteration < QPreferences.RANDOM_ITERATION_THRESHOLD) {
        double dy = QPreferences.RANDOM_THRESHOLD_END - QPreferences.RANDOM_THRESHOLD_START;
        double dx = QPreferences.RANDOM_ITERATION_THRESHOLD;
        threshold = dy / dx * currentIteration + QPreferences.RANDOM_THRESHOLD_START;
        threshold = Math.min(QPreferences.RANDOM_THRESHOLD_MAX, threshold);
      } else {
        double dy = QPreferences.RANDOM_THRESHOLD_START - QPreferences.RANDOM_THRESHOLD_MAX;
        double dx = QPreferences.MAX_ITERATION - QPreferences.RANDOM_ITERATION_THRESHOLD;
        threshold = dy / dx * (currentIteration - QPreferences.RANDOM_ITERATION_THRESHOLD) + QPreferences.RANDOM_THRESHOLD_MAX;
      }
      boolean pickRandom = Main.random().nextDouble() < threshold;
      QPreferences.ITERATION_RATE = threshold;
      if (!pickRandom) {
        return getBestAction(actionMap, actions);
      } else {
        int randomIndex = Main.random().nextInt(actions.size());
        return actions.get(randomIndex);
      }
    }

    private QAction getRandomAction(Map<QAction, Float> actionMap) {
      List<QAction> actions = new ArrayList<>(actionMap.keySet());
      return actions.get(Main.random().nextInt(actionMap.size()));
    }

    private QAction getBestAction(Map<QAction, Float> availableActions, List<QAction> allActions) {
      int missing = allActions.size() - availableActions.size();
      // there are some actions here
      if (missing != allActions.size()) {
        float best = availableActions.values().stream().max(Float::compare).get();
        // there are good actions present, pick the best one
        QAction qAction = availableActions.keySet().stream().max(getActionComparator(availableActions)).get();
        // found a good one, or they are all just here, and they suck
        if (best > 0 || missing == 0) {
          return qAction;
        }
      }

      // some actions are missing, or they are all shit

      // if they are ALL missing, just pick something random
      if (missing == allActions.size()) {
        QAction totallyRandomAction = allActions.get(Main.random().nextInt(allActions.size()));
        availableActions.put(totallyRandomAction, 0.f);
        return totallyRandomAction;
      }

      // so there are SOME stuff here, shouldn't override those
      else {
        QAction randomActionBetterThanThePresentOnes = allActions.stream() //
            .filter(a -> !availableActions.containsKey(a)).collect(Collectors.toList()).get(Main.random().nextInt(missing));
        availableActions.put(randomActionBetterThanThePresentOnes, 0.f);
        return randomActionBetterThanThePresentOnes;
      }
    }

    private Comparator<QAction> getActionComparator(Map<QAction, Float> actionMap) {
      return (a1, a2) -> Float.compare(actionMap.get(a1), actionMap.get(a2));
    }
  }
}
