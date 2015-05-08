package subsym.boids.entities;

import java.awt.*;

import subsym.Main;
import subsym.boids.BoidAdapter;
import subsym.gui.ColorUtils;
import subsym.models.Vec;

/**
 * Created by anon on 28.01.2015.
 */
public class Broid extends BroidEntity {

  private static final String TAG = Broid.class.getSimpleName();

  public Broid(int x, int y) {
    super(x, 0, y);
    setColor(getOriginalColor());
    setVelocity(Main.random().nextDouble(), Main.random().nextDouble());
  }

  public Color getOriginalColor() {
    return ColorUtils.c(1);
  }

  @Override
  public void update(Vec newVelocity) {
    super.update(newVelocity);
    position.add(v);
//    Log.v(TAG, toString());
  }

  @Override
  protected int getMaxSpeed() {
    return BoidAdapter.maxSpeed;
  }

  @Override
  public String toString() {
    return "P: " + position.toString() + " - V: " + v.toString();
  }

  public double getSepWeight() {
    return BoidAdapter.sepWeight;
  }

  public double getAlignWeight() {
    return BoidAdapter.alignWeight;
  }

  public double getCohWeight() {
    return BoidAdapter.cohWeight;
  }

  @Override
  public int getRadius() {
    return BoidAdapter.radius;
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
  public boolean isEvil(BroidEntity n) {
    return n instanceof Predator;
  }

  @Override
  public int getItemHeight() {
    return (int) (10 / BoidAdapter.scale);
  }

  @Override
  public int getItemWidth() {
    return (int) (10 / BoidAdapter.scale);
  }
}
