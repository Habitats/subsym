package subsym.surprisingsequence;

import java.util.BitSet;
import java.util.List;

import subsym.genetics.Genotype;
import subsym.genetics.Phenotype;

/**
 * Created by anon on 23.02.2015.
 */
public class SurprisingGenotype extends Genotype {

  private static final String TAG = SurprisingGenotype.class.getSimpleName();
  private final boolean grayCode;
  private boolean global;
  private List<Integer> alphabet;
  private SurprisingPhenotype phenotype;
  private int groupSize;

  public SurprisingGenotype(int bitGroupSize, boolean grayCode) {
    this.groupSize = bitGroupSize;
    this.grayCode = grayCode;
  }

  public SurprisingGenotype(boolean grayCode) {
    this(1, grayCode);
  }

  @Override
  public void copy(Genotype copy) {
    SurprisingGenotype surpriseCopy = (SurprisingGenotype) copy;
    surpriseCopy.alphabet = alphabet;
    surpriseCopy.phenotype = new SurprisingPhenotype(surpriseCopy);
    surpriseCopy.groupSize = groupSize;
    surpriseCopy.global = global;
  }

  public SurprisingGenotype(List<Integer> permutation, List<Integer> alphabet, boolean global, boolean grayCode) {
    this.alphabet = alphabet;
    this.global = global;
    groupSize = getBitGroupSize(alphabet);
    setSize(groupSize * permutation.size());
    bits = toBitSet(permutation, groupSize);
    phenotype = new SurprisingPhenotype(this);
    this.grayCode = grayCode;
//    Log.v(TAG, phenotype);
  }

  @Override
  public boolean shouldGrayCode() {
    return grayCode;
  }

  @Override
  public int getBitGroupSize() {
    return groupSize;
  }

  @Override
  protected Genotype newInstance() {
    return new SurprisingGenotype(shouldGrayCode());
  }

  @Override
  public Phenotype getPhenotype() {
    return phenotype;
  }


  public void setBits(BitSet bits) {
    this.bits = bits;
  }

  public List<Integer> getAlphabet() {
    return alphabet;
  }

  @Override
  public String toString() {
    return super.toString() + phenotype.toString();
  }

  public boolean global() {
    return global;
  }
}
