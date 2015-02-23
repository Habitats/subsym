package subsym.ga;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.Iterator;
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
  private int size;

  // ###############################################################################
  // ### CONSTRUCTORS ##############################################################
  // ###############################################################################

  protected Genotype() {
  }

  protected abstract Genotype newInstance();

  /**
   * Static factory method for initialization of a random bit vector
   */
  public Genotype setRandom(int size) {
    this.size = size;
    bits = new BitSet();
    for (int i = 0; i < size; i++) {
      bits.set(i, random.nextBoolean());
    }
    return this;
  }

  public Genotype setEmpty(int size) {
    this.size = size;
    bits = new BitSet();
    bits.set(0, size, false);
    return this;
  }

  public Genotype copy() {
    Genotype copy = newInstance();
    copy.bits = bits.get(0, bits.length());
    copy.size = size;
    copy(copy);
    return copy;
  }

  public abstract void copy(Genotype copy);

  public Genotype fromString(final String s) {
    size = s.length();
    bits = BitSet.valueOf(new long[]{Long.parseLong(s, 2)});
    return this;
  }

  // ###############################################################################
  // ### INSTANCE PROPERTIES #######################################################
  // ###############################################################################

  protected BitSet bits;


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
    Random r = new Random();
    for (int i = 0; i < numBits; i++) {
      Collections.swap(randomSequence, i, i + r.nextInt(randomSequence.size() - i));
    }
    IntStream.range(0, numBits).forEach(i -> bits.flip(randomSequence.remove(0)));
  }

  public BitSet toBitSet(List<Integer> ints, int groupSize) {
    BitSet bitSet = new BitSet();

    Iterator<Integer> iter = ints.iterator();
    IntStream.range(0, ints.size()).forEach(intIndex -> {
      BitSet bitInt = BitSet.valueOf(new long[]{iter.next()});
      int start = intIndex * groupSize;
      IntStream.range(0, groupSize).forEach(bitIndex -> bitSet.set(start + bitIndex, bitInt.get(bitIndex)));
    });
    return bitSet;
  }

  public List<Integer> toList(int bitGroupSize) {
    List<Integer> ints = new ArrayList<>();

    int numInts = size / bitGroupSize;
    IntStream.range(0, numInts).forEach(intIndex -> {
      int start = intIndex * bitGroupSize;
      BitSet bitSet = bits.get(start, start + bitGroupSize);
      int bitInt = !bitSet.isEmpty() ? (int) bitSet.toLongArray()[0] : 0;
      ints.add(bitInt);
    });
    return ints;
  }

  public String getBitsString() {
    BigInteger b = BigInteger.ZERO;
    for (int i = bits.nextSetBit(0); i >= 0; i = bits.nextSetBit(i + 1)) {
      b = b.setBit(i);
    }
    return b.toString(2);
  }

  public List<Integer> getOnBits() {
    List<Integer> lst = new ArrayList<>();
    for (int i = bits.nextSetBit(0); i >= 0; i = bits.nextSetBit(i + 1)) {
      lst.add(i);
    }

    Collections.reverse(lst);
    return lst;
  }

  public double fitness() {
    return getPhenotype().fitness();
  }

  public void invert() {
    bits.flip(0, size);
  }

  public boolean shouldDie() {
    return shouldDie;
  }

  public void tagForRemoval() {
    shouldDie = true;
  }

  public void tagForRevival() {
    shouldDie = false;
  }

  public void setGeneration(int generation) {
    this.generation = generation;
  }

  public void setSize(int size) {
    this.size = size;
  }

  public int size() {
    return size;
  }

  public int getBitGroupSize(List<Integer> ints) {
    return BigInteger.valueOf(Collections.max(ints)).bitLength();
  }

  protected BitSet getBits() {
    return bits;
  }

  public abstract Phenotype getPhenotype();

  public int getGeneration() {
    return generation;
  }


  public String toString() {
    return String.format("%" + size() + "s - From Gen: %6d - Keep: %s", //
                         getBitsString(), getGeneration(), !shouldDie);
  }

  @Override
  public boolean equals(Object obj) {
    return obj instanceof Genotype && bits.equals(((Genotype) obj).bits);
  }

  @Override
  public int compareTo(Genotype o) {
    return (int) (o.fitness() - fitness());
  }

}