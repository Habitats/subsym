package subsym.boids.entities;

import java.awt.*;

import subsym.MODELS.Vec;

/**
 * Created by Patrick on 08.09.2014.
 */
public abstract class Entity {


  private static final Color EMPTY = Color.WHITE;
  protected final int index;
  public Vec p;
  public Vec v;

  private Color color = EMPTY;
  private String desc = "";
  private Color outlineColor = Color.black;

  public Entity(int x, int index, int y) {
    this.index = index;
    p = Vec.create(x, y);
  }

  public void setVelocity(double x, double y) {
    v = Vec.create(x, y);
  }

  public void setVelocity(Vec v) {
    this.v = v;
  }

  public int getX() {
    return (int) p.x;
  }

  public int getY() {
    return (int) p.y;
  }

  public Color getColor() {
    return color;
  }

  public void setColor(Color color) {
    this.color = color;
  }

  public void setOutlineColor(Color outlineColor) {
    this.outlineColor = outlineColor;
  }

  public Color getOutlineColor() {
    return outlineColor;
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
    p.x = (p.x + width) % width;
    p.y = (p.y + height) % height;
  }

  protected abstract int getMaxSpeed();

  public abstract double getSepWeight();

  public abstract Color getOriginalColor();

  public abstract double getAlignWeight();

  public abstract double getCohWeight();

  public abstract int getRadius();

  public abstract double closeRadius();

  public abstract boolean isPurgable();

  public abstract boolean isEvil(Entity n);

  public abstract int getItemWidth();

  public abstract int getItemHeight();
}
