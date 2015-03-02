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
    super();
  }

  @Override
  public double fitness() {
    double fitness = getBits().cardinality() / (double) size();
    return fitness;
  }

  @Override
  public void copy(Genotype copy) {
  }

  @Override
  public String toString() {
    return super.toString() + String.format(" - Fitness: %6.2f > Phenotype > %s", fitness(), getPaddedBitString());
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
