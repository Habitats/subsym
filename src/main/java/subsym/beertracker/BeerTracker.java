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

  public BeerTracker(GeneticPreferences prefs) {
    super(prefs);

    Board<TileEntity> board = new Board<>(30, 15);
    IntStream.range(0, board.getWidth()).forEach(x -> IntStream.range(0, board.getHeight())//
        .forEach(y -> board.set(new Empty(x, y, board))));

    simulateFallingPieces(board);
  }

  private void simulateFallingPieces(Board<TileEntity> board) {
    Random r = new Random();
    Tracker tracker = new Tracker(board);
    BeerGui gui = new BeerGui(tracker);
    gui.setAdapter(board);
    IntStream.range(0, 100).forEach(i -> {
      Piece piece = new Piece(board, 1 + r.nextInt(6), tracker);
      int startPositionX = r.nextInt(board.getWidth() - (piece.getWidth() - 1));
      IntStream.range(0, startPositionX).forEach(y -> piece.moveRight(false));
      while (piece.moveDown(false)) {
        try {
          Thread.sleep(100);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    });
  }

  @Override
  protected double getCrossoverCut() {
    return 0;
  }

  @Override
  public void initPopulation() {
  }

  @Override
  public boolean solution() {
    return false;
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
}
