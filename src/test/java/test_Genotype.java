import org.junit.Test;

import subsym.ga.GeneticProblem;
import subsym.ga.Genotype;
import subsym.ga.Population;

import static org.junit.Assert.assertEquals;

/**
 * Created by anon on 22.02.2015.
 */
public class test_Genotype {

  @Test
  public void test_mutate() {
    Genotype v = Genotype.getRandom(10);
    Genotype u = v.copy();
    v.mutate(10);
    u.invert();
    assertEquals(v, u);
  }

  @Test
  public void test_equals() {
    Genotype v = Genotype.getEmpty(20);
    Genotype u = Genotype.getEmpty(20);
    assertEquals(v, u);
  }

  @Test
  public void test_copy() {
    Genotype v = Genotype.getEmpty(20);
    Genotype u = v.copy();
    assertEquals(v, u);
  }

  @Test
  public void test_crossover() {
    Genotype v = Genotype.fromString("11111000001010101010");
    Genotype u = Genotype.fromString("10101010101111100000");

    assertEquals(Genotype.crossOver(v, u, 0.5), Genotype.fromString("11111000001111100000"));
    assertEquals(Genotype.crossOver(u, v, 0.5), Genotype.fromString("10101010101010101010"));

    assertEquals(Genotype.crossOver(v, u, 0.8), Genotype.fromString("11111000001010100000"));
    assertEquals(Genotype.crossOver(v, u, 0.2), Genotype.fromString("11111010101111100000"));
  }

  @Test
  public void test_populationMutate() {
    Population p = new Population(10, 10);
    Genotype v = Genotype.fromString("1111111111");
    Genotype u = Genotype.fromString("0000000000");
    p.add(v);
    p.add(u);
    p.mutate(1, 1);

    assertEquals(v, Genotype.fromString("0000000000"));
    assertEquals(u, Genotype.fromString("1111111111"));
  }

  @Test
  public void test_populationMisc() {
    Population p = new Population(10, 10);
    Genotype v = Genotype.fromString("1111111111");
    Genotype u = Genotype.fromString("0000000000");
    p.add(v);
    p.add(u);

    assertEquals(p.size(), 12);
    assertEquals(v, p.getBestGenotype());
    assertEquals(u, p.getWorstGenotype());
  }

  @Test
  public void test_populationOverProductionSelection() {
    int initialSize = 10;
    Population p = new Population(initialSize, 10);
    p.selectAdults(GeneticProblem.AdultSelection.OVER_PRODUCTION);
    p.crossOver(1);
    // adults + children * overProductionRate should be present
    assertEquals(p.nextGenerationSize(), (int) (initialSize * Population.overProductionRate));
    // only the initalSize amount should be retained
    p.cleanUp();
    assertEquals(p.size(), initialSize);
  }

  @Test
  public void test_populationMixingSelection() {
    Population p = new Population(10, 10);
    p.selectAdults(GeneticProblem.AdultSelection.MIXING);
    p.crossOver(1);
    assertEquals(p.size() + p.nextGenerationSize(), 10 + p.getChildLimit());
    p.cleanUp();
    assertEquals(p.size(), 10);
  }

  @Test
  public void test_populationFullTurnoverSelection() {
    Population p = new Population(10, 10);
    p.selectAdults(GeneticProblem.AdultSelection.FULL_TURNOVER);
    p.cleanUp();
    assertEquals(p.size(), 0);
  }
}
