package subsym.beertracker;

import java.util.Random;
import java.util.stream.IntStream;

import subsym.ailife.entity.Empty;
import subsym.genetics.GeneticPreferences;
import subsym.genetics.GeneticProblem;
import subsym.models.Board;
import subsym.models.entity.TileEntity;

/**
 * Created by Patrick on 30.03.2015.
 */
public class BeerTracker extends GeneticProblem {

  private enum State {
    ABORTING, SIMULATING, IDLE;
  }

  private static final String TAG = BeerTracker.class.getSimpleName();
  private final BeerGui gui;
  private State state;

  public BeerTracker(GeneticPreferences prefs) {
    super(prefs);
    state = State.IDLE;
    gui = new BeerGui(this);
    reset();
  }

  public void reset() {
    Board<TileEntity> board = new Board<>(30, 15);
    IntStream.range(0, board.getWidth()).forEach(x -> IntStream.range(0, board.getHeight())//
        .forEach(y -> board.set(new Empty(x, y, board))));
    Tracker tracker = new Tracker(board);
    gui.setAdapter(board);
    gui.setTracker(tracker);
    simulateFallingPieces(board, tracker);
  }

  public void simulateFallingPieces(Board<TileEntity> board, Tracker tracker) {
    state = State.SIMULATING;
    Random r = new Random();
    int time = 0;
    int maxTime = 600;
    while (true) {
      Piece piece = new Piece(board, 1 + r.nextInt(6), tracker);
      int startPositionX = r.nextInt(board.getWidth() - (piece.getWidth() - 1));
      IntStream.range(0, startPositionX).forEach(y -> piece.moveRight(false));
      while (piece.moveDown(false)) {
        tracker.sense(piece);
        if (tracker.isPulling()) {
          piece.moveBottom();
        }
        try {
          Thread.sleep(gui.getSimulationSpeed());
        } catch (InterruptedException e) {
        }
        time++;
        gui.setTime(time, maxTime);
        if (time >= maxTime || state == State.ABORTING) {
          state = State.IDLE;
          return;
        }
      }
    }
  }

  @Override
  protected double getCrossoverCut() {
    return 0;
  }

  @Override
  public void initPopulation() {
    IntStream.range(0, getPopulationSize()).forEach(i -> {
      BeerGenotype genotype = new BeerGenotype(getPreferences().getAnnPreferences());
      getPopulation().add(genotype);
    });
  }

  @Override
  public boolean solution() {
    return getPopulation().getCurrentGeneration() == getPreferences().getMaxGenerations();
  }

  @Override
  public GeneticProblem newInstance(GeneticPreferences prefs) {
    return new BeerTracker(prefs);
  }

  @Override
  public GeneticProblem newInstance() {
    return new BeerTracker(getPreferences());
  }

  @Override
  public void increment(int increment) {
  }

  @Override
  public void onSolved() {
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
}
