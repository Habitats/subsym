package subsym.models.entity;

import java.awt.*;

import subsym.Log;
import subsym.gui.AICanvas;
import subsym.gui.Direction;
import subsym.gui.Theme;
import subsym.models.Board;

/**
 * Created by anon on 23.03.2015.
 */
public abstract class TileEntity extends Entity {

  private static final String TAG = TileEntity.class.getSimpleName();
  private final Board board;
  private boolean modified;

  public TileEntity(int x, int y, Board board) {
    super(x, y);
    this.board = board;
    modified = true;
  }

  @Override
  public int getItemWidth() {
    return board.getItemWidth();
  }

  @Override
  public int getItemHeight() {
    return board.getItemHeight();
  }

  protected void drawStringCenter(Graphics g, String s, int XPos, int YPos, int itemWidth, int itemHeight) {
    Graphics2D g2d = (Graphics2D) g;
    Font font = new Font("Consolas", Font.PLAIN, 14);
    g.setFont(font);
    g.setColor(Theme.getForeground());
    int stringLen = (int) g2d.getFontMetrics().getStringBounds(s, g2d).getWidth();
    int stringHeight = (int) g2d.getFontMetrics().getStringBounds(s, g2d).getHeight();
    int offsetWidth = itemWidth / 2 - stringLen / 2;
    int offsetHeight = itemHeight - stringHeight / 2;
    g2d.drawString(s, offsetWidth + XPos, offsetHeight + YPos);
  }

  protected void drawArrow(Graphics g, int x, int y, Direction direction) {
    g.setColor(Color.black);
    Point start = new Point();
    Point end = new Point();
    int arrowLength = 40;
    switch (direction) {
      case UP:
        start.setLocation(x + (getItemWidth() / 2), y + (getItemHeight() / 2) + (arrowLength / 2));
        end.setLocation(x + (getItemWidth() / 2), y + (getItemHeight() / 2) - (arrowLength / 2));
        break;
      case RIGHT:
        start.setLocation(x + (getItemWidth() / 2) - (arrowLength / 2), y + (getItemHeight() / 2));
        end.setLocation(x + (getItemWidth() / 2) + (arrowLength / 2), y + (getItemHeight() / 2));
        break;
      case DOWN:
        start.setLocation(x + (getItemWidth() / 2), y + (getItemHeight() / 2) - (arrowLength / 2));
        end.setLocation(x + (getItemWidth() / 2), y + (getItemHeight() / 2) + (arrowLength / 2));
        break;
      case LEFT:
        start.setLocation(x + (getItemWidth() / 2) + (arrowLength / 2), y + (getItemHeight() / 2));
        end.setLocation(x + (getItemWidth() / 2) - (arrowLength / 2), y + (getItemHeight() / 2));
        break;
    }
    AICanvas.createArrowShape((Graphics2D) g, start, end);
  }

  public void setModified() {
    modified = true;
    Log.v(TAG, "Modified ...");
  }

  @Override
  public void setColor(Color color) {
    super.setColor(color);
    setModified();
  }

  @Override
  public void setPosition(double x, double y) {
    if (getX() == x && getY() == y) {
      return;
    }
    super.setPosition(x, y);
    setModified();
  }

  public void draw(Graphics g, int x, int y) {
    g.setColor(getColor());
    g.fillRect(x, y, getItemWidth(), getItemHeight());
  }

  protected Board<TileEntity> getBoard() {
    return board;
  }

  public String getDescription() {
    return "";
  }

  public boolean isModified() {
    return modified;
  }

  public void reset() {
    modified = false;
  }
}
