package subsym.models.entity;

import java.awt.*;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import subsym.Log;
import subsym.ailife.entity.Empty;
import subsym.gui.Direction;
import subsym.models.Board;

/**
 * Created by Patrick on 30.03.2015.
 */
public abstract class MultiTile {

  private static final String TAG = MultiTile.class.getSimpleName();
  protected final List<TilePart> pieces;
  protected Board<TileEntity> board;
  protected int width;
  private int x;

  public MultiTile(int width, Board<TileEntity> board) {
    this.width = width;
    this.board = board;
    x = getStartX();
    pieces = IntStream.range(getStartX(), getStartX() + width)//
        .mapToObj(x -> new TilePart(x, getStartY())).collect(Collectors.toList());
    pieces.forEach(board::set);
  }

  protected abstract int getStartY();

  protected abstract int getStartX();

  public boolean moveLeft(boolean shouldWrap) {
    int fromX = shouldWrap ? (getX() + width - 1 + board.getWidth()) % board.getWidth() : getX() + width - 1;
    int toX = shouldWrap ? (getX() + board.getWidth() - 1) % board.getWidth() : getX() - 1;
    if ((board.get(toX, getY()) instanceof Empty)) {
      x = swap(fromX, toX, getY(), getY()) ? (x - 1 + board.getWidth()) % board.getWidth() : x;
      return true;
    } else {
      collision(Direction.LEFT);
      return false;
    }
  }

  public boolean moveRight(boolean shouldWrap) {
    int fromX = shouldWrap ? getX() % board.getWidth() : getX();
    int toX = shouldWrap ? (getX() + width) % board.getWidth() : getX() + width;
    if ((board.get(toX, getY()) instanceof Empty)) {
      x = swap(fromX, toX, getY(), getY()) ? (x + 1) % board.getWidth() : x;
      return true;
    } else {
      collision(Direction.RIGHT);
      return false;
    }
  }

  private void collision(Direction dir) {
    Log.v(TAG, "Collision: " + dir.name());
  }

  public boolean moveDown(boolean shouldWrap) {
    Function<Integer, Integer> padY = y -> shouldWrap ? (y - 1 + board.getHeight()) % board.getHeight() : (y - 1);
    if (!isCollisionY(padY)) {
      pieces.stream().forEach(p -> swap(p.getX(), p.getX(), p.getY(), padY.apply(p.getY())));
      return true;
    } else {
      collision(Direction.DOWN);
      return false;
    }
  }

  public boolean moveUp(boolean shouldWrap) {
    Function<Integer, Integer> padY = y -> shouldWrap ? (y + 1) % board.getHeight() : y + 1;
    if (!isCollisionY(padY)) {
      pieces.stream().forEach(p -> swap(p.getX(), p.getX(), p.getY(), padY.apply(p.getY())));
      return true;
    } else {
      collision(Direction.UP);
      return false;
    }
  }

  private boolean isCollisionY(Function<Integer, Integer> padY) {
    return !pieces.stream().allMatch(p -> board.get(p.getX(), padY.apply(p.getY())) instanceof Empty);
  }

  private boolean swap(int fromX, int toX, int fromY, int toY) {
    if (board.positionExist(fromX, fromY) && board.positionExist(toX, toY)) {
      TileEntity part = board.get(fromX, fromY);
      TileEntity empty = board.get(toX, toY);
      part.setPosition(toX, toY);
      empty.setPosition(fromX, fromY);
      board.set(part);
      board.set(empty);
      board.notifyDataChanged();
      return true;
    }
    return false;
  }

  private int getX() {
    return x;
  }

  private int getY() {
    return pieces.get(0).getY();
  }

  protected abstract Color getColor();

  protected class TilePart extends TileEntity {

    public TilePart(int x, int y) {
      super(x, y, board);
    }

    @Override
    public Color getColor() {
      return MultiTile.this.getColor();
    }
  }
}
