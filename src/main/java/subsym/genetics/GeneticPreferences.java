package subsym.genetics;

import subsym.genetics.adultselection.AdultSelection;
import subsym.genetics.adultselection.Mixing;
import subsym.genetics.matingselection.MatingSelection;
import subsym.genetics.matingselection.Tournament;

/**
 * Created by anon on 02.03.2015.
 */
public class GeneticPreferences {


  private int populationSize;
  private double crossOverRate;
  private double populationMutationRate;
  private double genomeMutationRate;
  private AdultSelection adultSelectionMode;
  private MatingSelection mateSelectionMode;

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

  public void setAdultSelectionMode(AdultSelection adultSelectionMode) {
    this.adultSelectionMode = adultSelectionMode;
  }

  public void setCrossOverRate(double crossOverRate) {
    this.crossOverRate = crossOverRate;
  }

  public void setGenomeMutationRate(double genomeMutationRate) {
    this.genomeMutationRate = genomeMutationRate;
  }

  public void setMateSelectionMode(MatingSelection mateSelectionMode) {
    this.mateSelectionMode = mateSelectionMode;
  }

  public void setPopulationMutationRate(double populationMutationRate) {
    this.populationMutationRate = populationMutationRate;
  }

  public void setPopulationSize(int populationSize) {
    this.populationSize = populationSize;
  }

  @Override
  public String toString() {
    return String.format("CR: %.2f - GMR: %.2f IMR: %.2f - AS: %s - MS: %s", //
                         crossOverRate, populationMutationRate, genomeMutationRate,
                         adultSelectionMode.getClass().getSimpleName(), mateSelectionMode.getClass().getSimpleName());
  }

  public static GeneticPreferences getDefault() {
    return new GeneticPreferences(40, 0.95, 0.9, 0.04, new Mixing(0.5), new Tournament(10, 0.05));

  }
}
