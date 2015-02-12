package subsym.broids.gui;

import java.awt.*;
import java.util.Collection;

import subsym.broids.BroidAdapter;
import subsym.broids.Vec;
import subsym.broids.entities.Entity;
import subsym.broids.entities.Obsticle;
import subsym.gui.AICanvas;

/**
 * Created by anon on 28.01.2015.
 */
public class BroidCanvas extends AICanvas<Entity> {

  private final int itemHeight = 10;
  private final int itemWidth = 10;
  private double horizontalScalingFactor;
  private double verticalScalingFactor;
  private final int padding = 0;

  @Override
  protected void draw(Graphics2D g) {
    drawNodes(g);
  }

  @Override
  protected int getItemHeight() {
    return itemHeight;
  }

  @Override
  protected int getItemWidth() {
    return itemWidth;
  }


  private void drawNodes(Graphics2D g) {

    Collection<Entity> items = getAdapter().getItems();
    synchronized (items) {
      // draw objects
      items.stream().forEach(broid -> {
        int x = getX(broid);
        int y = getY(broid);
        g.setColor(broid.getColor());
        g.fillOval(x, y, getItemWidth(), getItemHeight());
        g.setColor(broid.getOutlineColor());

        // with thickness 3
        drawOutline(g, x, y, 2);
      });

      // draw vectors
      if (BroidAdapter.VECTORS_ENABLED) {
        drawVectors(g, items);
      }
    }
  }

  private void drawVectors(Graphics2D g, Collection<Entity> items) {
    items.stream().filter(broid -> !(broid instanceof Obsticle)).forEach(broid -> {
      int x = getX(broid);
      int y = getY(broid);
      drawArrow(g, Vec.create(x + itemWidth / 2, y + itemHeight / 2), Vec.create(broid.v.x, -broid.v.y));
    });
  }

  @Override
  protected void drawOutline(Graphics2D g, int x, int y, int thickness) {
    for (int i = 0; i < thickness; i++) {
      g.drawOval(x + i, y + i, getItemWidth() - 2 * i, getItemHeight() - 2 * i);
    }
  }

  private int getY(Entity item) {
    return (int) (getHeight() - item.getY() * getVerticalScalingFactor()) - (padding + itemHeight);
  }

  private int getX(Entity item) {
    return (int) (item.getX() * getHorizontalScalingFactor()) + padding;
  }

  private int getCenterY(Entity item) {
    return getY(item) + getItemHeight() / 2;
  }

  private int getCenterX(Entity item) {
    return getX(item) + getItemWidth() / 2;
  }

  @Override
  protected void updateMetrics() {
    setVerticalScalingFactor(getHeight(), getAdapter().getHeight());
    setHorizontalScalingFactor(getWidth(), getAdapter().getWidth());
  }

  public void setHorizontalScalingFactor(double width, double adapterWidth) {
    this.horizontalScalingFactor = (width - (padding * 2 + itemWidth)) / adapterWidth;
  }

  public double getHorizontalScalingFactor() {
    return horizontalScalingFactor;
  }

  public void setVerticalScalingFactor(double height, double adapterHeight) {
    this.verticalScalingFactor = (height - (padding * 2 + itemHeight)) / adapterHeight;
  }

  public double getVerticalScalingFactor() {
    return verticalScalingFactor;
  }
}
