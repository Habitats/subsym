package subsym.flatland;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

import subsym.Log;
import subsym.Main;
import subsym.ann.AnnPreferences;
import subsym.ann.ArtificialNeuralNetwork;
import subsym.flatland.entity.Empty;
import subsym.flatland.entity.Food;
import subsym.flatland.entity.Poison;
import subsym.flatland.entity.Robot;
import subsym.genetics.GeneticPreferences;
import subsym.genetics.GeneticProblem;
import subsym.gui.Direction;
import subsym.models.Board;
import subsym.models.entity.TileEntity;

/**
 * Created by anon on 20.03.2015.
 */
public class FlatlandAnnSimulator extends GeneticProblem implements FlatlandSimulator {

  private static final String TAG = FlatlandAnnSimulator.class.getSimpleName();

  private Robot robot;
  private AnnPreferences annPrefs;
  private ArtificialNeuralNetwork ann;

//  public FlatlandAnnSimulator() {
//    super(null);
//    Board<TileEntity> board = createAiLifeBoard(0101);
//
//    robot = new Robot(0, 0, board, true, this);
//    board.set(robot);
//  }

  public FlatlandAnnSimulator(GeneticPreferences prefs, AnnPreferences annPrefs) {
    super(prefs);
    this.annPrefs = annPrefs;
  }

  @Override
  protected double getCrossoverCut() {
    return Main.random().nextDouble();
  }

  @Override
  public void demo(GeneticPreferences prefs) {
    onSolved();
  }

  @Override
  public void initPopulation() {
    IntStream.range(0, getPopulationSize()).forEach(i -> {
      FlatlandGenotype genotype = new FlatlandGenotype(annPrefs);
      genotype.randomize();
      getPopulation().add(genotype);
    });
  }

  @Override
  public boolean solution() {
//    return getPopulation().getBestGenotype().fitness() == 1;
    return getPopulation().getCurrentGeneration() == getPreferences().getMaxGenerations();
  }

  @Override
  public GeneticProblem newInstance(GeneticPreferences prefs) {
    return new FlatlandAnnSimulator(prefs, annPrefs);
  }

  @Override
  public GeneticProblem newInstance() {
    return new FlatlandAnnSimulator(getPreferences(), annPrefs);
  }

  @Override
  public void increment(int increment) {

  }

  @Override
  public void onSolved() {
    FlatlandGenotype best = (FlatlandGenotype) getPopulation().getBestGenotype();
    Log.v(TAG, best.fitness());
    FlatlandPhenotype pheno = (FlatlandPhenotype) best.getPhenotype();
    ann = pheno.getArtificialNeuralNetwork();
    Log.v(TAG,
          String.format("Genotype size: %d - Phenotype size: %d - Fitness: %.3f", best.size(), pheno.getNumWeights(), pheno.fitness()));
    List<Board<TileEntity>> boards = new ArrayList<>();
    IntStream.range(0, annPrefs.isSingle() ? 1 : 5).forEach(i -> boards.add(createAiLifeBoard(FlatlandPhenotype.goodSeeds.get(i))));
    simulate(boards, this, () -> Log.v(TAG, this));
  }

  public static void simulate(List<Board<TileEntity>> boards, FlatlandSimulator ann, Runnable callback) {
    Flatland flatland = new Flatland(ann, true);
    simulate(boards, callback, flatland);
  }

  private static void simulate(List<Board<TileEntity>> boards, Runnable callback, Flatland flatland) {
    if (boards.isEmpty()) {
      callback.run();
      return;
    }
    flatland.setBoard(boards.remove(0));
    flatland.simulate(true, () -> simulate(boards, callback, flatland));
  }

  public static Board<TileEntity> createAiLifeBoard(long seed) {
    Board<TileEntity> board = new Board<>(10, 10);
    Random random = new Random(seed);
    IntStream.range(0, 10).forEach(x -> IntStream.range(0, 10).forEach(y -> board.set(getRandomTile(board, x, y, random.nextDouble()))));
    return board;
  }

  private static TileEntity getRandomTile(Board<TileEntity> board, int x, int y, double random) {
    if (random < 1 / 3.) {
      return new Empty(x, y, board);
    } else if (random < 2 / 3.) {
      return new Food(x, y, board);
    } else {
      return new Poison(x, y, board);
    }
  }

  @Override
  public void move(Robot robot) {
    if (ann == null) {
      Log.v(TAG, "No Artificial Neural Network present! Simulation exiting ...");
      return;
    }
    int indexOfBest = ann.getBestIndex(robot.getSensoryInput());
    if (indexOfBest == 0) {
      robot.move(Direction.LEFT);
    } else if (indexOfBest == 1) {
      robot.move(Direction.UP);
    } else if (indexOfBest == 2) {
      robot.move(Direction.RIGHT);
    }
  }

  @Override
  public int getMaxSteps() {
    return 60;
  }

  @Override
  public void onNormalMove() {

  }

  @Override
  public void onFoodConsumed() {

  }

  @Override
  public void onPoisonConsumed() {

  }
}

