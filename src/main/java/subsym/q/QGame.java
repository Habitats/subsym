package subsym.q;

import java.util.BitSet;
import java.util.Deque;
import java.util.List;
import java.util.Map;

/**
 * Created by mail on 05.05.2015.
 */
public interface QGame {

  void restart();

  boolean solution();

  List<QAction> getActions();

  void execute(QAction a);

  BitSet computeState();

  double getReward();

  void onStep(Map<BitSet, Map<QAction, Float>> map);

  void addHisory(BitSet lastState, QAction a);

  Deque<BitSet> getHistory();

  QAction getHistoryAction(BitSet state);
}
