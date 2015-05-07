package subsym.boids.entities;

import java.awt.*;

import subsym.models.Vec;
import subsym.models.entity.Entity;

/**
 * Created by Patrick on 08.09.2014.
 */
public abstract class BroidEntity extends Entity {


  protected final int index;
  public Vec v;

  private String desc = "";

  public BroidEntity(int x, int index, int y) {
    super(x, y);
    this.index = index;
  }

  public void setVelocity(double x, double y) {
    v = Vec.create(x, y);
  }

  public void setVelocity(Vec v) {
    this.v = v;
  }

  public void update(Vec newVelocity) {
    setVelocity(newVelocity);
    limitVelocity(getMaxSpeed());
  }

  public Vec getVelocity() {
    return v;
  }

  public void limitVelocity(int maxSpeed) {
    if (v.lenght() > maxSpeed) {
      v.divide(v.lenght()).multiply(maxSpeed);
    }
  }


  public void wrapAround(int width, int height) {
    position.setX((position.getX() + width) % width);
    position.setY((position.getY() + height) % height);
  }

  protected abstract int getMaxSpeed();

  public abstract double getSepWeight();

  public abstract Color getOriginalColor();

  public abstract double getAlignWeight();

  public abstract double getCohWeight();

  public abstract int getRadius();

  public abstract double closeRadius();

  public abstract boolean isPurgable();

  public abstract boolean isEvil(BroidEntity n);

}
