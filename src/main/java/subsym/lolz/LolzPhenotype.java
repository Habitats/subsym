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
      return lolzGenotype.size();
    }
    List<Integer> arr = lolzGenotype.getOnBits();
    Iterator<Integer> iter = arr.iterator();
    Integer first = iter.next();
    boolean firstIsZero = first != (lolzGenotype.size() - 1);
    int count = 0;
    if (first == lolzGenotype.size() - 1) {
      int next;
      count++;
      while (iter.hasNext()) {
        if ((next = iter.next()) == first - 1) {
          count++;
          if (firstIsZero && count >= lolzGenotype.getZeroThreshold()) {
            return count;
          }
        } else {
          break;
        }
        first = next;
      }
    } else {
      count = lolzGenotype.size() - first - 1;
    }
    return count;
  }

  @Override
  public String toString() {
    return String.format("Fitness: %2d", (int) fitness());
  }
}
