package subsym.broids.gui;

import subsym.Log;
import subsym.Models.AIAdapter;
import subsym.Models.Entity;
import subsym.Models.Vec;
import subsym.broids.Obsticle;
import subsym.gui.ColorUtils;

/**
 * Created by Patrick on 30.01.2015.
 */
public class Predator extends Entity {

  private static final String TAG = Predator.class.getSimpleName();

  public Predator(int x, int y) {
    super(x, 0, y);
    setColor(ColorUtils.c(8));
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
    return (int) (0.5 * AIAdapter.maxSpeed);
  }

  @Override
  public double getSepWeight() {
    return -20000;
  }

  @Override
  public double getAlignWeight() {
    return 1;
  }

  @Override
  public double getCohWeight() {
    return 1;
  }

  @Override
  public int getRadius() {
    return 1000;
  }

  @Override
  public double closeRadius() {
    return 1000;
  }

  @Override
  public boolean isPurgable() {
    return false;
  }

  @Override
  public boolean isEvil(Entity n) {
    return n instanceof Predator || n instanceof Obsticle;
  }

  @Override
  public String toString() {
    return "PREDATOR --> P: " + p.toString() + " - V: " + v.toString();
  }
}
