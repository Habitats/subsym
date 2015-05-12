package subsym.flatland;

import java.util.BitSet;

class FlatlandQState {

  //  public final BitSet id;
  private static short TOTAL_FOOD;

//  public FlatlandQState(Flatland flatland) {
//    BitSet foodLocations = flatland.getFoodId();
//    BitSet robotLocation = flatland.getRobotId();
//    TOTAL_FOOD = (short) flatland.getMaxFoodCount();
//
//    id = (BitSet) foodLocations.clone();
//    id.set(TOTAL_FOOD + robotLocation.nextSetBit(0));
//  }

//  @Override
//  public int hashCode() {
//    return id.hashCode();
//  }
//
//  @Override
//  public boolean equals(Object obj) {
//    if (obj instanceof FlatlandQState) {
//      return ((FlatlandQState) obj).id.equals(this.id);
//    }
//    return false;
//  }


  public static BitSet getFoodLocations(BitSet id) {
    return id.get(0, TOTAL_FOOD);
  }

  public static BitSet getRobotLocation(BitSet id) {
    return id.get(TOTAL_FOOD, TOTAL_FOOD + id.nextSetBit(TOTAL_FOOD));
  }


  public static BitSet from(Flatland flatland) {
    BitSet foodLocations = flatland.getFoodId();
    BitSet robotLocation = flatland.getRobotId();
    TOTAL_FOOD = (short) flatland.getMaxFoodCount();

    BitSet id = (BitSet) foodLocations.clone();
    id.set(TOTAL_FOOD + robotLocation.nextSetBit(0));
    return id;
  }

}

