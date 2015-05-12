package subsym.q;

import subsym.gui.Direction;

/**
 * Created by mail on 05.05.2015.
 */
public class QAction {

  private final Direction id;

  public QAction(Direction id) {
    this.id = id;
  }

  public static QAction create(Direction id) {
    return new QAction(id);
  }
//
//  @Override
//  public int hashCode() {
//    return id.hashCode();
//  }
//
//  @Override
//  public boolean equals(Object obj) {
//    return toString().equals(obj.toString());
//  }

  @Override
  public String toString() {
    return id.name();
  }
}
