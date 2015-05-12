package subsym.q;

import java.util.BitSet;
import java.util.Map;

/**
 * Created by mail on 12.05.2015.
 */
public interface QCallback {

  void onIteration(int i, Map<BitSet, Map<QAction, Float>> map);

  void onFinished(Map<BitSet, Map<QAction, Float>> map);
}
