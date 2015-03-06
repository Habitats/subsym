package subsym.genetics.adultselection;

import java.util.Arrays;
import java.util.List;

import subsym.genetics.Population;

/**
 * Created by Patrick on 02.03.2015.
 */
public interface AdultSelection {


  void selectAdults(Population population);

  static List<String> values() {
    return Arrays
        .asList(FullTurnover.class.getSimpleName(), Mixing.class.getSimpleName(), OverProduction.class.getSimpleName());
  }

  int getFreeSpots(Population population);
}
