package subsym.broids.entities;

import java.awt.*;

import subsym.broids.Vec;

/**
 * Created by Patrick on 08.09.2014.
 */
public abstract class Entity implements Comparable<Entity> {


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

  public void setVelocity(int x, int y) {
    v = Vec.create(x, y);
  }

  public void setVelocity(Vec v) {
    this.v = v;
  }

  @Override
  public int compareTo(Entity o) {
    return 0;
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

  protected abstract int getMaxSpeed();

  public double distance(Entity neighbor) {
    double sqrt = Math.sqrt(Math.pow(getX() - neighbor.getX(), 2) + Math.pow(getY() - neighbor.getY(), 2));
    return sqrt;
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

  public abstract double getSepWeight();


  public abstract double getAlignWeight();


  public abstract double getCohWeight();

  public abstract int getRadius();

  public abstract double closeRadius();

  public abstract boolean isPurgable();

  public abstract boolean isEvil(Entity n);
}
