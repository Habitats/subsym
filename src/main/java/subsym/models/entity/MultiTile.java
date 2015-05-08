package subsym.models.entity;

import java.awt.*;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import subsym.flatland.entity.Empty;
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
  protected Color color = Color.darkGray;
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

  public boolean moveRight(boolean shouldWrap) {
    TileEntity old;
    int oldX = shouldWrap ? (getX() + width) % board.getWidth() : getX() + width;
    if (board.positionExist(oldX, getY()) && (old = board.get(oldX, getY())) instanceof Empty) {
      pieces.stream().forEach(p -> p.setPosition((p.getX() + 1) % board.getWidth(), p.getY()));
      pieces.stream().forEach(board::set);

      old.setPosition(getX(), getY());
      board.set(old);
      x = (x + 1) % board.getWidth();
      return true;
    } else {
      collision(Direction.RIGHT);
      return false;
    }
  }

  public boolean moveLeft(boolean shouldWrap) {
    TileEntity old;
    int oldX = shouldWrap ? (getX() - 1 + board.getWidth()) % board.getWidth() : getX() - 1;
    if (board.positionExist(oldX, getY()) && (old = board.get(oldX, getY())) instanceof Empty) {
      pieces.stream().forEach(p -> p.setPosition((p.getX() - 1 + board.getWidth()) % board.getWidth(), p.getY()));
      pieces.stream().forEach(board::set);

      old.setPosition((getX() + width - 1) % board.getWidth(), getY());
      board.set(old);
      x = (x - 1 + board.getWidth()) % board.getWidth();
      return true;
    } else {
      collision(Direction.RIGHT);
      return false;
    }
  }

  protected abstract void collision(Direction right);

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
    return !pieces.stream().allMatch(p -> board.positionExist(p.getX(), padY.apply(p.getY())) && //
                                          board.get(p.getX(), padY.apply(p.getY())) instanceof Empty);
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

  public int getX() {
    return x;
  }

  public Stream<TilePart> stream() {
    return pieces.stream();
  }

  @Override
  public String toString() {
    return String.format("%s x: %3d, y: %3d, Width: %3d", TAG, getX(), getY(), getWidth());
  }

  protected int getY() {
    return pieces.get(0).getY();
  }

  protected abstract Color getColor();

  public int getWidth() {
    return width;
  }

  protected void setColor(Color color) {
    this.color = color;
  }

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
