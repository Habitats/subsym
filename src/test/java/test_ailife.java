import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import subsym.Log;
import subsym.flatland.FlatlandGenotype;
import subsym.flatland.FlatlandPhenotype;
import subsym.flatland.entity.Empty;
import subsym.flatland.entity.Food;
import subsym.flatland.entity.Poison;
import subsym.flatland.entity.Robot;
import subsym.ann.AnnPreferences;
import subsym.ann.ArtificialNeuralNetwork;
import subsym.ann.WeightBound;
import subsym.ann.activation.Linear;
import subsym.ann.activation.Sigmoid;
import subsym.ann.nodes.AnnNodes;
import subsym.beertracker.BeerGenotype;
import subsym.beertracker.BeerPhenotype;
import subsym.beertracker.BeerScenario;
import subsym.genetics.GeneticPreferences;
import subsym.genetics.Genotype;
import subsym.gui.AIGridCanvas;
import subsym.models.Board;
import subsym.models.entity.TileEntity;

import static org.junit.Assert.assertEquals;

/**
 * Created by anon on 24.03.2015.
 */
public class test_ailife {

  private static final String TAG = test_ailife.class.getSimpleName();
  private Random random = new Random(0);

  @Test
  public void test_board() {
    Board<TileEntity> board = new Board<>(5, 5);
    IntStream.range(0, 5).forEach(x -> IntStream.range(0, 5).forEach(y -> board.set(new Empty(x, y, board))));
    board.set(new Poison(1, 1, board));
    board.set(new Poison(2, 2, board));
    board.set(new Poison(2, 3, board));
    board.set(new Poison(1, 3, board));
    board.set(new Food(0, 2, board));
    board.set(new Food(1, 2, board));
//    Robot robot = new Robot(0, 0, board, true, this);
//    board.set(robot);

//    assertEquals(robot.getFoodSensorInput(), Arrays.asList(0, 0, 0));
//    robot.move(1);
//    assertEquals(robot.getFoodSensorInput(), Arrays.asList(0, 1, 0));
//    assertEquals(robot.getPoisonSensorInput(), Arrays.asList(0, 0, 1));
//    assertEquals(robot.getPosition(), Vec.create(0, 1));
//    robot.move(1);
//    assertEquals(robot.getFoodSensorInput(), Arrays.asList(0, 0, 1));
//    assertEquals(robot.getPoisonSensorInput(), Arrays.asList(0, 0, 0));
//    assertEquals(robot.getPosition(), Vec.create(0, 2));
//    robot.move(2);
//    assertEquals(robot.getFoodSensorInput(), Arrays.asList(0, 0, 0));
//    assertEquals(robot.getPoisonSensorInput(), Arrays.asList(1, 1, 1));
//    assertEquals(robot.getPosition(), Vec.create(1, 2));
//    robot.move(1);
//    assertEquals(robot.getPosition(), Vec.create(2, 2));
//    robot.move(0);
//    assertEquals(robot.getPosition(), Vec.create(2, 3));
//    robot.move(0);
//    assertEquals(robot.getPosition(), Vec.create(1, 3));
//
//    displayGui(board, robot);
  }

