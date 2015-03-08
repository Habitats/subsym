package subsym.genetics.matingselection;

import java.util.List;
import java.util.function.Function;

import subsym.genetics.Genotype;

/**
 * Created by anon on 04.03.2015.
 */
public class Boltzman implements MatingSelection {

  private double temperature = 100;

  @Override
  public Genotype selectNext(List<Genotype> populationList) {
    double averageFitness = populationList.stream().mapToDouble(Genotype::fitness).average().getAsDouble();

    Function<Genotype, Double> toBoltzmanScale = v -> Math.exp(v.fitness() / temperature) / Math.exp(averageFitness);

    Genotype genotype = spinAndRemove(populationList, toBoltzmanScale);
    if (temperature - 0.001 > 0) {
      temperature -= 0.001;
    }
    return genotype;
  }
}
