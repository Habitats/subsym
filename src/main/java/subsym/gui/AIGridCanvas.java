package subsym.gui;

import java.awt.*;

import subsym.models.Board;
import subsym.models.entity.TileEntity;

/**
 * Created by Patrick on 23.03.2015.
 */
public class AIGridCanvas<T extends TileEntity> extends AICanvas<T, Board<T>> {

  private int oldHeight;
  private int oldWidth;

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

  protected void paintTile(Graphics g, T entity) {
    // put origin to be the left bottom corner
    int x = entity.getX() * entity.getItemWidth();
    int y = getHeight() - entity.getItemHeight() - entity.getY() * entity.getItemHeight();
    if (entity.isModified()) {
      entity.draw(g, x, y);
      drawOutline(entity, (Graphics2D) g, x, y, 2);
//      entity.reset();
    }
  }

  @Override
  protected void updateMetrics() {
    if (getAdapter() == null || (oldHeight == getHeight() && oldWidth == getWidth())) {
      return;
    }
    int tileHeight = getHeight() / getAdapter().getHeight();
    int tileWidth = getWidth() / getAdapter().getWidth();
    getAdapter().setItemHeight(tileHeight);
    getAdapter().setItemWidth(tileWidth);
    // this is resource heavy
    getAdapter().getItems().stream().forEach(TileEntity::setModified);
    oldHeight = getHeight();
    oldWidth = getWidth();
  }
}
