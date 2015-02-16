package subsym.boids.entities;

import java.awt.*;

import subsym.boids.BoidAdapter;
import subsym.models.Vec;
import subsym.gui.ColorUtils;

/**
 * Created by Patrick on 30.01.2015.
 */
public class Predator extends Entity {

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
    p.add(v);
  }

  @Override
  protected int getMaxSpeed() {
    return (int) (0.5 * BoidAdapter.maxSpeed);
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
  public boolean isEvil(Entity n) {
    return n instanceof Predator || n instanceof Obsticle;
  }

  @Override
  public String toString() {
    return "PREDATOR --> P: " + p.toString() + " - V: " + v.toString();
  }

}
