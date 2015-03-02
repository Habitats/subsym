package subsym.genetics.adultselection;

import subsym.genetics.Genotype;
import subsym.genetics.Population;

/**
 * Created by Patrick on 02.03.2015.
 */
public class OverProduction implements AdultSelection {

  public double overProductionRate ;

  public OverProduction(int overProductionRate) {
    this.overProductionRate = overProductionRate;
  }

  @Override
  public void cleanUp(Population population) {
    population.getCurrent().clear();
    population.getCurrent().addAll(population.getNextGeneration());
    while (population.getCurrent().size() > population.getMaxPopulationSize()) {
      population.getCurrent().removeLast();
    }
  }

  @Override
  public void selectAdults(Population population) {
    population.getCurrent().stream().forEach(Genotype::tagForRemoval);
    population.setFreeSpots((int) (population.getMaxPopulationSize() * overProductionRate));
  }
}
