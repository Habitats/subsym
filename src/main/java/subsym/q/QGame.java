package subsym.q;

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

  QState computeState();

  double getReward();

  void iterationDone(Map<QState, Map<QAction, Double>> map);
}
