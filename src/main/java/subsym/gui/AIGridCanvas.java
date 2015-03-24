package subsym.gui;

import java.awt.*;

import subsym.models.Board;
import subsym.models.TileEntity;

/**
 * Created by Patrick on 23.03.2015.
 */
public class AIGridCanvas<T extends TileEntity> extends AICanvas<T, Board<T>> {

  public AIGridCanvas() {
    setBackground(Color.BLACK);
  }

  @Override
  protected void draw(Graphics2D g) {
    paintsBoard(g);
  }

  @Override
  protected void drawOutline(T entity, Graphics2D g, int x, int y, int thickness) {
    g.setColor(entity.getOutlineColor());
    for (int i = 0; i < thickness; i++) {
      g.drawRect(x + i, y + i, entity.getItemWidth() - 2 * i, entity.getItemHeight() - 2 * i);
    }
  }

  private void paintsBoard(Graphics g) {
    if (getAdapter() != null) {
      for (int x = 0; x < getAdapter().getWidth(); x++) {
        for (int y = 0; y < getAdapter().getHeight(); y++) {
          T entity = (T) ((Board) getAdapter()).get(x, y);
          paintTile(g, entity);
        }
      }
    }
  }

  protected void drawStringCenter(Graphics g, String s, int XPos, int YPos) {
    Graphics2D g2d = (Graphics2D) g;
    Font font = new Font("Consolas", Font.PLAIN, 14);
    g.setFont(font);
    g.setColor(Theme.getForeground());
    int stringLen = (int) g2d.getFontMetrics().getStringBounds(s, g2d).getWidth();
    int stringHeight = (int) g2d.getFontMetrics().getStringBounds(s, g2d).getHeight();
    int offsetWidth = getAdapter().getItemWidth() / 2 - stringLen / 2;
    int offsetHeight = getAdapter().getItemHeight() - stringHeight / 2;
    g2d.drawString(s, offsetWidth + XPos, offsetHeight + YPos);
  }

  protected void paintTile(Graphics g, T entity) {
    // put origin to be the left bottom corner
    int x = entity.getX() * entity.getItemWidth();
    int y = getHeight() - entity.getItemHeight() - entity.getY() * entity.getItemHeight();

    drawTile(g, entity, x, y);

    drawStringCenter(g, entity.getDescription(), x, y);

    drawOutline(entity, (Graphics2D) g, x, y, 2);
  }

  protected void drawTile(Graphics g, T entity, int x, int y) {
    g.setColor(entity.getColor());
    g.fillRect(x, y, entity.getItemWidth(), entity.getItemHeight());
  }

  @Override
  protected void updateMetrics() {
    if (getAdapter() == null) {
      return;
    }
    int tileHeight = getHeight() / getAdapter().getHeight();
    int tileWidth = getWidth() / getAdapter().getWidth();
    getAdapter().setItemHeight(tileHeight);
    getAdapter().setItemWidth(tileWidth);
  }
}
