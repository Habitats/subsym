package subsym.boids.gui;

import java.awt.*;
import java.util.Collection;

import subsym.boids.BoidAdapter;
import subsym.boids.entities.BroidEntity;
import subsym.boids.entities.Obstacle;
import subsym.gui.AICanvas;
import subsym.models.AIAdapter;
import subsym.models.Vec;

/**
 * Created by anon on 28.01.2015.
 */
public class BoidCanvas extends AICanvas<BroidEntity, AIAdapter<BroidEntity>> {

  private double horizontalScalingFactor;
  private double verticalScalingFactor;
  private final int padding = 0;

  @Override
  protected void draw(Graphics2D g) {
    drawNodes(g);
  }

  private void drawNodes(Graphics2D g) {

    Collection<BroidEntity> items = getAdapter().getItems();
    synchronized (items) {
      // draw objects
      items.stream().forEach(broid -> {
        int x = getX(broid);
        int y = getY(broid);
        g.setColor(broid.getColor());
        g.fillOval(x, y, broid.getItemWidth(), broid.getItemHeight());
        g.setColor(broid.getOutlineColor());

        // with thickness 3
        drawOutline(broid, g, x, y, 2);
      });

      // draw vectors
      if (BoidAdapter.VECTORS_ENABLED) {
        drawVectors(g, items);
      }
    }
  }

  private void drawVectors(Graphics2D g, Collection<BroidEntity> items) {
    items.stream().filter(broid -> !(broid instanceof Obstacle)).forEach(broid -> {
      int x = getX(broid);
      int y = getY(broid);
      drawArrow(g, Vec.create(x + broid.getItemWidth() / 2, y + broid.getItemHeight() / 2), Vec.create(broid.v.x, -broid.v.y));
    });
  }

  @Override
  protected void drawOutline(BroidEntity broid, Graphics2D g, int x, int y, int thickness) {
    for (int i = 0; i < thickness; i++) {
      g.drawOval(x + i, y + i, broid.getItemWidth() - 2 * i, broid.getItemHeight() - 2 * i);
    }
  }

  private int getY(BroidEntity item) {
    return (int) (getHeight() - item.getY() * getVerticalScalingFactor()) - (padding + item.getItemHeight());
  }

  private int getX(BroidEntity item) {
    return (int) (item.getX() * getHorizontalScalingFactor()) + padding;
  }

  @Override
  protected void updateMetrics() {
    setVerticalScalingFactor(getHeight(), getAdapter().getHeight());
    setHorizontalScalingFactor(getWidth(), getAdapter().getWidth());
  }

  public void setHorizontalScalingFactor(double width, double adapterWidth) {
    this.horizontalScalingFactor = width / adapterWidth;
  }

  public double getHorizontalScalingFactor() {
    return horizontalScalingFactor;
  }

  public void setVerticalScalingFactor(double height, double adapterHeight) {
    this.verticalScalingFactor = height / adapterHeight;
  }

  public double getVerticalScalingFactor() {
    return verticalScalingFactor;
  }
}
