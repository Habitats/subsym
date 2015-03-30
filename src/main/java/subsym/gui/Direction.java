package subsym.gui;

/**
 * Created by anon on 24.03.2015.
 */
public enum Direction {
  UP(0), RIGHT(1), DOWN(2), LEFT(3);
  private final int i;

  Direction(int i) {
    this.i = i;
  }

  public int getId() {
    return i;
  }

  public static Direction getDirection(Integer value) {
    for (Direction dir : Direction.values()) {
      if (dir.getId() == value) {
        return dir;
      }
    }
    throw new IllegalStateException("No such direction!");
  }
}

