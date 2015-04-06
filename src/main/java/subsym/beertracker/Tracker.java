package subsym.beertracker;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.IntFunction;
import java.util.stream.Collectors;
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
  private boolean pulling;
  private boolean interrupt;
  private List<Boolean> sensors;
  private List<TrackerListener> listeners;
  private int caught;
  private int avoided;
  private int crashed;

  public Tracker(Board<TileEntity> board) {
    super(5, board);
    listeners = new ArrayList<>();
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

  public List<Boolean> getSensors() {
    return sensors;
  }

  public boolean contains(TileEntity v) {
    return pieces.contains(v);
  }

  public void onAvoided(Piece piece) {
    Log.v(TAG, "Avoided: \t" + piece);
    fade(50, 70, () -> setColor(Color.darkGray));
    listeners.forEach(TrackerListener::onAvoided);
    avoided++;
  }

  public void onCaught(Piece piece) {
    Log.v(TAG, "Caught: \t" + piece);
    fade(20, 40, () -> fade(40, 20, () -> setColor(Color.darkGray)));
    listeners.forEach(TrackerListener::onCaught);
    caught++;
  }

  public void onCrash(Piece piece) {
    Log.v(TAG, "Crashed: \t" + piece);
    fade(80, 100, () -> fade(100, 80, () -> setColor(Color.darkGray)));
    listeners.forEach(TrackerListener::onCrash);
    crashed++;
  }

  private void fade(int from, int to, Runnable callback) {
    new Thread(() -> {
      double rounds = 50;
      double resolution = Math.abs(from - to) / rounds;
      IntStream.range(0, (int) rounds).forEach(i -> {
        if (interrupt) {
          interrupt = false;
          return;
        }
        double colorValue = from < to ? (from + (i * resolution)) : (from - (i * resolution));
        double normalizedValue = colorValue / 100.;
//        Log.v(TAG, normalizedValue);
        setColor(ColorUtils.toHsv(normalizedValue, 1));
        board.notifyDataChanged();
        try {
          Thread.sleep(3);
        } catch (InterruptedException e) {
        }
      });

      callback.run();
    }).start();
  }

  public void pull() {
    pulling = true;
  }

  public boolean isPulling() {
    boolean isPulling = pulling;
    pulling = false;
    return isPulling;
  }

  public void sense(Piece piece) {
    IntFunction<Boolean> isSensed = x -> (x >= piece.getX() && x < piece.getX() + piece.getWidth());
    sensors = IntStream.range(getX(), getX() + getWidth()).mapToObj(isSensed).collect(Collectors.toList());

    AtomicInteger i = new AtomicInteger();
    pieces.stream().sorted((p1, p2) -> Integer.compare(p1.getX(), p2.getX()))
        .forEach(p -> p.setOutlineColor(sensors.get(i.getAndIncrement()) ? piece.getColor() : Color.BLACK));
  }

  public void addListener(TrackerListener listener) {
    listeners.add(listener);
  }

  public int getCaught() {
    return caught;
  }

  public int getAvoided() {
    return avoided;
  }

  public int getCrashed() {
    return crashed;
  }

  public int fitness() {
    return (caught * 2) - crashed - avoided;
  }

  public void move(List<Double> outputs) {
//    double max = Math.round(Math.max(outputs.get(0), outputs.get(1)));
//    double min = Math.floor(Math.min(outputs.get(0), outputs.get(1)));
//    int multiplier = (int) Math.round(max / min);
//    Log.v(TAG, multiplier  + " "  + min + " " + max);
    int multiplier = 1;
    if (outputs.get(0) > outputs.get(1)) {
      IntStream.range(0, multiplier).forEach(i -> moveRight(true));
    } else {
      IntStream.range(0, multiplier).forEach(i -> moveLeft(true));
    }
  }
}
