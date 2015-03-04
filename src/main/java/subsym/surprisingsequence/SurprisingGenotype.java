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
  private boolean global;
  private List<Integer> alphabet;
  private SurprisingPhenotype phenotype;
  private int groupSize;

  public SurprisingGenotype(int bitGroupSize) {
    this.groupSize = bitGroupSize;
  }

  public SurprisingGenotype() {
    this(1);
  }

  @Override
  public void copy(Genotype copy) {
    SurprisingGenotype surpriseCopy = (SurprisingGenotype) copy;
    surpriseCopy.alphabet = alphabet;
    surpriseCopy.phenotype = new SurprisingPhenotype(surpriseCopy);
    surpriseCopy.groupSize = groupSize;
    surpriseCopy.global = global;
  }

  public SurprisingGenotype(List<Integer> permutation, List<Integer> alphabet, boolean global) {
    this.alphabet = alphabet;
    this.global = global;
    groupSize = getBitGroupSize(alphabet);
    setSize(groupSize * permutation.size());
    bits = toBitSet(permutation, groupSize);
    phenotype = new SurprisingPhenotype(this);
//    Log.v(TAG, phenotype);
  }

  @Override
  public int getBitGroupSize() {
    return groupSize;
  }

  @Override
  protected Genotype newInstance() {
    return new SurprisingGenotype();
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
