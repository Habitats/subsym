package subsym.ailife.entity;

import java.awt.*;

import subsym.gui.ColorUtils;
import subsym.models.Board;
import subsym.models.TileEntity;

/**
 * Created by anon on 24.03.2015.
 */
public class Poison extends TileEntity {

  public Poison(int x, int y, Board board) {
    super(x, y, board);
  }

  @Override
  public String getDescription() {
    return "Poison";
  }

  @Override
  public Color getColor() {
    return ColorUtils.c(1);
  }
}