  //  @Test
  public void test_ann() {
    AnnNodes inputs = AnnNodes.createInput(new WeightBound(-5., 5.), 0.1, 0.2, 0.3);
    assertEquals(inputs.getValues(), Arrays.asList(0.1, 0.2, 0.3));
    AnnNodes outputs = AnnNodes.createOutput(new WeightBound(-5, 5), 2);
    ArtificialNeuralNetwork
        ann =
        new ArtificialNeuralNetwork(new AnnPreferences(1, 2, new Sigmoid(), BeerScenario.WRAP, true), inputs, outputs);
    ann.updateInput(0.8, 0.9, 0.2);
    assertEquals(inputs.getValues(), Arrays.asList(0.8, 0.9, 0.2));
    assertEquals(ann.getInputs(), Arrays.asList(0.8, 0.9, 0.2));
    ann.updateInput(0.0, 0.0, 0.0);
    Linear activationFunction = new Linear();
    ann.getLayers().stream().flatMap(AnnNodes::stream).forEach(n -> n.setActivationFunction(activationFunction));
    assertEquals(ann.getInputs(), Arrays.asList(0.0, 0.0, 0.0));
    assertEquals(ann.getOutputs(), Arrays.asList(0.0, 0.0));

    List<Double> testInputs = Arrays.asList(0.1, 0.2, 0.3);
    ann.updateInput(testInputs);
    List<Double> weights = IntStream.range(0, ann.getNumWeights())//
        .mapToDouble(i -> (double) i / ann.getNumWeights()).boxed().collect(Collectors.toList());
//        .mapToDouble(i -> random.nextDouble()).boxed().collect(Collectors.toList());
    ann.setWeights(weights);
    double h1 = weights.get(0) * testInputs.get(2) + weights.get(1) * testInputs.get(0) + weights.get(2) * testInputs.get(1);
    double h2 = weights.get(3) * testInputs.get(2) + weights.get(4) * testInputs.get(0) + weights.get(5) * testInputs.get(1);
    double o1 = weights.get(6) * activationFunction.evaluate(h1) + weights.get(7) * activationFunction.evaluate(h2);
    double o2 = weights.get(8) * activationFunction.evaluate(h1) + weights.get(9) * activationFunction.evaluate(h2);

    double out1 = activationFunction.evaluate(o1);
    double out2 = activationFunction.evaluate(o2);
    assertEquals(Arrays.asList(out1, out2), ann.getOutputs());
  }

  @Test
  public void test_annMutate() {
//    Genotype genotype = new FlatlandGenotype(AnnPreferences.getAiLifeDefault());
    Genotype genotype = new BeerGenotype(AnnPreferences.getBeerDefault());
    IntStream.range(0, 100).forEach(i -> {
      Log.v(TAG, genotype.getBitsString().length() + " " + genotype.getBitsString());
      Log.v(TAG, genotype.getPhenotype());
      genotype.mutate(0.011);
    });
  }

  @Test
  public void test_ctrnn() {
    AnnNodes inputs = AnnNodes.createInput(new WeightBound(0, 1), 1., 1.);
    AnnNodes outputs = AnnNodes.createOutput(new WeightBound(0, 1), 1);
    ArtificialNeuralNetwork
        ann =
        new ArtificialNeuralNetwork(new AnnPreferences(1, 2, new Sigmoid(), BeerScenario.WRAP, true), inputs, outputs);
    ann.setStateful();

    AnnNodes nodes = ann.getLayers().get(1);
    ann.addBiasNode(new WeightBound(1, 2), nodes);
    ann.addSelfNode(new WeightBound(3, 4), nodes);
    nodes.stream().forEach(n -> {
      nodes.stream().filter(other -> !n.equals(other)).forEach(n::crossConnect);
    });

    Random r = new Random(0);
    List<Double> weights = getRandom(ann.getNumWeights(), r);
    assertEquals(weights.size(), 12);
    List<Double> timeConstants = getRandom(ann.getNumNodes(), r);
    List<Double> gains = getRandom(ann.getNumNodes(), r);
    ann.setWeights(weights);
//    weights.forEach(System.out::print);
    ann.setTimeConstants(timeConstants);
    ann.setGains(gains);

    List<Double> vals = outputs.getValues();
    ann.statePrint();
    IntStream.range(0, 10000).forEach(i -> {
      ann.updateInput(1., 0.);
      ann.statePrint();
    });
    vals = outputs.getValues();
    ann.updateInput(0., 1.);
    ann.statePrint();
    vals = outputs.getValues();
    ann.updateInput(0., 0.);
    ann.statePrint();
    vals = outputs.getValues();
    ann.updateInput(1., 1.);
    ann.statePrint();
    vals = outputs.getValues();
  }

