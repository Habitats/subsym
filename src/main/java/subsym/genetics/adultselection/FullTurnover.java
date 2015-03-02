package subsym.genetics.adultselection;

import subsym.genetics.Genotype;
import subsym.genetics.Population;

/**
 * Created by Patrick on 02.03.2015.
 */
public class FullTurnover implements AdultSelection {

  @Override
  public void selectAdults(Population population) {
    population.getCurrent().stream().forEach(Genotype::tagForRemoval);
    population.setFreeSpots(population.getMaxPopulationSize());
  }

  @Override
  public void cleanUp(Population population) {
    population.getCurrent().clear();
    population.getCurrent().addAll(population.getNextGeneration());
  }
}
