package subsym.beertracker;

import java.awt.*;
import java.util.stream.IntStream;

import subsym.ailife.entity.Empty;
import subsym.gui.ColorUtils;
import subsym.gui.Direction;
import subsym.models.Board;
import subsym.models.entity.MultiTile;
import subsym.models.entity.TileEntity;

/**
 * Created by Patrick on 30.03.2015.
 */
public class Piece extends MultiTile {

  private static final String TAG = Piece.class.getSimpleName();
  private Tracker tracker;

  public Piece(Board<TileEntity> board, int width, Tracker tracker) {
    super(width, board);
    this.tracker = tracker;
  }

  @Override
  protected int getStartY() {
    return board.getHeight() - 1;
  }

  @Override
  protected int getStartX() {
    return 0;
  }

  protected void collision(Direction dir) {
//    Log.v(TAG, "Collision: " + dir.name());
    if (dir == Direction.DOWN) {
      if (getY() == 0) {
        tracker.onAvoided(this);
      } else if (IntStream.range(getX(), getX() + getWidth())//
          .mapToObj(x -> board.get(x, getY() - 1)).allMatch(v -> tracker.contains(v))) {
        tracker.onCaught(this);
      } else {
        tracker.onCrash(this);
      }

      dispose();
    }
  }

  private void dispose() {
    pieces.stream().forEach(p -> board.set(new Empty(p.getX(), p.getY(), board)));
  }

  @Override
  protected Color getColor() {
    return ColorUtils.toHsv(width / 10., 1);
  }

  public void moveBottom() {
    pieces.stream().forEach(p -> {
      int oldY = p.getY();
      p.setPosition(p.getX(), 1);
      board.set(p);
      board.set(new Empty(p.getX(), oldY, board));
    });
    board.notifyDataChanged();
  }
}

