import org.junit.Test;

import java.util.Arrays;
import java.util.BitSet;
import java.util.List;
import java.util.stream.IntStream;

import subsym.genetics.GeneticProblem;
import subsym.genetics.Genotype;
import subsym.genetics.Population;
import subsym.lolz.LolzGenotype;
import subsym.onemax.OneMaxGenotype;
import subsym.surprisingsequence.SurprisingGenotype;
import subsym.surprisingsequence.SurprisingPhenotype;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

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
    p.crossOver(1, Math.random(), GeneticProblem.MateSelection.FITNESS_PROPORTIONATE);
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
    p.crossOver(1, Math.random(), GeneticProblem.MateSelection.FITNESS_PROPORTIONATE);
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

  @Test
  public void test_intToBit() {
    List<Integer> permutation = Arrays.asList(1, 2, 3, 4);
    SurprisingGenotype v = new SurprisingGenotype(permutation, permutation);
    BitSet bits = v.toBitSet(permutation, v.getBitGroupSize());
    v.setBits(bits);
    SurprisingGenotype
        w =
        (SurprisingGenotype) new SurprisingGenotype(permutation, permutation).fromString("100011010001");
    assertEquals(v.getOnBits(), w.getOnBits());
  }

  @Test
  public void test_bitToInt() {
    SurprisingGenotype w = (SurprisingGenotype) new SurprisingGenotype(3).fromString("100011010001");
    List<Integer> lst1 = w.toList();
    List<Integer> lst2 = Arrays.asList(1, 2, 3, 4);

    assertEquals(lst1, lst2);
  }

  @Test
  public void test_bitBlockCrossOver() {
    List<Integer> alphabet = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8);
    Genotype v = new SurprisingGenotype(Arrays.asList(1, 2, 3, 4), alphabet);
    Genotype u = new SurprisingGenotype(Arrays.asList(5, 6, 7, 8), alphabet);

    Genotype w;
    w = Genotype.crossOver(v, u, 0);
    assertEquals(w.toList(), Arrays.asList(5, 6, 7, 8));

    w = Genotype.crossOver(v, u, 0.25);
    assertEquals(w.toList(), Arrays.asList(5, 6, 7, 4));

    w = Genotype.crossOver(v, u, 0.5);
    assertEquals(w.toList(), Arrays.asList(5, 6, 3, 4));

    w = Genotype.crossOver(v, u, 0.75);
    assertEquals(w.toList(), Arrays.asList(5, 2, 3, 4));
    w = Genotype.crossOver(v, u, 0.76);
    assertEquals(w.toList(), Arrays.asList(5, 2, 3, 4));
    w = Genotype.crossOver(v, u, 0.74);
    assertEquals(w.toList(), Arrays.asList(5, 2, 3, 4));

    w = Genotype.crossOver(v, u, 0.90);
    assertEquals(w.toList(), Arrays.asList(1, 2, 3, 4));
    w = Genotype.crossOver(v, u, 1);
    assertEquals(w.toList(), Arrays.asList(1, 2, 3, 4));
  }

  @Test
  public void test_surprisingSequence() {
    List<Integer> seq1 = Arrays.asList(1, 2, 2, 1);
    double fg1 = SurprisingPhenotype.getGlobalSurprisingSequenceFitness(seq1);
    double fl1 = SurprisingPhenotype.getLocalSurprisingSequenceFitness(seq1);
    assertEquals(fg1, 1., 0);
    assertEquals(fl1, 1., 0);

    List<Integer> seq2 = Arrays.asList(1, 2, 3, 3, 2, 1);
    double fg2 = SurprisingPhenotype.getGlobalSurprisingSequenceFitness(seq2);
    double fl2 = SurprisingPhenotype.getLocalSurprisingSequenceFitness(seq2);
    assertEquals(fg2, 1., 0);
    assertEquals(fl2, 1., 0);

    List<Integer> seq3 = Arrays.asList(1, 1, 2, 3, 3);
    double fg3 = SurprisingPhenotype.getGlobalSurprisingSequenceFitness(seq3);
    double fl3 = SurprisingPhenotype.getLocalSurprisingSequenceFitness(seq3);
    assertNotEquals(fg3, 1., 0);
    assertEquals(fl3, 1., 0);

    List<Integer> seq4 = Arrays.asList(1, 2, 2, 1, 3, 3, 1);
    double fg4 = SurprisingPhenotype.getGlobalSurprisingSequenceFitness(seq4);
    double fl4 = SurprisingPhenotype.getLocalSurprisingSequenceFitness(seq4);
    assertNotEquals(fg4, 1., 0);
    assertEquals(fl4, 1., 0);

    List<Integer> seq5 = Arrays.asList(0, 0, 2, 3, 3, 0, 3, 1, 2, 1, 1, 3, 2, 2, 0, 1, 0);
    double fg5 = SurprisingPhenotype.getGlobalSurprisingSequenceFitness(seq5);
    double fl5 = SurprisingPhenotype.getLocalSurprisingSequenceFitness(seq5);
    assertNotEquals(fg5, 1., 0);
    assertEquals(fl5, 1., 0);
  }

  public Population getPopulation(int size) {
    Population p = new Population(size);
    IntStream.range(0, size).forEach(i -> p.add(new OneMaxGenotype().setRandom(size)));
    return p;
  }
}
