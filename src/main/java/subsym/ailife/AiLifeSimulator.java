package subsym.ailife;

import subsym.ailife.entity.Robot;

/**
 * Created by mail on 04.05.2015.
 */
public interface AiLifeSimulator {

  void move(Robot robot);

  int getMaxSteps();
}
