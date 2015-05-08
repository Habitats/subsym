package subsym.q;

/**
 * Created by mail on 08.05.2015.
 */
public class QPreferences {

  public static String[] SCENARIOS = new String[] //
      {"1-simple.txt", "2-still-simple.txt", "3-dont-be-greedy.txt", "4-big-one.txt", "5-even-bigger.txt"};
  public static final boolean DEBUG = false;

  public static final boolean RUN_FOREVER = true;
  public static final boolean DRAW_ARROWS = true;
  public static final int BACKUP_THRESHOLD = 1;
  public static final int MAX_ITERATION = 3000;
  public static final double UPPER_RANDOM_THRESHOLD = .31;
  public static final double LOWER_RANDOM_THRESHOLD = 0.01;
  public static final String SCENARIO = SCENARIOS[4];

  public static final double STEP_PENALTY = -0.50000001;
  public static final double FOOD_REWARD = 10;
  public static final double POISON_PENALTY = -20;
}
