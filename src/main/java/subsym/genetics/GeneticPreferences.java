package subsym.genetics;

import subsym.genetics.adultselection.AdultSelection;
import subsym.genetics.matingselection.MatingSelection;

/**
 * Created by anon on 02.03.2015.
 */
public class GeneticPreferences {


  private final int populationSize;
  private final double crossOverRate;
  private final double populationMutationRate;
  private final double genomeMutationRate;
  private final AdultSelection adultSelectionMode;
  private final MatingSelection mateSelectionMode;

  public GeneticPreferences(int populationSize, double crossOverRate, double populationMutationRate,
                            double genomeMutationRate, AdultSelection adultSelectionMode,
                            MatingSelection mateSelectionMode) {
    this.populationSize = populationSize;
    this.crossOverRate = crossOverRate;
    this.populationMutationRate = populationMutationRate;
    this.genomeMutationRate = genomeMutationRate;
    this.adultSelectionMode = adultSelectionMode;
    this.mateSelectionMode = mateSelectionMode;
  }

  public AdultSelection getAdultSelectionMode() {
    return adultSelectionMode;
  }

  public double getCrossOverRate() {
    return crossOverRate;
  }

  public double getGenomeMutationRate() {
    return genomeMutationRate;
  }

  public double getPopulationMutationRate() {
    return populationMutationRate;
  }

  public int getPopulationSize() {
    return populationSize;
  }

  public MatingSelection getMateSelectionMode() {
    return mateSelectionMode;
  }

  @Override
  public String toString() {
    return String.format("CR: %.2f - GMR: %.2f IMR: %.2f - AS: %s - MS: %s", //
                         crossOverRate, populationMutationRate, genomeMutationRate,
                         adultSelectionMode.getClass().getSimpleName(), mateSelectionMode.getClass().getSimpleName());
  }
}
