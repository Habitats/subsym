package subsym.broids;

import subsym.Log;
import subsym.Models.Entity;
import subsym.gui.Theme;

/**
 * Created by anon on 28.01.2015.
 */
public class Broid extends Entity {

  private static final String TAG = Broid.class.getSimpleName();

  public Broid(int x, int y) {
    super(x, 0, y);
    setColor(Theme.BACKGROUND_DARK);
    setVelocity(0, 0);
  }

  @Override
  public void update() {
    p.add(v);
    Log.v(TAG, toString());
  }

  @Override
  public String toString() {
    return "P: " + p.toString() + " - V: " + v.toString();
  }
}
