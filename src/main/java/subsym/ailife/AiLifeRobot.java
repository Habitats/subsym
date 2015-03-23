package subsym.ailife;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Patrick on 23.03.2015.
 */
public class AiLifeRobot {

  public List<Integer> getFoodSensorInput() {
    return Arrays.asList(0, 1, 1);
  }

  public List<Integer> getPoisonSensorInput() {
    return Arrays.asList(1, 0, 0);
  }
}
