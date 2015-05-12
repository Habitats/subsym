package subsym.q;

import subsym.gui.Direction;

/**
 * Created by mail on 05.05.2015.
 */
public enum QAction {

  UP(0), RIGHT(1), DOWN(2), LEFT(3);
  private final int i;

  QAction(int i) {
    this.i = i;
  }

  public int getId() {
    return i;
  }

  public static QAction get(Direction dir) {
    return values()[dir.ordinal()];
  }
}
