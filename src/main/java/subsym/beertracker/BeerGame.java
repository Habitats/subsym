package subsym.beertracker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import subsym.Log;
import subsym.ailife.entity.Empty;
import subsym.ann.AnnPreferences;
import subsym.ann.ArtificialNeuralNetwork;
import subsym.ann.nodes.OutputNode;
import subsym.models.Board;
import subsym.models.entity.TileEntity;

/**
 * Created by Patrick on 01.04.2015.
 */
public class BeerGame {

  private static final String TAG = BeerGame.class.getSimpleName();
  private final AnnPreferences annPreferences;
  private ArtificialNeuralNetwork ann;
  private int numGood;
  private int numBad;
  private int startPositionX;
  private int lastWidth;
  private int lastStartX;
  private long simulationSeed;
  private int maxTime;

  public BeerScenario getScenario() {
    return annPreferences.getBeerScenario();
  }

  public long getSimulationSeed() {
    return simulationSeed;
  }

  public Stream<OutputNode> getOutputNodeStream() {
    return ann.getOutputNodeStream();
  }

  public int getNumWeights() {
    return ann.getNumWeights();
  }

  public void statePrint() {
    ann.statePrint();
  }


  private enum State {
    ABORTING, SIMULATING, IDLE;

  }

  private int simulationSpeed = 0;

  private BeerGui listener;

  private Board<TileEntity> board;

  private Tracker tracker;
  private int time = 0;
  private boolean shouldWrap = true;
  private State state;
  private BeerGui gui;

  public BeerGame(AnnPreferences prefs) {
    annPreferences = prefs;
    state = State.IDLE;
    switch (prefs.getBeerScenario()) {
      case WRAP:
        ann = ArtificialNeuralNetwork.buildWrappingCtrnn(prefs);
        break;
      case NO_WRAP:
        ann = ArtificialNeuralNetwork.buildNoWrapCtrnn(prefs);
        break;
      case PULL:
        ann = ArtificialNeuralNetwork.buildPullingCtrnn(prefs);
        break;
      default:
        throw new IllegalStateException("No scenario!");
    }
    reset();
  }

  public void reset() {
    numGood = 0;
    numBad = 0;
    board = new Board<>(30, 15);
    IntStream.range(0, board.getWidth()).forEach(x -> IntStream.range(0, board.getHeight())//
        .forEach(y -> board.set(new Empty(x, y, board))));
    tracker = new Tracker(board, this);
  }

  public void demo(long simulationSeed) {
    setSimulationSeed(simulationSeed);
    initGui();
    simulateFallingPieces(board, tracker, null, simulationSeed, false, 600);
  }

  public void generateNew() {
    reset();
    initGui();
    long seed = System.currentTimeMillis();
    setSimulationSeed(seed);
    simulateFallingPieces(board, tracker, ann, seed, false, 600);
  }

  public void play() {
    reset();
    initGui();
    simulateFallingPieces(board, tracker, null, System.currentTimeMillis(), false, 600);
  }

  public void setValues(List<Integer> values) {
    int numNodes = (int) ann.getOutputNodeStream().count();
    int numWeights = ann.getNumWeights();
    List<Double> normalizedValues = getNormalizedValues(values);

    List<Double> weights = normalizedValues.subList(0, numWeights);
    List<Double> timeConstants = getTimeConstants(normalizedValues.subList(numWeights, numWeights + numNodes));
    List<Double> gains = getGains(normalizedValues.subList(numWeights + numNodes, numWeights + numNodes * 2));
    ann.setWeights(weights);
    ann.setTimeConstants(timeConstants);
    ann.setGains(gains);
  }

  private static List<Double> getTimeConstants(List<Double> values) {
    return values.stream().map(g -> g + 1).collect(Collectors.toList());
  }

  private static List<Double> getGains(List<Double> values) {
    return values.stream().map(g -> (g * 4) + 1).collect(Collectors.toList());
  }

  private static List<Double> getNormalizedValues(List<Integer> values) {
    return values.stream().mapToDouble(BeerPhenotype::normalize).boxed().collect(Collectors.toList());
  }

  public void manual(String text) {
    reset();
    initGui();
    gui.setInput(text);
    switch (getScenario()) {
      case WRAP:
        ann = ArtificialNeuralNetwork.buildWrappingCtrnn(annPreferences);
        break;
      case NO_WRAP:
        ann = ArtificialNeuralNetwork.buildNoWrapCtrnn(annPreferences);
        break;
      case PULL:
        ann = ArtificialNeuralNetwork.buildPullingCtrnn(annPreferences);
        break;
    }
    List<Integer> values = Arrays.asList(text.trim().split("\\s+")).stream()//
        .mapToInt(Integer::parseInt).boxed().collect(Collectors.toList());
    setValues(values);
    ann.statePrint();
    simulateFallingPieces(board, tracker, ann, getSimulationSeed(), false, 600);
  }

  public void initGui() {
    if (gui == null) {
      gui = new BeerGui(this);
    }
    gui.setAdapter(board);
  }

  public double simulate(long seed, boolean shouldLog, int maxTime) {
    simulateFallingPieces(board, tracker, ann, seed, false, maxTime);
    return getScore();
  }

