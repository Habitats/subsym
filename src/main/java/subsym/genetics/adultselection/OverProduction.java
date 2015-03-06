package subsym.genetics.adultselection;

import subsym.genetics.Population;

/**
 * Created by Patrick on 02.03.2015.
 */
public class OverProduction implements AdultSelection {

  public double overProductionRate;

  public OverProduction(double overProductionRate) {
    this.overProductionRate = overProductionRate;
  }

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
    int max = (int) (population.getMaxPopulationSize() * overProductionRate);
    return max - population.getNextGeneration().size();
  }

  public double getOverProductionRate() {
    return overProductionRate;
  }
}
