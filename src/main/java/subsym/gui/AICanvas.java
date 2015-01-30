package subsym.gui;

import java.awt.*;
import java.awt.geom.AffineTransform;

import javax.swing.*;

import subsym.Models.AIAdapter;
import subsym.Models.AIAdapterListener;
import subsym.broids.models.Entity;

/**
 * Created by Patrick on 24.08.2014.
 */
public abstract class AICanvas<T extends Entity> extends JPanel implements AIAdapterListener {

  public enum Direction {
    UP(0), RIGHT(1), DOWN(2), LEFT(3);
    private final int i;

    Direction(int i) {
      this.i = i;
    }

    public int getId() {
      return i;
    }

    public static Direction getDirection(Integer value) {
      for (Direction dir : Direction.values()) {
        if (dir.getId() == value) {
          return dir;
        }
      }
      return null;
    }
  }

  private static final String TAG = AICanvas.class.getSimpleName();
  private AIAdapter adapter;
  public boolean drawLabels;

  public AICanvas() {
    super();
  }

  @Override
  public void paintComponent(Graphics g) {
    super.paintComponent(g);
    ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    if (getAdapter() != null) {
      updateMetrics();
      draw((Graphics2D) g);
    }
  }

  protected abstract void draw(Graphics2D g);

  protected abstract void drawOutline(Graphics2D g, int x, int y, int thickness);

  protected void drawStringCenter(Graphics g, String s, int XPos, int YPos) {
    Graphics2D g2d = (Graphics2D) g;
    Font font = new Font("Consolas", Font.PLAIN, 14);
    g.setFont(font);
    g.setColor(Theme.getForeground());
    int stringLen = (int) g2d.getFontMetrics().getStringBounds(s, g2d).getWidth();
    int stringHeight = (int) g2d.getFontMetrics().getStringBounds(s, g2d).getHeight();
    int offsetWidth = getItemWidth() / 2 - stringLen / 2;
    int offsetHeight = getItemHeight() - stringHeight / 2;
    g2d.drawString(s, offsetWidth + XPos, offsetHeight + YPos);
  }

  public static void createArrowShape(Graphics2D g, Point fromPt, Point toPt) {
    Polygon arrowPolygon = new Polygon();
    arrowPolygon.addPoint(-6, 1);
    arrowPolygon.addPoint(3, 1);
    arrowPolygon.addPoint(3, 3);
    arrowPolygon.addPoint(6, 0);
    arrowPolygon.addPoint(3, -3);
    arrowPolygon.addPoint(3, -1);
    arrowPolygon.addPoint(-6, -1);

    Point midPoint = midpoint(fromPt, toPt);

    double rotate = Math.atan2(toPt.y - fromPt.y, toPt.x - fromPt.x);

    AffineTransform transform = new AffineTransform();
    transform.translate(midPoint.x, midPoint.y);
    double ptDistance = fromPt.distance(toPt);
    double scale = ptDistance / 12.0; // 12 because it's the length of the arrow polygon.
    transform.scale(scale, scale);
    transform.rotate(rotate);

    Shape shape = transform.createTransformedShape(arrowPolygon);
    g.fill(shape);
  }

  private static Point midpoint(Point p1, Point p2) {
    return new Point((int) ((p1.x + p2.x) / 2.0), (int) ((p1.y + p2.y) / 2.0));
  }

  protected abstract int getItemHeight();

  protected abstract int getItemWidth();

  protected abstract void updateMetrics();

  @Override
  public void notifyDataChanged() {
    repaint();
  }

  public void setAdapter(AIAdapter<T> adapter) {
    this.adapter = adapter;
    adapter.setListener(this);
  }

  public void drawLabels(boolean selected) {
    this.drawLabels = selected;
    repaint();
  }

  public AIAdapter<T> getAdapter() {
    return adapter;
  }
}
