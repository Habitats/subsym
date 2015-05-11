package subsym.flatland;

import java.util.BitSet;

import subsym.q.QState;

class FlatlandQState implements QState {

  public final BitSet id;
  private static int states = 0;
  private static int TOTAL_FOOD;

  public FlatlandQState(Flatland flatland) {
    BitSet foodLocations = flatland.getFoodId();
    BitSet robotLocation = flatland.getRobotId();
    int size = flatland.getMaxFoodCount();
    TOTAL_FOOD = flatland.getMaxFoodCount();

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
    if (obj instanceof FlatlandQState) {
      return ((FlatlandQState) obj).id.equals(this.id);
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

