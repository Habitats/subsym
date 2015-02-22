package subsym;

import java.awt.*;
import java.util.Arrays;
import java.util.stream.DoubleStream;

import javax.swing.*;

import subsym.boids.Boids;
import subsym.ga.GeneticEngine;
import subsym.ga.GeneticProblem;
import subsym.gui.AICanvas;
import subsym.gui.AIGui;
import subsym.gui.AITextArea;
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
        oneMax();
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

  private static void oneMax() {
//    double crossOverRate = .5;
    double genotypeMutationRate = .02;
    double genomeMutationRate = .02;
    DoubleStream.of(.1, .2, .3, .4, .5, .6, .7, .8, .9, 1).forEach(crossOverRate -> {
      Arrays.stream(GeneticProblem.AdultSelection.values()).forEach(as -> {
        OneMax oneMax = new OneMax(20, 20, crossOverRate, genomeMutationRate, genotypeMutationRate, as,
                                   GeneticProblem.MateSelection.FITNESS_PROPORTIONATE);
        Log.v(TAG, String.format("CR: %.2f - G: %.2f - AS: %s", crossOverRate, GeneticEngine.solve(oneMax, 100), as));
      });
    });
  }

  private static void broid() {
    new Thread(new Boids()).start();
  }

}
