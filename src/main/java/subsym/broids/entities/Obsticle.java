package subsym.broids.entities;

import java.awt.*;

import subsym.models.Vec;
import subsym.gui.ColorUtils;

/**
 * Created by Patrick on 30.01.2015.
 */
public class Obsticle extends Entity {

  public Obsticle(int x, int y) {
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
    return -1;
  }

  public double getAlignWeight() {
    return 1;
  }

  public double getCohWeight() {
    return 1;
  }

  @Override
  public int getRadius() {
    return 100;
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
    return false;
  }
}
