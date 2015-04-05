package subsym.genetics.matingselection;

import com.google.common.util.concurrent.AtomicDouble;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import subsym.genetics.Genotype;

/**
 * Created by Patrick on 02.03.2015.
 */
public interface MatingSelection {

  Genotype selectNext(List<Genotype> populationList);

  default Genotype spinAndRemove(List<Genotype> populationList, Function<Genotype, Double> mapper) {
    Map<Genotype, Double> adjusted = populationList.stream().collect(Collectors.toMap(i -> i, mapper, (a, b) -> a + b));
    double sum = adjusted.values().stream().mapToDouble(Double::doubleValue).sum();
    double index = Math.random() * sum;
    AtomicDouble i = new AtomicDouble(0);
    Genotype parent = adjusted.keySet().stream()//
        .filter(v -> i.addAndGet(adjusted.get(v)) >= index)//
        .findFirst()//
        .orElse(populationList.get(populationList.size() - 1));
    populationList.remove(parent);
    return parent;
  }

  static List<String> values() {
    return Arrays.asList(FitnessProportiate.class.getSimpleName(), SigmaScaled.class.getSimpleName(), Tournament.class.getSimpleName(),
                         Boltzman.class.getSimpleName(), Rank.class.getSimpleName());
  }
}
