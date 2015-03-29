package subsym.beertracker;

import java.awt.*;
import java.util.stream.IntStream;

import subsym.gui.ColorUtils;
import subsym.models.Board;
import subsym.models.entity.TileEntity;

/**
 * Created by Patrick on 30.03.2015.
 */
public class Tracker {

  private Board<TileEntity> board;

  public Tracker(Board<TileEntity> board) {
    this.board = board;
    IntStream.range(0, 5).mapToObj(x -> new TrackerPart(x, board)).forEach(board::set);
  }

  public boolean moveLeft() {
    int fromX = getLocation() + 4;
    int toX = getLocation() - 1;
    return swap(fromX, toX);
  }

  public boolean moveRight() {
    int fromX = getLocation();
    int toX = getLocation() + 5;
    return swap(fromX, toX);
  }

  private boolean swap(int fromX, int toX) {
    if (board.positionExist(fromX, 0) && board.positionExist(toX, 0)) {
      TileEntity part = board.get(fromX, 0);
      TileEntity empty = board.get(toX, 0);
      part.setPosition(toX, 0);
      empty.setPosition(fromX, 0);
      board.set(part);
      board.set(empty);
      board.notifyDataChanged();
      return true;
    }
    return false;
  }

  public int getLocation() {
    return IntStream.range(0, board.getWidth()).mapToObj(x -> board.get(x, 0)).filter(n -> n instanceof TrackerPart)
        .findFirst().get().getX();
  }

  private static class TrackerPart extends TileEntity {

    public TrackerPart(int x, Board board) {
      super(x, 0, board);
    }

    @Override
    public Color getColor() {
      return ColorUtils.c(4);
    }
  }
}
