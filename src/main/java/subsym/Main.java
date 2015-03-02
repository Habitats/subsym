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
import subsym.genetics.adultselection.FullTurnover;
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
    boolean ensureUnique = false;
    GeneticPreferences prefs = new GeneticPreferences(20, crossOverRate, populationMutationRate, genotypeMutationRate,//
                                                      new Mixing(0.2), new SigmaScaled(), true);
    Log.v(TAG, GeneticEngine.solve(new Lolz(prefs, 20), true));

//    averageOver(genotypeMutationRate, populationMutationRate, crossOverRate, 1000);
  }

  private static void oneMax() {
//    double crossOverRate = .5;
    double genotypeMutationRate = .02;
    double populationMutationRate = .9;
    double crossOverRate = 1;
    boolean ensureUnique = false;
    GeneticRun run = new GeneticRun();
    GeneticPreferences prefs = new GeneticPreferences(20, crossOverRate, populationMutationRate, genotypeMutationRate,//
                                                      new Mixing(0.5), new Tournament(3, 0.05), ensureUnique);
    IntStream.range(0, 100).forEach(i -> run.add(GeneticEngine.solve(new OneMax(prefs, 5), true)));

    Log.v(TAG, run.getBest());
//    averageOver(genotypeMutationRate, populationMutationRate, crossOverRate, 1000);
  }

  private static void surprisingSequence() {
//    profileSurprisingSequence();
    profileSingleSurprisingSequence();

//    averageOver(genotypeMutationRate, populationMutationRate, crossOverRate, 1000);
  }

  private static void profileSingleSurprisingSequence() {
    double crossOverRate = 1;
    double genomeMutationRate = .0400;
    double populationMutationRate = 1;
    AdultSelection adultSelectionMode = new Mixing(0.5);
    MatingSelection mateSelectionMode = new Tournament(10, 0.05);
    int alphabetSize = 10;
    int populationSize = 40;
    boolean ensureUnique = false;
    GeneticPreferences prefs = new GeneticPreferences(populationSize, crossOverRate, populationMutationRate, //
                                                      genomeMutationRate, adultSelectionMode, mateSelectionMode,
                                                      ensureUnique);
    for (int length = 2; length < alphabetSize * 3; length++) {
      SurprisingSequences problem = new SurprisingSequences(prefs, alphabetSize, length);
      GeneticProblem solution = GeneticEngine.solve(problem, true);
      Log.v(TAG, solution);
    }
  }

  private static void profileSurprisingSequence() {
    //    double crossOverRate = .5;
    double genomeMutationRate = .02;
//    double populationMutationRate = .01;
    AdultSelection adultSelectionMode = new FullTurnover();
    MatingSelection mateSelectionMode = new SigmaScaled();
    double crossOverRate = 1;
    int alphabetSize = 10;
    int populationSize = 20;
    int length = 4;
    boolean ensureUnique = true;
    GeneticRun runs = new GeneticRun();
//    for (AdultSelection adultSelectionMode : AdultSelection.values()) {
//      for (MateSelection mateSelectionMode : MateSelection.values()) {
    for (Double populationMutationRate : IntStream.range(2, 7)//
        .mapToDouble(i -> (i + 0.1) / 10.).boxed().collect(Collectors.toList())) {
      for (Double genotypeMutationRate : IntStream.range(2, 7) //
          .mapToDouble(i -> (i + 0.1) / 10.).boxed().collect(Collectors.toList())) {

        for (Integer len : IntStream.range(13, 17).boxed().collect(Collectors.toList())) {
          for (int i = 0; i < 10; i++) {
            GeneticPreferences prefs = new GeneticPreferences(populationSize, crossOverRate, populationMutationRate, //
                                                              genomeMutationRate, adultSelectionMode, mateSelectionMode,
                                                              ensureUnique);
            SurprisingSequences problem = new SurprisingSequences(prefs, alphabetSize, length);
            GeneticProblem solution = GeneticEngine.solve(problem, false);
            Log.v(TAG, solution);
            runs.add(solution);
          }
        }
//      }
//    }
      }
    }

    Log.v(TAG, "Best: " + runs.getBest());
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
