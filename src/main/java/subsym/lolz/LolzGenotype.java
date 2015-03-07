package subsym.lolz;

import java.util.BitSet;

import subsym.genetics.Genotype;
import subsym.genetics.Phenotype;

/**
 * Created by anon on 23.02.2015.
 */
public class LolzGenotype extends Genotype {

  private LolzPhenotype phenotype;
  private int zeroThreshold;

  public LolzGenotype(int zeroThreshold) {
    super();
    this.zeroThreshold = zeroThreshold;
  }

  @Override
  public Phenotype getPhenotype() {
    return phenotype;
  }

  @Override
  public int getBitGroupSize() {
    return 1;
  }

  @Override
  public Genotype setRandom(int size) {
    super.setRandom(size);
    phenotype = new LolzPhenotype(this);
    return this;
  }

  @Override
  public Genotype setEmpty(int size) {
    super.setEmpty(size);
    phenotype = new LolzPhenotype(this);
    return this;
  }

  @Override
  public Genotype fromString(String s) {
    super.fromString(s);
    phenotype = new LolzPhenotype(this);
    return this;
  }

  @Override
  public void copy(Genotype copy) {
    LolzGenotype lolzCopy = (LolzGenotype) copy;
    lolzCopy.phenotype = new LolzPhenotype(lolzCopy);
    lolzCopy.zeroThreshold = zeroThreshold;
  }

  @Override
  public String toString() {
    return super.toString() + String.format(" - Fitness: %6.2f > Pheno > %s", fitness(), getPaddedBitString());
  }

  @Override
  public BitSet getBits() {
    return super.getBits();
  }

  @Override
  protected Genotype newInstance() {
    return new LolzGenotype(zeroThreshold);
  }

  public int getZeroThreshold() {
    return zeroThreshold;
  }
}
