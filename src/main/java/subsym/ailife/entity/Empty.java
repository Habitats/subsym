package subsym.ailife.entity;

import java.awt.*;

import subsym.gui.ColorUtils;
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
    return ColorUtils.c(2);
  }

  @Override
  public void draw(Graphics g, int x, int y) {
    g.setColor(getColor());
    g.fillRect(x, y, getItemWidth(), getItemHeight());
  }
}

