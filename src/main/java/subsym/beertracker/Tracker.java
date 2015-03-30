package subsym.beertracker;

import java.awt.*;

import subsym.gui.ColorUtils;
import subsym.models.Board;
import subsym.models.entity.MultiTile;
import subsym.models.entity.TileEntity;

/**
 * Created by Patrick on 30.03.2015.
 */
public class Tracker extends MultiTile {

  public Tracker(Board<TileEntity> board) {
    super(5, board);
  }

  @Override
  protected int getStartY() {
    return 0;
  }

  @Override
  protected int getStartX() {
    return board.getWidth() / 2 - 3;
  }

  @Override
  protected Color getColor() {
    return ColorUtils.c(4);
  }
}
