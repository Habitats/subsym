package subsym.flatland;

import subsym.flatland.entity.Robot;

/**
 * Created by mail on 04.05.2015.
 */
public interface FlatlandSimulator {

  void move(Robot robot);

  int getMaxSteps();

  default void onTick() {
  }

  void onNormalMove();

  void onFoodConsumed();

  void onPoisonConsumed();
}
