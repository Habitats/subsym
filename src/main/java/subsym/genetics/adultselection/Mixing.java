package subsym.genetics.adultselection;

import java.util.stream.IntStream;

import subsym.genetics.Population;

/**
 * Created by Patrick on 02.03.2015.
 */
public class Mixing implements AdultSelection {

  private double mixingRate;

  public Mixing(double mixingRate) {
    this.mixingRate = mixingRate;
  }

  @Override
  public void selectAdults(Population population) {
    int adultLimit = (int) (mixingRate * population.getMaxPopulationSize());
    IntStream.range(0, adultLimit)
        .forEach(i -> population.getNextGeneration().add(population.getCurrent().removeBest()));
    population.getCurrent().clear();
    population.getCurrent().addAll(population.getNextGeneration());
    while (population.getCurrent().size() > population.getMaxPopulationSize()) {
      population.getCurrent().removeWorst();
    }
  }

  @Override
  public int getFreeSpots(Population population) {
    int max = (int) (population.getMaxPopulationSize() * mixingRate);
    return max - population.getNextGeneration().size();
  }

  public double getMixingRate() {
    return mixingRate;
  }
}
