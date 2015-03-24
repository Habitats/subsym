package subsym.gui;

import java.awt.*;
import java.awt.geom.AffineTransform;

import javax.swing.*;

import subsym.models.AIAdapter;
import subsym.models.AIAdapterListener;
import subsym.models.Vec;

/**
 * Created by Patrick on 24.08.2014.
 */
public abstract class AICanvas<T, A extends AIAdapter<T>> extends JPanel implements AIAdapterListener {

  private long delta = 0;

  private static final String TAG = AICanvas.class.getSimpleName();
  private A adapter;

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

  protected abstract void drawOutline(T entity, Graphics2D g, int x, int y, int thickness);


  protected void drawArrow(Graphics g, Vec p, Vec v) {
    Point start = new Point();
    Point end = new Point();

    start.setLocation(p.x, p.y);
    end.setLocation(p.x + v.x / 2, p.y + v.y / 2);

    createArrowShape((Graphics2D) g, start, end);
  }


  public void createArrowShape(Graphics2D g, Point fromPt, Point toPt) {
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

  private Point midpoint(Point p1, Point p2) {
    return new Point((int) ((p1.x + p2.x) / 2.0), (int) ((p1.y + p2.y) / 2.0));
  }

  protected abstract void updateMetrics();

  @Override
  public void notifyDataChanged() {
    if (System.currentTimeMillis() - delta > 10) {
      repaint();
      delta = System.currentTimeMillis();
    }
  }

  public void setAdapter(A adapter) {
    this.adapter = adapter;
    adapter.setListener(this);
  }

  public A getAdapter() {
    return adapter;
  }
}
