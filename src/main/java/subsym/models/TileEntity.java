package subsym.models;

/**
 * Created by anon on 23.03.2015.
 */
public class TileEntity extends Entity {

  private final Board board;

  public TileEntity(int x, int y, Board board) {
    super(x, y);
    this.board = board;
  }

  @Override
  public int getItemWidth() {
    return board.getItemWidth();
  }

  @Override
  public int getItemHeight() {
    return board.getItemHeight();
  }

protected Board<TileEntity> getBoard() {
    return board;
  }
}
