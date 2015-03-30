package subsym.beertracker;

import java.awt.*;
import java.util.stream.IntStream;

import subsym.Log;
import subsym.gui.ColorUtils;
import subsym.gui.Direction;
import subsym.models.Board;
import subsym.models.entity.MultiTile;
import subsym.models.entity.TileEntity;

/**
 * Created by Patrick on 30.03.2015.
 */
public class Tracker extends MultiTile {

  private static final String TAG = Tracker.class.getSimpleName();
  private Color color = Color.darkGray;

  public Tracker(Board<TileEntity> board) {
    super(5, board);
  }

  @Override
  protected int getStartY() {
    return 0;
  }

  @Override
  protected int getStartX() {
    return board.getWidth() / 2 - 3;
  }

  @Override
  protected void collision(Direction dir) {
    Log.v(TAG, "Collision: " + dir.name());
  }

  @Override
  protected Color getColor() {
    return color;
  }

  private void setColor(Color color) {
    this.color = color;
  }

  public boolean contains(TileEntity v) {
    return pieces.contains(v);
  }

  public void onAvoided(Piece piece) {
    Log.v(TAG, "Avoided: " + piece);
    fade(50, 70, () -> setColor(Color.darkGray));
  }

  public void onCaught(Piece piece) {
    Log.v(TAG, "Caught: " + piece);
    fade(20, 40, () -> fade(40, 20, () -> setColor(Color.darkGray)));
  }

  public void onCrash(Piece piece) {
    Log.v(TAG, "Crashed:" + piece);
    fade(80, 100, () -> fade(100, 80, () -> setColor(Color.darkGray)));
  }

  private void fade(int from, int to, Runnable callback) {
    new Thread(() -> {
      double rounds = 200;
      double resolution = Math.abs(from - to) / rounds;
      IntStream.range(0, (int) rounds).forEach(i -> {
        double colorValue = from < to ? (from + (i * resolution)) : (from - (i * resolution));
        double normalizedValue = colorValue / 100.;
//        Log.v(TAG, normalizedValue);
        setColor(ColorUtils.toHsv(normalizedValue, 1));
        board.notifyDataChanged();
        try {
          Thread.sleep(2);
        } catch (InterruptedException e) {
        }
      });

      callback.run();
    }).start();
  }
}
