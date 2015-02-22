package subsym.onemax;

import subsym.ga.Genotype;
import subsym.ga.Phenotype;

/**
 * Created by anon on 22.02.2015.
 */
public class OneMaxGenotype extends Genotype implements Phenotype {

  @Override
  protected Genotype newInstance(int size) {
    return new OneMaxGenotype(size);
  }

  public OneMaxGenotype(int size) {
    super(size);
  }

  @Override
  public double fitness() {
    return getBits().cardinality();
  }

  @Override
  public Phenotype getPhenotype() {
    return this;
  }
}
