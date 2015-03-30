package subsym.beertracker;

import java.awt.*;

import subsym.gui.ColorUtils;
import subsym.models.Board;
import subsym.models.entity.MultiTile;
import subsym.models.entity.TileEntity;

/**
 * Created by Patrick on 30.03.2015.
 */
public class Piece extends MultiTile {

  public Piece(Board<TileEntity> board, int width) {
    super(width, board);
  }

  @Override
  protected int getStartY() {
    return board.getHeight() - 1;
  }

  @Override
  protected int getStartX() {
    return 0;
  }

  @Override
  protected Color getColor() {
    return ColorUtils.toHsv(width / 10., 1);
  }
}
