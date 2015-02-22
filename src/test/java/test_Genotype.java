import org.junit.Test;

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
  }
}
