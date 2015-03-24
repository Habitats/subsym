package subsym.models.entity;

import java.awt.*;

import subsym.models.Board;
import subsym.models.ResourceLoader;

/**
 * Created by anon on 24.03.2015.
 */
public abstract class ImageTileEntity extends TileEntity {

  public ImageTileEntity(int x, int y, Board board) {
    super(x, y, board);
  }

  protected abstract String getResourcePath();

  public Image getImage() {
    Image resource = ResourceLoader.getInstance().getResource(getResourcePath());
    return resource.getScaledInstance(getBoard().getItemWidth(), getBoard().getItemHeight(), 0);
  }
}
