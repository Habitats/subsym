package subsym;


import java.awt.*;
import java.util.Random;

import javax.swing.*;

import subsym.boids.Boids;
import subsym.gui.AICanvas;
import subsym.gui.AIGui;
import subsym.gui.AITextArea;
import subsym.gui.Plot;
import subsym.q.QGui;

/**
 * Created by anon on 28.01.2015.
 */
public class Main {

  public static final String TAG = Main.class.getSimpleName();
  private static Plot chart;
  public static Random random = new Random();

  public static void main(String[] args) {
    loadGui();
  }

  private static void loadGui() {
    new AIGui() {

      @Override
      public int getDefaultCloseOperation() {
        return WindowConstants.EXIT_ON_CLOSE;
      }

      @Override
      public Dimension getPreferredSize() {
        return new Dimension(300, 200);
      }

      @Override
      protected void init() {
//        AIPanel panel = new AIPanel();
//        panel.setPreferredSize(new Dimension(300, 200));
//        AIButton broid = new AIButton("Boids");
//
//        broid.addActionListener(e -> broid());
//        panel.add(broid);
//
//        buildFrame(panel, null, null);
//
//        BeerGame.demo();
//        FlatlandGui.demo();

//        new Genetics();
//        FlatlandQSimulator sim = new FlatlandQSimulator();
        new QGui();
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

  private static void broid() {
    new Thread(new Boids()).start();
  }


  public static Random random() {
    return random;
  }
}
