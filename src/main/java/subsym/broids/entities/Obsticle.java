package subsym.broids.entities;

import subsym.broids.BroidAdapter;
import subsym.broids.Vec;
import subsym.gui.ColorUtils;

/**
 * Created by Patrick on 30.01.2015.
 */
public class Obsticle extends Entity {

  public Obsticle(int x, int y) {
    super(x, 0, y);
    setColor(ColorUtils.c(4));
    setVelocity(0, 0);
  }

  @Override
  public void update(Vec newVelocity) {

  }

  @Override
  protected int getMaxSpeed() {
    return 0;
  }

  public double getSepWeight() {
    return -BroidAdapter.sepWeight * 20;
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
    return 20;
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
