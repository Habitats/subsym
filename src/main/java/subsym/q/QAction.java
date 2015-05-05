package subsym.q;

/**
 * Created by mail on 05.05.2015.
 */
public class QAction {

  private final String id;

  public QAction(String id) {
    this.id = id;
  }

  public static QAction create(String id) {
    return new QAction(id);
  }

  @Override
  public String toString() {
    return id;
  }
}
