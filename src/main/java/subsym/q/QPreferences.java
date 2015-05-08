package subsym.q;

/**
 * Created by mail on 08.05.2015.
 */
public class QPreferences {

  public static String[] SCENARIOS = new String[] //
      {"1-simple.txt", "2-still-simple.txt", "3-dont-be-greedy.txt", "4-big-one.txt", "5-even-bigger.txt"};

  public static final boolean DEBUG = false;
  public static final int BACKUP_THRESHOLD = 1;
  public static final int MAX_ITERATION = 10000;
  public static final double UPPER_RANDOM_THRESHOLD = .200;
  public static final double LOWER_RANDOM_THRESHOLD = 0.01;
  public static final String SCENARIO = SCENARIOS[0];
}
