package subsym.models.entity;

import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import subsym.models.Board;

/**
 * Created by Patrick on 30.03.2015.
 */
public abstract class MultiTile {

  protected final List<TilePart> pieces;
  protected Board<TileEntity> board;
  protected int width;
  private int x;

  public MultiTile(int width, Board<TileEntity> board) {
    this.width = width;
    this.board = board;
    x = getStartX();
    pieces = IntStream.range(getStartX(), getStartX() + width)//
        .mapToObj(x -> new TilePart(x, getStartY(), x == getStartX())).collect(Collectors.toList());
    pieces.forEach(board::set);
  }

  protected abstract int getStartY();

  protected abstract int getStartX();

  public void moveLeft(boolean shouldWrap) {
    int fromX = shouldWrap ? (getX() + width - 1 + board.getWidth()) % board.getWidth() : getX() + width - 1;
    int toX = shouldWrap ? (getX() + board.getWidth() - 1) % board.getWidth() : getX() - 1;
    x = swap(fromX, toX, getY(), getY()) ? (x - 1 + board.getWidth()) % board.getWidth() : x;
  }

  public void moveRight(boolean shouldWrap) {
    int fromX = shouldWrap ? getX() % board.getWidth() : getX();
    int toX = shouldWrap ? (getX() + width) % board.getWidth() : getX() + width;
    x = swap(fromX, toX, getY(), getY()) ? (x + 1) % board.getWidth() : x;
  }

  public void moveDown(boolean shouldWrap) {
    pieces.stream().forEach(
        p -> swap(p.getX(), p.getX(), p.getY(), shouldWrap ? (p.getY() - 1 + board.getHeight()) % board.getHeight() : (p.getY() - 1)));
  }

  public void moveUp(boolean shouldWrap) {
    pieces.stream().forEach(
        p -> swap(p.getX(), p.getX(), p.getY(), shouldWrap ? (p.getY() + 1) % board.getHeight() : p.getY() + 1));
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

    private boolean isFirst;

    public TilePart(int x, int y, boolean isFirst) {
      super(x, y, board);
      this.isFirst = isFirst;
    }

    @Override
    public Color getColor() {
      return MultiTile.this.getColor();
    }

    public boolean isFirst() {
      return isFirst;
    }
  }
}
