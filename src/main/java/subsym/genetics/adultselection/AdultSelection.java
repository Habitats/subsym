package subsym.genetics.adultselection;

import subsym.genetics.Population;

/**
 * Created by Patrick on 02.03.2015.
 */
public interface AdultSelection {


  void selectAdults(Population population);

  void cleanUp(Population population);
}
