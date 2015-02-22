package subsym.ga;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.BitSet;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by anon on 21.02.2015.
 */
public abstract class Genotype implements Comparable<Genotype> {

  private static Random random = new Random();
  private boolean shouldDie = false;
  private int generation = 0;

  /**
   * Static factory method for initialization of a random bit vector
   */
  public Genotype setRandom() {
    bits = new BitSet();
    for (int i = 0; i < size; i++) {
      bits.set(i, random.nextBoolean());
    }
    return this;
  }

  protected abstract Genotype newInstance(int size);

  public Genotype setEmpty() {
    bits = new BitSet();
    bits.set(0, size, false);
    return this;
  }

  private final int size;
  private BitSet bits;

  protected Genotype(int size) {
    this.size = size;
  }

  public int getGeneration() {
    return generation;
  }

  public Genotype fromString(final String s) {
    Genotype v = newInstance(s.length());
    v.bits = BitSet.valueOf(new long[]{Long.parseLong(s, 2)});
    return v;
  }

  public String toString() {
    return String.format("%" + size() + "s - %d - G: %d - Keep: %s", //
                         bits.toLongArray().length > 0 ? Long.toString(bits.toLongArray()[0], 2) : "0",  //
                         fitness(), getGeneration(), !shouldDie);
  }

  /**
   * @return a new Genotype with a genome of v.from(0, cut) + u.from(cut, end)
   */
  public static Genotype crossOver(Genotype v, Genotype u, double cut) {
    Genotype w = v.copy();
    for (int i = v.bits.length(); i >= 0; i--) {
      boolean value = (1 - cut) * v.size <= i ? v.bits.get(i) : u.bits.get(i);
      w.bits.set(i, value);
    }
    return w;
  }

  public void mutate(int numBits) {
    List<Integer> randomSequence = IntStream.range(0, size).boxed().collect(Collectors.toList());
    Collections.shuffle(randomSequence);
    IntStream.range(0, numBits).forEach(i -> bits.flip(randomSequence.remove(0)));
  }

  public int size() {
    return size;
  }

  public int fitness() {
    return getPhenotype().fitness();
  }

  public Genotype copy() {
    Genotype copy = newInstance(size);
    copy.bits = bits.get(0, bits.length());
    return copy;
  }

  @Override
  public boolean equals(Object obj) {
    return obj instanceof Genotype && bits.equals(((Genotype) obj).bits);
  }

  public void invert() {
    bits.flip(0, size);
  }

  public void tagForRemoval() {
    shouldDie = true;
  }

  public boolean shouldDie() {
    return shouldDie;
  }

  public void tagForRevival() {
    shouldDie = false;
  }

  public void setGeneration(int generation) {
    this.generation = generation;
  }

  protected BitSet getBits() {
    return bits;
  }

  @Override
  public int compareTo(Genotype o) {
    return o.fitness() - fitness();
  }

  public Phenotype getPhenotype() {
    throw new NotImplementedException();
  }
}
