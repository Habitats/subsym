package subsym.ailife.entity;

import java.awt.*;

import subsym.gui.ColorUtils;
import subsym.models.Board;
import subsym.models.entity.ImageTileEntity;

/**
 * Created by anon on 24.03.2015.
 */
public class Poison extends ImageTileEntity {

  public Poison(int x, int y, Board board) {
    super(x, y, board);
  }

  @Override
  public void draw(Graphics g, int x, int y) {
    super.draw(g, x, y);
    g.drawImage(getImage(), x, y, null);
  }

  @Override
  protected String getResourcePath() {
    return "res/skull.png";
  }

  @Override
  public String getDescription() {
    return "Poison";
  }

  @Override
  public Color getColor() {
    return ColorUtils.c(0);
  }


}

