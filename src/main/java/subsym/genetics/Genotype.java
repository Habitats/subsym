package subsym.genetics;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import subsym.ann.ArtificialNeuralNetwork;

/**
 * Created by anon on 21.02.2015.
 */
public abstract class Genotype implements Comparable<Genotype> {

  private static Random random = ArtificialNeuralNetwork.random();
  private int generationOrigin = 0;
  private int size;
  private Double fitness;
  private int currentGeneration;
  private final boolean grayCode;
  private static final String TAG = Genotype.class.getSimpleName();

  // ###############################################################################
  // ### CONSTRUCTORS ##############################################################
  // ###############################################################################

  protected Genotype(boolean shouldGrayCode) {
    this.grayCode = shouldGrayCode;
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
    resetFitness();
    return this;
  }

  public void resetFitness() {
    fitness = null;
    if (getPhenotype() != null && this != getPhenotype()) {
      getPhenotype().resetFitness();
    }
  }

  public Genotype setEmpty(int size) {
    this.size = size;
    bits = new BitSet();
    bits.set(0, size, false);
    resetFitness();
    return this;
  }

  public Genotype copy() {
    Genotype copy = newInstance();
    copy.bits = bits.get(0, bits.length());
    copy.size = size;
    copy.fitness = fitness;
    copy(copy);
    return copy;
  }

  public abstract void copy(Genotype copy);

  public Genotype fromString(final String s) {
    size = s.length();
    bits = BitSet.valueOf(new long[]{Long.parseLong(s, 2)});
    resetFitness();
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
    int cutAtGroup = (int) Math.round((v.size()) / v.getBitGroupSize() * (1 - cut));
    int cutAtBit = cutAtGroup * v.getBitGroupSize();
    for (int i = v.bits.length(); i >= 0; i--) {
      boolean value = cutAtBit <= i ? v.bits.get(i) : u.bits.get(i);
      w.bits.set(i, value);
    }

    w.resetFitness();
    return w;
  }

  // mutate n randomly chosen bits
  public void mutate(int numBits) {
    List<Integer> randomSequence = IntStream.range(0, size).boxed().collect(Collectors.toList());
    Random r = new Random();
    for (int i = 0; i < numBits; i++) {
      Collections.swap(randomSequence, i, i + r.nextInt(randomSequence.size() - i));
    }
    IntStream.range(0, numBits).forEach(i -> bits.flip(randomSequence.remove(0)));
    resetFitness();
  }

  // mutate each bit with a given probability
  public void mutate(double mutationRate) {
    IntStream.range(0, size()).filter(i -> Math.random() < mutationRate).forEach((bitIndex) -> {
      bits.flip(bitIndex);
    });
    resetFitness();
  }

  // mutate each bit with a given probability
  public void mutateBlock(double mutationRate) {
    for (int i = 0; i < size(); i += getBitGroupSize()) {
      if (Math.random() < mutationRate) {
        IntStream.range(i, i + getBitGroupSize()).forEach((bitIndex -> bits.set(bitIndex, Math.random() < .5 ? true : false)));
      }
    }
    resetFitness();
  }


  public BitSet toBitSet(List<Integer> ints, int groupSize) {
    BitSet bitSet = new BitSet();

    Iterator<Integer> iter = ints.iterator();
    IntStream.range(0, ints.size()).forEach(intIndex -> {
      long decimalNumber = shouldGrayCode() ? grayEncode(iter.next()) : iter.next();
      BitSet bitInt = BitSet.valueOf(new long[]{decimalNumber});
      int start = intIndex * groupSize;
      IntStream.range(0, groupSize).forEach(bitIndex -> bitSet.set(start + bitIndex, bitInt.get(bitIndex)));
    });
    resetFitness();
    return bitSet;
  }

  public List<Integer> toList() {
    List<Integer> ints = new ArrayList<>();

    int numInts = size / getBitGroupSize();
    IntStream.range(0, numInts).forEach(intIndex -> {
      int start = intIndex * getBitGroupSize();
      BitSet bitSet = bits.get(start, start + getBitGroupSize());
      int bitInt = shouldGrayCode() ? grayDecode(toIntegerFromBinary(bitSet)) : toIntegerFromBinary(bitSet);
      ints.add(bitInt);
    });
    return ints;
  }

  public int grayEncode(int n) {
    return n ^ (n >>> 1);
  }

  public int grayDecode(int n) {
    int p = n;
    while ((n >>>= 1) != 0) {
      p ^= n;
    }
    return p;
  }

  private int toIntegerFromBinary(BitSet bitSet) {
    return !bitSet.isEmpty() ? (int) bitSet.toLongArray()[0] : 0;
  }

  public String getBitsString() {
    return getBitsString(bits);
  }

  public String getBitsString(BitSet bits) {
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
    if (fitness == null) {
      return fitness = getPhenotype().fitness();
    }
    return fitness;
  }

  public void invert() {
    bits.flip(0, size);
    resetFitness();
  }

  public void setGenerationOrigin(int generationOrigin) {
    this.generationOrigin = generationOrigin;
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

  public boolean shouldGrayCode() {
    return grayCode;
  }

  public abstract Phenotype getPhenotype();

  public abstract int getBitGroupSize();

  public int getGenerationOrigin() {
    return generationOrigin;
  }

  public String getPaddedBitString() {
    return String.format("%" + size() + "s", getBitsString()).replaceAll(" ", "0");
  }

  public String toString() {
    return String.format("From Gen: %6d", getGenerationOrigin());
  }

  @Override
  public int hashCode() {
    return bits.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    return obj instanceof Genotype && bits.equals(((Genotype) obj).bits);
  }

  @Override
  public int compareTo(Genotype o) {
    return Double.compare(fitness(), o.fitness());
  }

  public void setCurrentGeneration(int currentGeneration) {
    this.currentGeneration = currentGeneration;
  }

  public int getCurrentGeneration() {
    return currentGeneration;
  }
}