package subsym.beertracker;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import subsym.ailife.entity.Empty;
import subsym.ann.ArtificialNeuralNetwork;
import subsym.models.Board;
import subsym.models.entity.TileEntity;

/**
 * Created by Patrick on 01.04.2015.
 */
public class BeerGame {

  private static final String TAG = BeerGame.class.getSimpleName();
  private ArtificialNeuralNetwork ann;
  private int numGood;
  private int numBad;


  private enum State {
    ABORTING, SIMULATING, IDLE;
  }

  private int simulationSpeed = 0;
  private BeerGui listener;
  private Board<TileEntity> board;
  private Tracker tracker;
  private final int MAX_TIME = 600;
  private int time = 0;
  private boolean shouldWrap = true;
  private State state;
  private BeerGui gui;

  public BeerGame() {
    state = State.IDLE;
    reset();
  }

  public static void demo() {
    BeerGame game = new BeerGame();
    game.initGui();
    game.simulateFallingPieces(game.board, game.tracker, null, System.currentTimeMillis());
  }

  public void reset() {
    numGood = 0;
    numBad = 0;
    board = new Board<>(30, 15);
    IntStream.range(0, board.getWidth()).forEach(x -> IntStream.range(0, board.getHeight())//
        .forEach(y -> board.set(new Empty(x, y, board))));
    tracker = new Tracker(board);
  }

  public void restart() {
    reset();
    initGui();
    simulateFallingPieces(board, tracker, ann, System.currentTimeMillis());
  }

  public void play() {
    reset();
    initGui();
    simulateFallingPieces(board, tracker, null, System.currentTimeMillis());
  }

  public void initGui() {
    if (gui == null) {
      gui = new BeerGui(this);
    }
    gui.setAdapter(board);
  }

  public double simulate(ArtificialNeuralNetwork ann, int seed) {
    this.ann = ann;
    simulateFallingPieces(board, tracker, ann, seed);
    return getScore();
  }

  public void simulateFallingPieces(Board<TileEntity> board, Tracker tracker, ArtificialNeuralNetwork ann, long seed) {
    state = State.SIMULATING;
    Random r = new Random(seed);
    time = 0;

    while (true) {
      int width = 1 + r.nextInt(6);
      Piece piece = new Piece(board, width, tracker);
      if (width < 5) {
        numGood++;
      } else {
        numBad++;
      }
      int startPositionX = r.nextInt(board.getWidth() - (piece.getWidth() - 1));
      IntStream.range(0, startPositionX).forEach(y -> piece.moveRight(false));
      while (piece.moveDown(false)) {
        time++;
        onTick();
        tracker.sense(piece);
        if (tracker.isPulling()) {
          piece.moveBottom();
        }
        if (ann != null) {
          List<Boolean> sensors = tracker.getSensors();
//          Log.v(TAG, sensors.stream().map(s -> s.toString()).collect(Collectors.joining("\t", "", "")));
          ann.updateInput(sensors.stream().mapToDouble(b -> b ? 1. : 0.).boxed().collect(Collectors.toList()));
          List<Double> outputs = ann.getOutputs();
          tracker.move(outputs);
        }
        try {
          Thread.sleep(simulationSpeed);
        } catch (InterruptedException e) {
        }
        if (time >= MAX_TIME || state == State.ABORTING) {
          state = State.IDLE;
          return;
        }
      }
      if (ann != null) {
        ann.resetInternalState();
      }
    }
  }

  private void onTick() {
    if (listener != null) {
      listener.onTick();
    }
  }

  public void pull() {
    tracker.pull();
  }

  public void moveLeft() {
    tracker.moveLeft(shouldWrap);
  }

  public void moveRight() {
    tracker.moveRight(shouldWrap);
  }

  public void stop(Runnable callback) {
    state = state == State.SIMULATING ? State.ABORTING : State.IDLE;
    while (state == State.ABORTING) {
      try {
        Thread.sleep(2);
      } catch (InterruptedException e) {
      }
    }
    new Thread(() -> callback.run()).start();
  }

  public void setListener(BeerGui beerGui) {
    this.listener = beerGui;
  }

  public int getTime() {
    return MAX_TIME - time;
  }

  public double getScore() {
    return tracker.calculateScore(numBad, numGood);
  }

  public int getCrashed() {
    return tracker.getCrashed();
  }

  public int getAvoided() {
    return tracker.getAvoided();
  }

  public int getCaught() {
    return tracker.getCaught();
  }

  public void setSimulationSpeed(int simulationSpeed) {
    this.simulationSpeed = simulationSpeed;
  }
}
