package subsym.models.entity;

import java.awt.*;

import subsym.models.Vec;

/**
 * Created by Patrick on 22.03.2015.
 */
public abstract class Entity {

  private static final Color EMPTY = Color.WHITE;
  protected Vec position;
  private Color color = EMPTY;
  private Color outlineColor = Color.black;

  public Entity(int x, int y) {
    position = Vec.create(x, y);
  }

  public int getX() {
    return (int) position.getX();
  }

  public int getY() {
    return (int) position.getY();
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

  public abstract int getItemWidth();

  public abstract int getItemHeight();

  public Vec getPosition() {
    return position;
  }

  public void setPosition(double x, double y) {
    position = Vec.create(x, y);
  }
}
