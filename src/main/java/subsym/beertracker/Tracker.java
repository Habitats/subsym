package subsym.beertracker;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
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
  private int goodCrash;
  private int goodAvoid;
  private int badAvoid;
  private int badCrash;
  private BeerGame beerGame;
  private double numPulls;
  private int numGoodPull;
  private int numFailPull;
  private int numBadPull;

  public Tracker(Board<TileEntity> board, BeerGame beerGame) {
    super(5, board);
    this.beerGame = beerGame;
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
//    Log.v(TAG, "Collision: " + dir.name());
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
//    Log.v(TAG, "Avoided: \t" + piece);
    if (piece.getWidth() < 5) {
      fade(50, 70, () -> setColor(Color.darkGray));
      goodAvoid++;
    } else {
      fade(20, 40, () -> fade(40, 20, () -> setColor(Color.darkGray)));
      badAvoid++;
    }

    listeners.forEach(TrackerListener::onAvoided);
  }

  public void onCaught(Piece piece) {
//    Log.v(TAG, "Caught: \t" + piece);
    fade(20, 40, () -> fade(40, 20, () -> setColor(Color.darkGray)));
    listeners.forEach(TrackerListener::onCaught);
    caught++;
  }

  public void onCrash(Piece piece) {
//    Log.v(TAG, "Crashed: \t" + piece);
    fade(80, 100, () -> fade(100, 80, () -> setColor(Color.darkGray)));
    listeners.forEach(TrackerListener::onCrash);
    if (piece.getWidth() < 5) {
      goodCrash++;
    } else {
      badCrash++;
    }
  }

  private void fade(int from, int to, Runnable callback) {
    if (!beerGame.isVisible()) {
      return;
    }
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
    numPulls++;
  }

  public boolean isPulling() {
    boolean isPulling = pulling;
    pulling = false;
    return isPulling;
  }

  public void sense(Piece piece) {
    sensors = pieces.stream()//
        .mapToInt(t -> t.getX()) //
        .mapToObj(trackerX -> piece.stream().mapToInt(TilePart::getX).anyMatch(pieceX -> pieceX == trackerX))//
        .collect(Collectors.toList());

    sensors.add(isCollidingLeft());
    sensors.add(isCollidingRight());
    AtomicInteger i = new AtomicInteger();
    pieces.stream().forEach(p -> p.setOutlineColor(sensors.get(i.getAndIncrement()) ? piece.getColor() : Color.BLACK));
  }

  private boolean isCollidingLeft() {
    return getX() == 0;
  }

  private Boolean isCollidingRight() {
    return getX() + getWidth() == board.getWidth();
  }

  public void addListener(TrackerListener listener) {
    listeners.add(listener);
  }

  public int getCaught() {
    return caught;
  }

  public int getAvoided() {
    return goodAvoid + badAvoid;
  }

  public int getCrash() {
    return goodCrash + badCrash;
  }

  public double calculateScore(double numBad, double numGood) {
    switch (beerGame.getScenario()) {
      case WRAP:
        return (caught + badAvoid) / (numGood + numBad);
      case NO_WRAP:
//        return (caught + badAvoid) / (numGood + numBad);
        return caught / numGood;
      case PULL:
//        return (caught + badAvoid) / (numGood + numBad);
        double pullScore = (numGoodPull / numGood) - (numBadPull / numBad);
        double score = pullScore;
        double max = (numGood + numBad) * 1;
        double totalScore = score;
//        Log.v(TAG, String.format(
//            "Score: %8.3f - Max: %8.3f - Good Pull: %4d - Bad Pull: %4d - Good: %3.0f - Bad: %3.0f - Pull Score: %8.3f - Score: %8.3f",
//            score, max, numGoodPull, numBadPull, numGood, numBad, pullScore, totalScore));
        if (totalScore > 1) {
          Log.v(TAG, "wut");
        }
        return totalScore;
      default:
        throw new IllegalStateException("Invalid scenario!");
    }
  }

  public void move(List<Double> outputs, boolean shouldWrap) {
    Double left = outputs.get(0);
    Double right = outputs.get(1);
    newMove(left, right, shouldWrap);
  }

  private String newMove(Double left, Double right, boolean shouldWrap) {
    double dir = Math.max(left, right);
    int multiplier = (int) Math.round(dir * 4);
//    Log.v(TAG, String.format("Multiplier: %d - Min: %f - Max: %f - Delta: %f", multiplier, min, max, delta));
//    Log.v(TAG, String.format("M: %d - LEFT: %.10f - RIGHT: %.10f", multiplier, left, right));
//    Log.v(TAG, multiplier);
    if (dir < 0.2) {
      return "STAY";
    }
    if (left > right) {
      IntStream.range(0, multiplier).forEach(i -> moveLeft(shouldWrap));
      return "LEFT " + multiplier;
    } else if (left < right) {
      IntStream.range(0, multiplier).forEach(i -> moveRight(shouldWrap));
      return "RIGHT " + multiplier;
    }
    return null;
  }

  private void oldMove(Double left, Double right) {
    double max = Math.max(left, right);
    double min = Math.min(left, right);
    double delta = Math.abs(max - min) / max;
    int multiplier = (int) Math.round(delta * 4);
//    Log.v(TAG, String.format("Multiplier: %d - Min: %f - Max: %f - Delta: %f", multiplier, min, max, delta));
//    Log.v(TAG, String.format("LEFT: %.10f - RIGHT: %.10f", left, right));
    if (delta < 0.2) {
      return;
    }
    if (left > right) {
      IntStream.range(0, multiplier).forEach(i -> moveLeft(true));
    } else if (left < right) {
      IntStream.range(0, multiplier).forEach(i -> moveRight(true));
    }
  }

  public int getNumBadPull() {
    return numBadPull;
  }

  public int getNumGoodPull() {
    return numGoodPull;
  }

  public void pullFail() {
    numFailPull++;
  }

  public void pullSuccsess() {
    numGoodPull++;
  }

  public void pullBad() {
    numBadPull++;
  }
}
