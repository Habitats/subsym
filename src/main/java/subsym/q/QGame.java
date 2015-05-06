package subsym.q;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Created by mail on 05.05.2015.
 */
public interface QGame<T extends QState> {

  void restart();

  boolean solution();

  List<QAction> getActions();

  void execute(QAction a);

  T computeState();

  double getReward();

  void onStep(Map<T, Map<QAction, Double>> map);

  void addHisory(T lastState);


  Collection<T> getHistoryStream();

//  T nextState(QAction a, T newState);
}
