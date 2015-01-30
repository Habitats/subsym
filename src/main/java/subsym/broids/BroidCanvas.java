package subsym.broids;

import java.awt.*;
import java.util.Collection;

import subsym.gui.AICanvas;

/**
 * Created by anon on 28.01.2015.
 */
public class BroidCanvas extends AICanvas<Broid> {

  private final int itemHeight = 20;
  private final int itemWidth = 20;
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

    Collection<Broid> items = getAdapter().getItems();
    synchronized (items) {
      for (Broid item : items) {
        int x = getX(item);
        int y = getY(item);
        g.setColor(item.getColor());
        g.fillOval(x, y, getItemWidth(), getItemHeight());
        g.setColor(item.getOutlineColor());

        // with thickness 3
        drawOutline(g, x, y, 3);
      }
    }
  }

  @Override
  protected void drawOutline(Graphics2D g, int x, int y, int thickness) {
    for (int i = 0; i < thickness; i++) {
      g.drawOval(x + i, y + i, getItemWidth() - 2 * i, getItemHeight() - 2 * i);
    }
  }

  private int getY(Broid item) {
    return (int) (getHeight() - item.getY() * getVerticalScalingFactor()) - (padding + itemHeight);
  }

  private int getX(Broid item) {
    return (int) (item.getX() * getHorizontalScalingFactor()) + padding;
  }

  private int getCenterY(Broid item) {
    return getY(item) + getItemHeight() / 2;
  }

  private int getCenterX(Broid item) {
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
