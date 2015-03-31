package subsym.genetics.adultselection;

import subsym.genetics.Population;

/**
 * Created by Patrick on 02.03.2015.
 */
public class FullTurnover implements AdultSelection {

  @Override
  public void selectAdults(Population population) {
    population.getCurrent().clear();
    population.getCurrent().addAll(population.getNextGeneration());
    while (population.getCurrent().size() > population.getMaxPopulationSize()) {
      population.getCurrent().removeWorst();
    }
  }

  @Override
  public int getFreeSpots(Population population) {
    return population.getMaxPopulationSize() - population.nextGenerationSize();
  }
}
