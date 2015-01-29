package subsym.Models;

import java.awt.*;

/**
 * Created by Patrick on 08.09.2014.
 */
public abstract class Entity implements Comparable<Entity> {


  private static final Color EMPTY = Color.WHITE;
  protected final int index;
  protected Vec p;
  protected Vec v;

  private Color color = EMPTY;
  private String desc = "";
  private Color outlineColor = Color.black;
  private int value;
  private Component velocity;

  public Entity(int x, int index, int y) {
    this.index = index;
    p = Vec.create(x,y);
  }

  public void setVelocity(int x, int y) {
    v = Vec.create(x,y);
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

  public String getId() {
    return "n" + index;
  }

  public int getY() {
    return (int) p.y;
  }

  public String getDescription() {
    return desc;
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

  public abstract void update();

  public double distance(Entity neighbor) {
    double sqrt = Math.sqrt(Math.pow(getX() - neighbor.getX(), 2) + Math.pow(getY() - neighbor.getY(), 2));
    return sqrt;
  }


  public Vec getVelocity() {
    return v;
  }

  public void limitVelocity() {
    if (v.lenght() > 50) {
      v.divide(v.lenght()).multiply(50);
    }
  }
}
