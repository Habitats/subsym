package subsym.boids.entities;

import java.awt.*;

import subsym.boids.BoidAdapter;
import subsym.gui.ColorUtils;
import subsym.models.Vec;

/**
 * Created by Patrick on 30.01.2015.
 */
public class Predator extends BroidEntity {

  private static final String TAG = Predator.class.getSimpleName();

  public Predator(int x, int y) {
    super(x, 0, y);
    setColor(getOriginalColor());
    setVelocity(0, 0);
  }

  public Color getOriginalColor() {
    return ColorUtils.c(2);
  }

  @Override
  public void update(Vec newVelocity) {
    super.update(newVelocity);
    position.add(v);
  }

  @Override
  protected int getMaxSpeed() {
    return (int) (0.8 * BoidAdapter.maxSpeed);
  }

  @Override
  public double getSepWeight() {
    return 2;
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
    return 1500;
  }

  @Override
  public double closeRadius() {
    return 500;
  }

  @Override
  public boolean isPurgable() {
    return false;
  }

  @Override
  public boolean isEvil(BroidEntity n) {
    return n instanceof Predator || n instanceof Obstacle;
  }

  @Override
  public String toString() {
    return "PREDATOR --> P: " + position.toString() + " - V: " + v.toString();
  }

  @Override
  public int getItemHeight() {
    return (int) (15 / BoidAdapter.scale);
  }

  @Override
  public int getItemWidth() {
    return (int) (15 / BoidAdapter.scale);
  }
}
