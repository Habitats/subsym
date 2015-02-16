package subsym.broids.entities;

import java.awt.*;

import subsym.broids.BroidAdapter;
import subsym.models.Vec;
import subsym.gui.ColorUtils;

/**
 * Created by anon on 28.01.2015.
 */
public class Broid extends Entity {

  private static final String TAG = Broid.class.getSimpleName();

  public Broid(int x, int y) {
    super(x, 0, y);
    setColor(getOriginalColor());
    setVelocity(Math.random(), Math.random());
  }

  public Color getOriginalColor() {
    return ColorUtils.c(1);
  }

  @Override
  public void update(Vec newVelocity) {
    super.update(newVelocity);
    p.add(v);
//    Log.v(TAG, toString());
  }

  @Override
  protected int getMaxSpeed() {
    return BroidAdapter.maxSpeed;
  }

  @Override
  public String toString() {
    return "P: " + p.toString() + " - V: " + v.toString();
  }

  public double getSepWeight() {
    return BroidAdapter.sepWeight;
  }

  public double getAlignWeight() {
    return BroidAdapter.alignWeight;
  }

  public double getCohWeight() {
    return BroidAdapter.cohWeight;
  }

  @Override
  public int getRadius() {
    return BroidAdapter.radius;
  }

  @Override
  public double closeRadius() {
    return 100;
  }

  @Override
  public boolean isPurgable() {
    return true;
  }

  @Override
  public boolean isEvil(Entity n) {
    return n instanceof Predator;
  }

  @Override
  public double getEvilWeight() {
    return 1;
  }
}
