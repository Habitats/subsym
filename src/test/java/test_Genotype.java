import org.junit.Test;

import java.util.Arrays;
import java.util.BitSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import subsym.genetics.GeneticPreferences;
import subsym.genetics.Genotype;
import subsym.genetics.Population;
import subsym.genetics.PopulationList;
import subsym.genetics.adultselection.FullTurnover;
import subsym.genetics.adultselection.Mixing;
import subsym.genetics.adultselection.OverProduction;
import subsym.genetics.matingselection.Boltzman;
import subsym.genetics.matingselection.FitnessProportiate;
import subsym.genetics.matingselection.MatingSelection;
import subsym.genetics.matingselection.Rank;
import subsym.genetics.matingselection.SigmaScaled;
import subsym.lolz.LolzGenotype;
import subsym.onemax.OneMax;
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
  public void test_mutate2() {
    Genotype v = new OneMaxGenotype().setRandom(10);
    Genotype u = v.copy();
    v.mutate(1.);
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
    Population p = getPopulation(1000);
    Genotype v = new OneMaxGenotype().fromString("1111111111");
    Genotype u = new OneMaxGenotype().fromString("0000000000");
    p.add(v);
    p.add(u);
    p.mutate(1, 1);

//    assertEquals(v, new OneMaxGenotype().fromString("0000000000"));
//    assertEquals(u, new OneMaxGenotype().fromString("1111111111"));
  }

  @Test
  public void test_populationMisc() {
    Population p = getPopulation(0);
    Genotype v = new OneMaxGenotype().fromString("1111111111");
    Genotype u = new OneMaxGenotype().fromString("0000000000");
    while (p.size() < 10) {
      p.add(new OneMaxGenotype().setRandom(10));
    }
    p.add(v);
    p.add(u);

    assertEquals(p.size(), 12);
    assertEquals(v, p.getBestGenotype());
    assertEquals(u, p.getWorstGenotype());
  }

  //  @Test
  public void test_populationOverProductionSelection() {
    int initialSize = 10;
    Population p = getPopulation(initialSize);
    int overProductionRate = 2;
    p.crossOver(1, Math.random(), new FitnessProportiate());
//    adults + children * overProductionRate should be present
    assertEquals(p.nextGenerationSize(), initialSize * overProductionRate);
    // only the initalSize amount should be retained
    p.selectAdults(new OverProduction(overProductionRate));
    assertEquals(p.size(), initialSize);
  }

  //  @Test
  public void test_populationMixingSelection() {
    Population p = getPopulation(10);
    double mixingRate = .5;
    p.crossOver(1, Math.random(), new FitnessProportiate());
    assertEquals(p.size() + p.nextGenerationSize(), (int) (10 + p.getMaxPopulationSize() * (1 - mixingRate)));
    p.selectAdults(new Mixing(mixingRate));
    assertEquals(p.size(), 10);
  }

  @Test
  public void test_populationFullTurnoverSelection() {
    GeneticPreferences test = GeneticPreferences.getTest();
    Population p = new Population(test);
    p.selectAdults(new FullTurnover());
    assertEquals(p.size(), 0);
  }

  @Test
  public void test_standardDeviation() {
    List<Double> numbers = //
        Arrays.asList(9., 2., 5., 4., 12., 7., 8., 11., 9., 3., 7., 4., 12., 5., 4., 10., 9., 6., 9., 4.);
    double sd = Population.standardDeviation(numbers);

    assertEquals(sd, 2.983, 0.01);
  }

  @Test
  public void test_spin() {
    GeneticPreferences test = GeneticPreferences.getTest();
    test.setPopulationSize(10);
    Population p = new Population(test);
    while (p.size() < 10) {
      p.add(new OneMaxGenotype().setRandom(4));
    }
    Genotype parent;
    MatingSelection sigmaScaled = new SigmaScaled();
    parent = sigmaScaled.selectNext(p.getCurrent().stream().collect(Collectors.toList()));
    MatingSelection rank = new Rank();
    parent = rank.selectNext(p.getCurrent().stream().collect(Collectors.toList()));
    MatingSelection boltzman = new Boltzman();
    parent = boltzman.selectNext(p.getCurrent().stream().collect(Collectors.toList()));
    MatingSelection fitnessProportiate = new FitnessProportiate();
    parent = fitnessProportiate.selectNext(p.getCurrent().stream().collect(Collectors.toList()));

  }

  //  @Test
  public void test_lolzGenotypeToPhenotype() {
    Population p = new Population(GeneticPreferences.getTest());
    Genotype i = new LolzGenotype(5).fromString("1011000000");
    assertEquals((int) i.getPhenotype().fitness(), 3 / 10.);
    Genotype v = new LolzGenotype(5).fromString("1111000000");
    assertEquals((int) v.getPhenotype().fitness(), 4 / 10.);
    Genotype u = new LolzGenotype(5).fromString("0011110000");
    assertEquals((int) u.getPhenotype().fitness(), 2 / 10.);
    Genotype w = new LolzGenotype(5).fromString("1111111111");
    assertEquals((int) w.getPhenotype().fitness(), 10 / 10.);

    p.add(v);
    p.add(u);
    p.add(w);
    p.add(i);
    assertEquals(p.getBestGenotype(), w);
    assertEquals(p.getWorstGenotype(), i);
  }

  @Test
  public void test_intToBit() {
    List<Integer> permutation = Arrays.asList(1, 2, 3, 4);
    SurprisingGenotype v = new SurprisingGenotype(permutation, permutation, true, false);
    BitSet bits = v.toBitSet(permutation, v.getBitGroupSize());
    v.setBits(bits);
    SurprisingGenotype w = (SurprisingGenotype) new SurprisingGenotype(permutation, permutation, true, false).fromString("100011010001");
    assertEquals(v.getOnBits(), w.getOnBits());
  }

  @Test
  public void test_bitToInt() {
    SurprisingGenotype w = (SurprisingGenotype) new SurprisingGenotype(3, false).fromString("100011010001");
    List<Integer> lst1 = w.toList();
    List<Integer> lst2 = Arrays.asList(1, 2, 3, 4);

    assertEquals(lst1, lst2);
  }

  @Test
  public void test_bitBlockCrossOver() {
    List<Integer> alphabet = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8);
    Genotype v = new SurprisingGenotype(Arrays.asList(1, 2, 3, 4), alphabet, true, false);
    Genotype u = new SurprisingGenotype(Arrays.asList(5, 6, 7, 8), alphabet, true, false);

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
  public void test_grayCode() {
    List<Integer> alphabet = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8);

    Genotype g1 = new SurprisingGenotype(Arrays.asList(5, 6, 7, 8), alphabet, true, true);
    Genotype g2 = new SurprisingGenotype(Arrays.asList(5, 6, 7, 8), alphabet, true, false);
    String g1s = g1.getBitsString();
    String g2s = g2.getBitsString();
    List<Integer> grayList = g1.toList();
    List<Integer> list = g2.toList();
    assertEquals(grayList, list);
    g1.mutate(.5);
    g1.mutate(.5);
    g1.mutate(.5);
    g1.mutate(.5);
    g1.mutate(.5);
    g1.mutate(.5);
    g1.mutate(.5);
    BitSet grayBits = g1.toBitSet(grayList, 4);
    BitSet bits = g2.toBitSet(list, 4);
    assertNotEquals(grayBits, bits);
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

  @Test
  public void test_remove() {
    PopulationList q = new PopulationList();
    int size = 1000;
    IntStream.range(0, size).forEach(v -> q.add(new OneMaxGenotype().setRandom(10)));
    assertEquals(q.size(), size);
    IntStream.range(0, 200).forEach(v -> q.removeWorst());
    assertEquals(q.size(), size - 200);
  }

  public Population getPopulation(int size) {
    GeneticPreferences test = GeneticPreferences.getTest();
    test.setPopulationSize(size);
    Population p = new Population(test);
    while (p.size() < size) {
      p.add(new OneMaxGenotype().setRandom(10));
    }
    return p;
  }

  @Test
  public void test_rank() {
    Population p = getPopulation(10);
    Rank rank = new Rank();
    GeneticPreferences prefs = GeneticPreferences.getTest();
    prefs.setMateSelectionMode(rank);

    OneMax om = new OneMax(prefs, 10);
//    om.cleanUp();
//    om.crossOver();
  }
}
