package subsym;

import java.awt.*;
import java.util.Arrays;
import java.util.stream.IntStream;

import javax.swing.*;

import subsym.boids.Boids;
import subsym.ga.GeneticEngine;
import subsym.ga.GeneticProblem.AdultSelection;
import subsym.ga.GeneticProblem.MateSelection;
import subsym.gui.AICanvas;
import subsym.gui.AIGui;
import subsym.gui.AITextArea;
import subsym.lolz.Lolz;
import subsym.onemax.OneMax;

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
        lolz();
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
    double genotypeMutationRate = .02;
    double genomeMutationRate = .0002;
    double crossOverRate = 1;
    Log.v(TAG, GeneticEngine.solve(new Lolz(20, 63, crossOverRate, genomeMutationRate, genotypeMutationRate,//
                                            AdultSelection.MIXING, MateSelection.TOURNAMENT)));

//    averageOver(genotypeMutationRate, genomeMutationRate, crossOverRate, 1000);
  }

  private static void oneMax() {
//    double crossOverRate = .5;
    double genotypeMutationRate = .02;
    double genomeMutationRate = .0002;
    double crossOverRate = 1;
    Log.v(TAG, GeneticEngine.solve(new OneMax(20, 40, crossOverRate, genomeMutationRate, genotypeMutationRate,//
                                              AdultSelection.MIXING, MateSelection.TOURNAMENT)));

//    averageOver(genotypeMutationRate, genomeMutationRate, crossOverRate, 1000);
  }

  private static void averageOver(double genotypeMutationRate, double genomeMutationRate, double crossOverRate,
                                  int runs) {
    Arrays.stream(MateSelection.values()).forEach(mateSelection -> {
      Arrays.stream(AdultSelection.values()).forEach(as -> {
        double average = IntStream.range(0, runs) //
            .map(i -> getGenerations(genotypeMutationRate, genomeMutationRate, crossOverRate, as, mateSelection)) //
            .average().getAsDouble();
        prettyPrint(genotypeMutationRate, genomeMutationRate, crossOverRate, mateSelection, as, average);
      });
    });
  }

  private static void prettyPrint(double genotypeMutationRate, double genomeMutationRate, double crossOverRate,
                                  MateSelection mateSelection, AdultSelection as, double average) {
    int l1 = Arrays.stream(AdultSelection.values()).mapToInt(v -> v.name().length()).max().getAsInt();
    int l2 = Arrays.stream(MateSelection.values()).mapToInt(v -> v.name().length()).max().getAsInt();
    Log.v(TAG, String.format("CR: %.2f - GMR: %.2f IMR: %.2f - G: %.2f - AS: %" + l1 + "s - MS: %" + l2 + "s", //
                             crossOverRate, genomeMutationRate, genotypeMutationRate, average, as, mateSelection));
  }

  private static int getGenerations(double genotypeMutationRate, double genomeMutationRate, double crossOverRate,
                                    AdultSelection as, MateSelection mateSelection) {
    return GeneticEngine
        .solve(new OneMax(20, 20, crossOverRate, genomeMutationRate, genotypeMutationRate, as, mateSelection))
        .generations();
  }

  private static void broid() {
    new Thread(new Boids()).start();
  }

}
