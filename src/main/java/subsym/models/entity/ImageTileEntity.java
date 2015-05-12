package subsym.models.entity;

import java.awt.*;

import subsym.Log;
import subsym.models.Board;
import subsym.models.ResourceLoader;

/**
 * Created by anon on 24.03.2015.
 */
public abstract class ImageTileEntity extends TileEntity {

  private static final String TAG = ImageTileEntity.class.getSimpleName();

  public ImageTileEntity(int x, int y, Board board) {
    super(x, y, board);
  }

  protected abstract String getResourcePath();

  public Image getImage() {
    Image resource = ResourceLoader.getInstance().getResource(getResourcePath());
    Image scaledInstance = null;
    try {
      scaledInstance = resource.getScaledInstance(getBoard().getItemWidth(), getBoard().getItemHeight(), 0);
    } catch (Exception e) {
      Log.v(TAG, "DRAWING ERROR!");
    }
    return scaledInstance;
  }
}
