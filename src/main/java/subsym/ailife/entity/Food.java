package subsym.ailife.entity;

import java.awt.*;

import subsym.gui.ColorUtils;
import subsym.models.Board;
import subsym.models.entity.ImageTileEntity;

/**
 * Created by anon on 24.03.2015.
 */

public class Food extends ImageTileEntity {

  public Food(int x, int y, Board board) {
    super(x, y, board);
  }

  @Override
  protected String getResourcePath() {
    return "res/banana.png";
  }

  @Override
  public void draw(Graphics g, int x, int y) {
    super.draw(g, x, y);
    g.drawImage(getImage(), x, y, null);
    if (getDirection() != null) {
      drawArrow(g, x, y, getDirection());
    }
//    drawStringCenter(g, getDescription(), x, y, getItemWidth(), getItemHeight());
  }

  @Override
  public String getDescription() {
    return "Food";
  }

  @Override
  public Color getColor() {
    return ColorUtils.c(1);
  }
}