  public void simulateFallingPieces(Board<TileEntity> board, Tracker tracker, ArtificialNeuralNetwork ann, long seed, boolean shouldLog,
                                    int maxTime) {
    if (seed == 1000) {
      seed = 1;
      loadPreset(annPreferences.getBeerScenario());
      ann = this.ann;
      ann.statePrint();
    }

    this.maxTime = maxTime;
    state = State.SIMULATING;
    lastWidth = 0;
    lastStartX = 0;
    Random r = new Random(seed);
    time = 0;

    while (true) {
      Piece piece = spawnPiece(board, tracker, r);
      boolean spawnNext = false;
      while (!spawnNext) {
        if (time >= maxTime || state == State.ABORTING) {
          state = State.IDLE;
          return;
        }
        time++;
        onTick();
        if (tracker.isPulling()) {
          piece.moveBottom();
          spawnNext = true;
        } else if (!piece.moveDown(false)) {
          spawnNext = true;
        }
        try {
          Thread.sleep(simulationSpeed);
        } catch (InterruptedException e) {
        }
        tracker.sense(piece);
        moveTracker(tracker, ann);
//          if (shouldLog) {
//            ann.statePrint();
//          }
//          Log.v(TAG, sensors.stream().map(s -> s.toString()).collect(Collectors.joining("\t", "", "")));
      }
    }
  }

  private void loadPreset(BeerScenario beerScenario) {
    switch (beerScenario) {
      case WRAP:
        setValues(
            "062 030 203 111 027 120 226 249 194 039 156 070 048 043 116 206 021 192 073 014 017 138 157 002 159 074 123 224 206 019 049 087 063 157 009 025 208 205 249 078 070 212 114 028 149 050 195 161 119 166 249 156 056 199 099 136 250 191 026 132");
        break;
      case NO_WRAP:
        setValues(
            "005 148 021 045 064 203 243 197 043 068 193 190 217 229 060 186 153 015 037 003 039 145 168 240 003 224 001 076 062 174 049 153 064 200");
        break;
      case PULL:
        setValues(
            "045 143 161 071 165 242 047 052 002 064 030 240 248 225 251 205 129 237 106 028 010 212 241 106 009 225 059 083 255 241 096 150 131 070 082 153");
        break;
    }
  }

  private void setValues(String s) {
    setValues(Arrays.asList(s.split("\\s+")).stream().map(Integer::parseInt).collect(Collectors.toList()));
  }

  private void moveTracker(Tracker tracker, ArtificialNeuralNetwork ann) {
    if (ann == null) {
      return;
    }
    List<Double> sensors = tracker.getSensors().stream().mapToDouble(b -> b ? 1. : 0.).boxed().collect(Collectors.toList());
    List<Double> outputs = ann.getOutputs();
    switch (getScenario()) {
      case WRAP:
        ann.updateInput(sensors.subList(0, 5));
        tracker.move(outputs.subList(0, 2), true);
        break;
      case NO_WRAP:
        ann.updateInput(sensors);
        tracker.move(outputs.subList(0, 2), false);
        break;
      case PULL:
        ann.updateInput(sensors.subList(0, 5));
        if (outputs.get(2) > .5) {
          tracker.pull();
        }
        tracker.move(outputs.subList(0, 2), true);
        break;
      default:
        throw new IllegalStateException("Invalid scenario");
    }
  }

  private Piece spawnPiece(Board<TileEntity> board, Tracker tracker, Random r) {
    int width = 1 + r.nextInt(6);
    Piece piece = new Piece(board, width, tracker);
    if (width < 5) {
      numGood++;
    } else {
      numBad++;
    }
    List<Integer> possibleSpawns = new ArrayList<>();
    if (piece.getWidth() < lastStartX) {
      possibleSpawns.addAll(IntStream.range(0, lastStartX - piece.getWidth()).boxed().collect(Collectors.toSet()));
    }
    if (lastStartX + lastWidth + piece.getWidth() < board.getWidth()) {
      possibleSpawns
          .addAll(IntStream.range(lastStartX + lastWidth + 1, board.getWidth() - piece.getWidth() + 1).boxed().collect(Collectors.toSet()));
    }
    startPositionX = possibleSpawns.get(r.nextInt(possibleSpawns.size()));
    lastStartX = startPositionX;
    lastWidth = piece.getWidth();
    IntStream.range(0, startPositionX).forEach(y -> piece.moveRight(false));
    return piece;
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

  public void setSimulationSeed(long simulationSeed) {
    this.simulationSeed = simulationSeed;
    Log.v(TAG, "New seed: " + simulationSeed);
  }

  public boolean isVisible() {
    return gui != null && gui.isVisible();
  }

  public void setListener(BeerGui beerGui) {
    this.listener = beerGui;
  }

  public int getTime() {
    return maxTime - time;
  }

  public double getScore() {
    return tracker.calculateScore(numBad, numGood);
  }

  public int getCrashed() {
    return tracker.getCrash();
  }

  public int getAvoided() {
    return tracker.getAvoided();
  }

  public int getCaught() {
    return tracker.getCaught();
  }

  public int getNumBad() {
    return numBad;
  }

  public int getNumGood() {
    return numGood;
  }

  public int getNumBadPull() {
    return tracker.getNumBadPull();
  }

  public int getNumGoodPull() {
    return tracker.getNumGoodPull();
  }

  public void setSimulationSpeed(int simulationSpeed) {
    this.simulationSpeed = simulationSpeed;
  }
}
