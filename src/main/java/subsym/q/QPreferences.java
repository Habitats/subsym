package subsym.q;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import javax.swing.*;

/**
 * Created by mail on 08.05.2015.
 */
public class QPreferences {

  private static JProgressBar bar;
  public static final String PATH = "q";
  public static boolean SHOULD_TERMINATE = false;
  public static List<String> SCENARIOS = Arrays.asList(new File("q").list());
  public static String SCENARIO = SCENARIOS.get(4);

  public static final boolean DEBUG = false;
  public static boolean RUN_FOREVER = false;
  public static boolean DRAW_ARROWS = false;

  public static boolean INTERMEDIATE_SIMULATIONS = true;
  public static int MAX_ITERATION = 10000;
  public static final int BACKUP_THRESHOLD = 1;
  public static final double UPPER_RANDOM_THRESHOLD = .41;
  public static final double LOWER_RANDOM_THRESHOLD = 0.105;

  public static final int RANDOM_ITERATION_THRESHOLD = 5000;
  public static final double LEARNING_RATE = 0.99;
  public static final double DISCOUNT_RATE = 0.99;

  public static final double STEP_PENALTY = -0.01000001;
  public static final double FOOD_REWARD = 10;
  public static final double POISON_PENALTY = -50;

  public static void setProgress(int i, int iterations) {
    SwingUtilities.invokeLater(() -> {
      bar.setMaximum(iterations);
      bar.setMinimum(0);
      bar.setValue(i);
    });
  }

  public static void setProgressBar(JProgressBar bar) {
    QPreferences.bar = bar;
  }
}
