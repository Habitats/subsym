package subsym.boids.entities;

import java.awt.*;

import subsym.boids.BoidAdapter;
import subsym.gui.ColorUtils;
import subsym.models.Vec;

/**
 * Created by Patrick on 30.01.2015.
 */
public class Obstacle extends Entity {

  public Obstacle(int x, int y) {
    super(x, 0, y);
    setColor(getOriginalColor());
    setVelocity(0, 0);
  }

  public Color getOriginalColor() {
    return ColorUtils.c(3);
  }

  @Override
  public void update(Vec newVelocity) {
// obsticles should never update
  }

  @Override
  protected int getMaxSpeed() {
    return 0;
  }

  public double getSepWeight() {
    return BoidAdapter.obsticleSepWeight;
  }

  public double getAlignWeight() {
    return 1;
  }

  public double getCohWeight() {
    return 1;
  }

  @Override
  public int getRadius() {
    return BoidAdapter.radius;
  }

  @Override
  public double closeRadius() {
    return 200;
  }

  @Override
  public boolean isPurgable() {
    return false;
  }

  @Override
  public boolean isEvil(Entity n) {
    return false;
  }

  @Override
  public int getItemHeight() {
    return (int) (20 / BoidAdapter.scale);
  }

  @Override
  public int getItemWidth() {
    return (int) (20 / BoidAdapter.scale);
  }
}
