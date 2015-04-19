package subsym.ailife;

import java.util.Random;
import java.util.stream.IntStream;

import subsym.Log;
import subsym.ailife.entity.Empty;
import subsym.ailife.entity.Food;
import subsym.ailife.entity.Poison;
import subsym.ailife.entity.Robot;
import subsym.ann.AnnPreferences;
import subsym.ann.ArtificialNeuralNetwork;
import subsym.genetics.GeneticPreferences;
import subsym.genetics.GeneticProblem;
import subsym.models.Board;
import subsym.models.entity.TileEntity;

/**
 * Created by anon on 20.03.2015.
 */
public class AiLife extends GeneticProblem {

  private static final String TAG = AiLife.class.getSimpleName();

  private Robot robot;
  private AnnPreferences annPrefs;

  public AiLife() {
    super(null);
    Board<TileEntity> board = createAiLifeBoard(0101);

    robot = new Robot(0, 0, board);
    board.set(robot);
  }

  public AiLife(GeneticPreferences prefs, AnnPreferences annPrefs) {
    super(prefs);
    this.annPrefs = annPrefs;
  }

  @Override
  protected double getCrossoverCut() {
    return ArtificialNeuralNetwork.random().nextDouble();
  }

  @Override
  public void initPopulation() {
    IntStream.range(0, getPopulationSize()).forEach(i -> {
      AiLifeGenotype genotype = new AiLifeGenotype(annPrefs);
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
    return new AiLife(prefs, annPrefs);
  }

  @Override
  public GeneticProblem newInstance() {
    return new AiLife(getPreferences(), annPrefs);
  }

  @Override
  public void increment(int increment) {

  }

  @Override
  public void onSolved() {
    AiLifeGenotype best = (AiLifeGenotype) getPopulation().getBestGenotype();
    Log.v(TAG, best.fitness());
    AiLifePhenotype pheno = (AiLifePhenotype) best.getPhenotype();
    ArtificialNeuralNetwork ann = pheno.getArtificialNeuralNetwork();
    Log.v(TAG, pheno.fitness());
    Board<TileEntity> board = createAiLifeBoard(0101);
    AiLifeGui.show(board, ann);

    Log.v(TAG, this);
  }

  public static Board<TileEntity> createAiLifeBoard(int seed) {
    Log.v(TAG, "Board seed: " + seed);
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
}

