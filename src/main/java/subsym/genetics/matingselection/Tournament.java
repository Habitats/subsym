package subsym.genetics.matingselection;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import subsym.genetics.Genotype;

/**
 * Created by Patrick on 02.03.2015.
 */
public class Tournament implements MatingSelection {

  private int tournamentLimit;
  private double chooseRandom;

  public Tournament(int tournamentLimit, double chooseRandom) {
    this.tournamentLimit = tournamentLimit;
    this.chooseRandom = chooseRandom;
  }

  public Genotype selectNext(List<Genotype> populationList) {
    Random r = new Random();
    if (r.nextDouble() < chooseRandom) {
      return populationList.remove(r.nextInt(populationList.size() - 1));
    }

    for (int i = 0; i < tournamentLimit; i++) {
      Collections.swap(populationList, i, i + r.nextInt(populationList.size() - i));
    }
    Genotype best = populationList.stream().limit(tournamentLimit).max(Comparator.<Genotype>reverseOrder()).get();
    populationList.remove(best);
    return best;
  }
}
