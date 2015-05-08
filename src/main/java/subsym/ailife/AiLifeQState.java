package subsym.ailife;

import java.util.BitSet;

import subsym.ailife.entity.Robot;
import subsym.q.QState;

class AiLifeQState implements QState {

  public final BitSet id;
  private static int states = 0;
  private static int TOTAL_FOOD;

  public AiLifeQState(Robot robot) {
    BitSet foodLocations = robot.getFoodId();
    BitSet robotLocation = robot.getRobotId();
    int size = robot.getFood().size();
    TOTAL_FOOD = robot.getFood().size();

    id = (BitSet) foodLocations.clone();
    id.set(size + robotLocation.nextSetBit(0));
    states++;
  }

  @Override
  public int hashCode() {
    return id.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof AiLifeQState) {
      return ((AiLifeQState) obj).id.equals(this.id);
    }
    return false;
  }

  public static BitSet getFoodLocations(BitSet id) {
    return id.get(0, TOTAL_FOOD);
  }

  public static BitSet getRobotLocation(BitSet id) {
    return id.get(TOTAL_FOOD, TOTAL_FOOD + id.nextSetBit(TOTAL_FOOD));
  }
}

