package subsym.lolz;

import java.util.Iterator;
import java.util.List;

import subsym.genetics.Phenotype;

/**
 * Created by anon on 23.02.2015.
 */
public class LolzPhenotype implements Phenotype {

  private final LolzGenotype lolzGenotype;

  public LolzPhenotype(LolzGenotype lolzGenotype) {
    this.lolzGenotype = lolzGenotype;
  }

  @Override
  public double fitness() {
    if (lolzGenotype.getBits().isEmpty()) {
      return Math.min(lolzGenotype.getZeroThreshold(), lolzGenotype.size()) / lolzGenotype.size();
    }
    List<Integer> arr = lolzGenotype.getOnBits();
    Iterator<Integer> iter = arr.iterator();
    Integer first = iter.next();
    double count = 0;
    if (first == lolzGenotype.size() - 1) {
      int next;
      count++;
      while (iter.hasNext()) {
        if ((next = iter.next()) == first - 1) {
          count++;
        } else {
          break;
        }
        first = next;
      }
    } else {
      count = lolzGenotype.size() - first - 1;
      if (count >= lolzGenotype.getZeroThreshold()) {
        count = lolzGenotype.getZeroThreshold();
      }
    }
    return count / lolzGenotype.size();
  }

  @Override
  public String toString() {
    return String.format("Fitness: %2d", (int) fitness());
  }
}
