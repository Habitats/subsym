package subsym.q;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import javax.swing.*;

/**
 * Created by mail on 08.05.2015.
 */
public class QPreferences {

  public static final String PATH = "q";
  public static boolean SHOULD_TERMINATE = false;
  public static List<String> SCENARIOS = Arrays.asList(new File("q").list());

  public static final boolean DEBUG = false;
  public static boolean RUN_FOREVER = true;
  public static boolean DRAW_ARROWS = false;

  public static final int BACKUP_THRESHOLD = 1;
  public static final int MAX_ITERATION = 3000;
  public static final double UPPER_RANDOM_THRESHOLD = .31;
  public static final double LOWER_RANDOM_THRESHOLD = 0.01;
  public static String SCENARIO = SCENARIOS.get(4);

  public static final double STEP_PENALTY = -0.50000001;
  public static final double FOOD_REWARD = 10;
  public static final double POISON_PENALTY = -20;
  private static JProgressBar bar;

  public static void setProgress(int i, int iterations) {
    bar.setMaximum(iterations);
    bar.setMinimum(0);
    bar.setValue(i);
  }

  public static void setProgressBar(JProgressBar bar){
    QPreferences.bar = bar;
  }
}
