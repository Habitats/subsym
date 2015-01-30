package subsym.broids;

import subsym.Log;
import subsym.Models.AIAdapter;
import subsym.Models.Entity;
import subsym.Models.Vec;
import subsym.gui.ColorUtils;

/**
 * Created by anon on 28.01.2015.
 */
public class Broid extends Entity {

  private static final String TAG = Broid.class.getSimpleName();

  public Broid(int x, int y) {
    super(x, 0, y);
    setColor(ColorUtils.c(0));
    setVelocity(0, 0);
  }

  @Override
  public void update(Vec newVelocity) {
    super.update(newVelocity);
    p.add(v);
    Log.v(TAG, toString());
  }

  @Override
  protected int getMaxSpeed() {
    return AIAdapter.maxSpeed;
  }

  @Override
  public String toString() {
    return "P: " + p.toString() + " - V: " + v.toString();
  }

  public double getSepWeight() {
    return AIAdapter.sepWeight;
  }

  public double getAlignWeight() {
    return AIAdapter.alignWeight;
  }

  public double getCohWeight() {
    return AIAdapter.cohWeight;
  }

  @Override
  public int getRadius() {
    return AIAdapter.radius;
  }

  @Override
  public double closeRadius() {
    return 200;
  }

  @Override
  public boolean isPurgable() {
    return true;
  }
}
