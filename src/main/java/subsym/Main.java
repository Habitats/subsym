package subsym;


import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.swing.*;

import subsym.boids.Boids;
import subsym.genetics.GeneticEngine;
import subsym.genetics.GeneticPreferences;
import subsym.genetics.GeneticProblem;
import subsym.genetics.adultselection.AdultSelection;
import subsym.genetics.adultselection.Mixing;
import subsym.genetics.matingselection.MatingSelection;
import subsym.genetics.matingselection.SigmaScaled;
import subsym.genetics.matingselection.Tournament;
import subsym.gui.AICanvas;
import subsym.gui.AIGui;
import subsym.gui.AITextArea;
import subsym.lolz.Lolz;
import subsym.onemax.OneMax;
import subsym.surprisingsequence.SurprisingSequences;

/**
 * Created by anon on 28.01.2015.
 */
public class Main {

  public static final String TAG = Main.class.getSimpleName();
  private static Plot chart;

  public static void main(String[] args) {
    loadGui();
  }

  private static void loadGui() {
    new AIGui() {

      @Override
      protected int getDefaultCloseOperation() {
        return WindowConstants.EXIT_ON_CLOSE;
      }

      @Override
      protected Dimension getPreferredSize() {
        return new Dimension(300, 200);
      }

      @Override
      protected void init() {

//        AIPanel panel = new AIPanel();
//        panel.setPreferredSize(new Dimension(300, 200));
//        AIButton broid = new AIButton("BBoids);
//
//        broid.addActionListener(e -> broid());
//        panel.add(broid);
//
//        buildFrame(panel, null, null);

//        broid();
//        oneMax();
//        lolz();
        surprisingSequence();
      }


      @Override
      public JPanel getMainPanel() {
        return null;
      }

      @Override
      public AICanvas getDrawingCanvas() {
        return null;
      }

      @Override
      public AITextArea getInputField() {
        return null;
      }
    }.init();
  }

  private static void lolz() {
//    double crossOverRate = .5;
    double genotypeMutationRate = .000001;
    double populationMutationRate = 0.2;
    double crossOverRate = 1;
    GeneticPreferences prefs = new GeneticPreferences(20, crossOverRate, populationMutationRate, genotypeMutationRate,//
                                                      new Mixing(0.2), new SigmaScaled());
    Log.v(TAG, GeneticEngine.solve(new Lolz(prefs, 20), true));

//    averageOver(genotypeMutationRate, populationMutationRate, crossOverRate, 1000);
  }

  private static void oneMax() {
//    double crossOverRate = .5;
    double genotypeMutationRate = .0001;
    double populationMutationRate = 1;
    double crossOverRate = 0.5;
    GeneticRun run = new GeneticRun();
    Tournament mateSelectionMode = new Tournament(10, 0.05);
    GeneticPreferences prefs = new GeneticPreferences(40, crossOverRate, populationMutationRate, genotypeMutationRate,//
                                                      new Mixing(0.5), mateSelectionMode);
    IntStream.range(0, 1).forEach(i -> run.add(GeneticEngine.solve(new OneMax(prefs, 10000), true)));

    Log.v(TAG, run.getBest());
  }

  private static void surprisingSequence() {
//    profileSurprisingSequence();
    profileSingleSurprisingSequence();
  }

  private static void profileSingleSurprisingSequence() {
    double crossOverRate = .9;
    double genomeMutationRate = .010;
    double populationMutationRate = .9;
    AdultSelection adultSelectionMode = new Mixing(0.5);
    MatingSelection mateSelectionMode = new Tournament(10, 0.05);
    int alphabetSize = 40;
    int populationSize = 40;
    GeneticPreferences prefs = new GeneticPreferences(populationSize, crossOverRate, populationMutationRate, //
                                                      genomeMutationRate, adultSelectionMode, mateSelectionMode);
    for (int length = 90; length < alphabetSize * 3; length++) {
      SurprisingSequences problem = new SurprisingSequences(prefs, alphabetSize, length);
      GeneticProblem solution = GeneticEngine.solve(problem, true);
      Log.v(TAG, solution);
    }
  }

  private static void broid() {
    new Thread(new Boids()).start();
  }

  private static class GeneticRun {

    private List<GeneticProblem> runs = new ArrayList<>();

    public void add(GeneticProblem solution) {
      runs.add(solution);
    }

    public String getBest() {
      Map<String, Double> avgMap = runs.stream()//
          .map(p -> p.getId()).distinct() //
          .collect(Collectors.toMap(p -> p, p -> runs.stream() //
              .filter(p2 -> p.equals(p2.getId())) //
              .mapToDouble(p2 -> p2.generations()) //
              .average().getAsDouble()));
      Double bestAvg = avgMap.values().stream().min(Comparator.<Double>naturalOrder()).get();
      Predicate<String> isBest = p -> avgMap.get(p) == bestAvg;
      String best = "Best Average: " + bestAvg + " - " + avgMap.keySet().stream().filter(isBest).findFirst().get();

      return best;
    }
  }
}
