package subsym.genetics.adultselection;

import subsym.UniquePriorityQueue;
import subsym.genetics.Genotype;
import subsym.genetics.Population;

/**
 * Created by Patrick on 02.03.2015.
 */
public class Mixing implements AdultSelection {

  public double mixingRate;

  public Mixing(double mixingRate) {
    this.mixingRate = mixingRate;
  }

  private void removeBadAdults(UniquePriorityQueue currentPopulation, int maxPopulationSize) {
    currentPopulation.stream().sorted().limit(getAdultLimit(maxPopulationSize)).forEach(Genotype::tagForRevival);
    currentPopulation.removeIf(Genotype::shouldDie);
  }

  private void addNextGeneration(UniquePriorityQueue currentPopulation, UniquePriorityQueue nextGeneration,
                                 int maxPopulationSize) {
    currentPopulation.addAll(nextGeneration);
    while (currentPopulation.size() > maxPopulationSize) {
      currentPopulation.removeLast();
    }
  }

  private int getAdultLimit(int maxPopulationSize) {
    return (int) (mixingRate * maxPopulationSize);
  }

  @Override
  public void selectAdults(Population population) {
    int adultLimit = getAdultLimit(population.getMaxPopulationSize());
    population.getCurrent().stream().forEach(Genotype::tagForRemoval);
    population.getCurrent().stream().sorted().limit(adultLimit).forEach(Genotype::tagForRevival);

    int toKeepCount = (int) population.getCurrent().stream().filter(v -> !v.shouldDie()).count();
    int freeSpots = population.getMaxPopulationSize() - toKeepCount;
    population.setFreeSpots(freeSpots);
  }

  @Override
  public void cleanUp(Population population) {
    addNextGeneration(population.getCurrent(), population.getNextGeneration(), population.getMaxPopulationSize());
//    removeBadAdults(population.getCurrent(), population.getMaxPopulationSize());
  }
}