  @Test
  public void test_copy() {
    AnnPreferences preferences = AnnPreferences.getAiLifeDefault();
    preferences.setSingle(true);
    preferences.setDynamic(false);
    FlatlandGenotype genotype = new FlatlandGenotype(preferences);
    genotype.randomize();
    Genotype copy = genotype.copy();
    FlatlandPhenotype p1 = (FlatlandPhenotype) genotype.getPhenotype();
    FlatlandPhenotype p2 = (FlatlandPhenotype) copy.getPhenotype();
    double f1 = copy.fitness();
    double f2 = genotype.fitness();
    assertEquals(f1, f2, 0);
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

  @Test
  public void test_beerOutput() {
    String
        text =
        "034 221 126 248 082 073 062 124 073 187 061 171 016 095 138 102 030 121 021 097 014 236 085 146 082 205 107 231 160 184 048 235 026 015 152 103 213 222 177 182 071 218 200 053 144 027 162 139 120 180 213 216 016 244 053 252 074 178 064 005";
    List<Integer> values = Arrays.asList(text.trim().split("\\s+")).stream()//
        .mapToInt(Integer::parseInt).boxed().collect(Collectors.toList());
    AnnPreferences annPreferences = GeneticPreferences.getBeer().getAnnPreferences();
    ArtificialNeuralNetwork ann = ArtificialNeuralNetwork.buildWrappingCtrnn(annPreferences);
    int numNodes = (int) ann.getOutputNodeStream().count();
    int numWeights = ann.getNumWeights();
    List<Double> normalizedValues = getNormalizedValues(values);

    List<Double> weights = normalizedValues.subList(0, numWeights);
    List<Double> timeConstants = getTimeConstants(normalizedValues.subList(numWeights, numWeights + numNodes));
    List<Double> gains = getGains(normalizedValues.subList(numWeights + numNodes, numWeights + numNodes * 2));
    ann.setWeights(weights);
    ann.setTimeConstants(timeConstants);
    ann.setGains(gains);
    ann.resetInternalState();
    ann.updateInput(1., 1., 1., 1., 1.);
    ann.statePrint();


    for (double a = 0; a < 2; a++) {
      for (double b = 0; b < 2; b++) {
        for (double c = 0; c < 2; c++) {
          for (double d = 0; d < 2; d++) {
            for (double e = 0; e < 2; e++) {
//              Log.v(TAG, "New input!");
//              ann.statePrint();
              String inputs = "" + (int) a + (int) b + (int) c + (int) d + (int) e;
              String[] split = inputs.replaceAll("1+", "1").replaceAll("0+", " ").trim().split(" ");
              if (split.length > 1) {
                continue;
              }
//              ann.resetInternalState();
              ann.updateInput(a, b, c, d, e);
              ann.statePrint();
              String outputs = ann.getOutputs().stream().map(o -> String.format("%.3f", o)).collect(Collectors.joining(" "));
              System.out.println(String.format("%s > %s > %s", inputs, outputs, move(ann.getOutputs())));

            }
          }
        }
      }
    }
  }

  private String move(List<Double> outputs) {
    Double left = outputs.get(0);
    Double right = outputs.get(1);
    double dir = Math.max(left, right);
    int multiplier = (int) Math.round(dir * 4);
//    Log.v(TAG, String.format("Multiplier: %d - Min: %f - Max: %f - Delta: %f", multiplier, min, max, delta));
//    Log.v(TAG, String.format("M: %d - LEFT: %.10f - RIGHT: %.10f", multiplier, left, right));
//    Log.v(TAG, multiplier);
    if (dir < 0.2) {
      return "STAY";
    }
    if (left > right) {
      return "LEFT " + multiplier;
    } else if (left < right) {
      return "RIGHT " + multiplier;
    }
    return null;
  }

  private List<Double> getRandom(int num, Random r) {
    return IntStream.range(0, num).mapToDouble(i -> r.nextDouble()).boxed().collect(Collectors.toList());
  }

  private void displayGui(Board<TileEntity> board, final Robot robot) {
    AIGridCanvas<TileEntity> canvas = new AIGridCanvas<>();
    canvas.setAdapter(board);
  }

}
