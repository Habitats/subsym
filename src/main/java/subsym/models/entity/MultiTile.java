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

  public MultiTile(int width, Board<TileEntity> board) {
    this.width = width;
    this.board = board;
    pieces = IntStream.range(getStartX(), getStartX() + width)//
        .mapToObj(x -> new TilePart(x, getStartY())).collect(Collectors.toList());
    pieces.forEach(board::set);
  }
  protected abstract int getStartY();

  protected abstract int getStartX();

  public void moveLeft() {
    int fromX = getX() + width - 1;
    int toX = getX() - 1;
    swap(fromX, toX, getY(), getY());
  }

  public void moveRight() {
    int fromX = getX();
    int toX = getX() + width;
    swap(fromX, toX, getY(), getY());
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

  public void moveDown() {
    pieces.stream().forEach(p -> swap(p.getX(), p.getX(), p.getY(), p.getY() - 1));
  }

  public void moveUp() {
    pieces.stream().forEach(p -> swap(p.getX(), p.getX(), p.getY(), p.getY() + 1));
  }

  private int getX() {
    return pieces.stream().mapToInt(TileEntity::getX).min().getAsInt();
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
