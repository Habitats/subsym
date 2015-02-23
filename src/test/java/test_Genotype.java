import org.junit.Test;

import java.util.stream.IntStream;

import subsym.ga.GeneticProblem;
import subsym.ga.Genotype;
import subsym.ga.Population;
import subsym.lolz.LolzGenotype;
import subsym.onemax.OneMaxGenotype;

import static org.junit.Assert.assertEquals;

/**
 * Created by anon on 22.02.2015.
 */
public class test_Genotype {

  @Test
  public void test_mutate() {
    Genotype v = new OneMaxGenotype().setRandom(10);
    Genotype u = v.copy();
    v.mutate(10);
    u.invert();
    assertEquals(v, u);
  }

  @Test
  public void test_equals() {
    Genotype v = new OneMaxGenotype().setEmpty(10);
    Genotype u = new OneMaxGenotype().setEmpty(10);
    assertEquals(v, u);
  }

  @Test
  public void test_copy() {
    Genotype v = new OneMaxGenotype().setEmpty(10);
    Genotype u = v.copy();
    assertEquals(v, u);
  }

  @Test
  public void test_crossover() {
    Genotype v = new OneMaxGenotype().fromString("11111000001010101010");
    Genotype u = new OneMaxGenotype().fromString("10101010101111100000");

    assertEquals(Genotype.crossOver(v, u, 0.5), new OneMaxGenotype().fromString("11111000001111100000"));
    assertEquals(Genotype.crossOver(u, v, 0.5), new OneMaxGenotype().fromString("10101010101010101010"));

    assertEquals(Genotype.crossOver(v, u, 0.8), new OneMaxGenotype().fromString("11111000001010100000"));
    assertEquals(Genotype.crossOver(v, u, 0.2), new OneMaxGenotype().fromString("11111010101111100000"));
  }

  @Test
  public void test_populationMutate() {
    Population p = getPopulation(10);
    Genotype v = new OneMaxGenotype().fromString("1111111111");
    Genotype u = new OneMaxGenotype().fromString("0000000000");
    p.add(v);
    p.add(u);
    p.mutate(1, 1);

    assertEquals(v, new OneMaxGenotype().fromString("0000000000"));
    assertEquals(u, new OneMaxGenotype().fromString("1111111111"));
  }

  @Test
  public void test_populationMisc() {
    Population p = getPopulation(10);
    Genotype v = new OneMaxGenotype().fromString("1111111111");
    Genotype u = new OneMaxGenotype().fromString("0000000000");
    p.add(v);
    p.add(u);

    assertEquals(p.size(), 12);
    assertEquals(v, p.getBestGenotype());
    assertEquals(u, p.getWorstGenotype());
  }

  @Test
  public void test_populationOverProductionSelection() {
    int initialSize = 10;
    Population p = getPopulation(initialSize);
    p.selectAdults(GeneticProblem.AdultSelection.OVER_PRODUCTION);
    p.crossOver(1, GeneticProblem.MateSelection.FITNESS_PROPORTIONATE);
    // adults + children * overProductionRate should be present
    assertEquals(p.nextGenerationSize(), (int) (initialSize * Population.overProductionRate));
    // only the initalSize amount should be retained
    p.cleanUp();
    assertEquals(p.size(), initialSize);
  }

  @Test
  public void test_populationMixingSelection() {
    Population p = getPopulation(10);
    p.selectAdults(GeneticProblem.AdultSelection.MIXING);
    p.crossOver(1, GeneticProblem.MateSelection.FITNESS_PROPORTIONATE);
    assertEquals(p.size() + p.nextGenerationSize(), 10 + p.getChildLimit());
    p.cleanUp();
    assertEquals(p.size(), 10);
  }

  @Test
  public void test_populationFullTurnoverSelection() {
    Population p = getPopulation(10);
    p.selectAdults(GeneticProblem.AdultSelection.FULL_TURNOVER);
    p.cleanUp();
    assertEquals(p.size(), 0);
  }

  @Test
  public void test_lolzGenotypeToPhenotype() {
    Genotype v = new LolzGenotype().fromString("1111000000");
    assertEquals((int) v.getPhenotype().fitness(), 4);
    Genotype u = new LolzGenotype().fromString("0011110000");
    assertEquals((int) u.getPhenotype().fitness(), 2);
    Genotype w = new LolzGenotype().fromString("1111111111");
    assertEquals((int) w.getPhenotype().fitness(), 10);
  }

  public Population getPopulation(int size) {
    Population p = new Population(size);
    IntStream.range(0, size).forEach(i -> p.add(new OneMaxGenotype().setRandom(size)));
    return p;
  }
}
