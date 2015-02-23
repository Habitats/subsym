package subsym.surprisingsequence;

import java.util.BitSet;
import java.util.List;

import subsym.ga.Genotype;
import subsym.ga.Phenotype;

/**
 * Created by anon on 23.02.2015.
 */
public class SurprisingGenotype extends Genotype {

  private static final String TAG = SurprisingGenotype.class.getSimpleName();
  private List<Integer> alphabet;
  private SurprisingPhenotype phenotype;
  private int groupSize;

  public SurprisingGenotype() {
  }

  @Override
  public void copy(Genotype copy) {
    SurprisingGenotype surpriseCopy = (SurprisingGenotype) copy;
    surpriseCopy.alphabet = alphabet;
    surpriseCopy.phenotype = new SurprisingPhenotype(surpriseCopy);
    surpriseCopy.groupSize = groupSize;
  }

  public SurprisingGenotype(List<Integer> permutation, List<Integer> alphabet) {
    this.alphabet = alphabet;
    groupSize = getBitGroupSize(permutation);
    setSize(groupSize * permutation.size());
    bits = toBitSet(permutation, groupSize);
    phenotype = new SurprisingPhenotype(this);
//    Log.v(TAG, phenotype);
  }

  public int getGroupSize() {
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

  public List<Integer> toList() {
    return toList(groupSize);
  }

  public List<Integer> getAlphabet() {
    return alphabet;
  }

  @Override
  public String toString() {
    return super.toString() + phenotype.toString();
  }
}
