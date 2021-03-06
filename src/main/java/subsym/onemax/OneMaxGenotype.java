package subsym.onemax;

import subsym.genetics.Genotype;
import subsym.genetics.Phenotype;

/**
 * Created by anon on 22.02.2015.
 */
public class OneMaxGenotype extends Genotype implements Phenotype {

  @Override
  protected Genotype newInstance() {
    return new OneMaxGenotype();
  }

  public OneMaxGenotype() {
    super(false);
  }

  @Override
  public double fitness() {
//    double fitness = getBits().cardinality() / (double) size();
    double fitness = 1 / (1. + ((size() - getBits().cardinality())));
    return fitness;
  }

  @Override
  public void copy(Genotype copy) {
  }

  @Override
  public String toString() {
    return super.toString() + String.format(" - Fitness: %6.2f > Pheno > %s", fitness(), getPaddedBitString());
  }

  @Override
  public Phenotype getPhenotype() {
    return this;
  }

  @Override
  public int getBitGroupSize() {
    return 1;
  }
}
