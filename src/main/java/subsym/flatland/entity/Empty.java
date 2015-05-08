package subsym.flatland.entity;

import java.awt.*;

import subsym.models.Board;
import subsym.models.entity.TileEntity;

/**
 * Created by anon on 24.03.2015.
 */

public class Empty extends TileEntity {

  public Empty(int x, int y, Board<TileEntity> board) {
    super(x, y, board);
  }

  @Override
  public Color getColor() {
    return Color.lightGray;
  }

  @Override
  public void draw(Graphics g, int x, int y) {
    super.draw(g, x, y);
//    g.setColor(getColor());
//    g.fillRect(x, y, getItemWidth(), getItemHeight());
    if (getDirection() != null) {
      drawArrow(g, x, y, getDirection());
    }
    if (getDescription().length() > 0) {
      drawStringCenter(g, getDescription(), x, y, getItemWidth(), getItemHeight());
    }
  }
}

