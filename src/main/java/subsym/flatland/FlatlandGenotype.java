package subsym.flatland;

import subsym.ann.AnnPreferences;
import subsym.genetics.Genotype;
import subsym.genetics.Phenotype;

/**
 * Created by anon on 21.03.2015.
 */
public class FlatlandGenotype extends Genotype {

  private final AnnPreferences prefs;
  private FlatlandPhenotype phenotype;

  public FlatlandGenotype(AnnPreferences prefs) {
    super(true);
    this.prefs = prefs;
    phenotype = new FlatlandPhenotype(this, prefs);
  }

  public void randomize() {
    setRandom(phenotype.getNumWeights() * getBitGroupSize());
  }

  @Override
  protected Genotype newInstance() {
    return new FlatlandGenotype(prefs);
  }

  @Override
  public void copy(Genotype copy) {
    FlatlandGenotype aiCopy = (FlatlandGenotype) copy;
    aiCopy.phenotype = new FlatlandPhenotype(aiCopy, prefs);
  }

  @Override
  public Phenotype getPhenotype() {
    return phenotype;
  }

  @Override
  public int getBitGroupSize() {
    return 10;
  }

  @Override
  public String toString() {
    return super.toString() + getPhenotype().toString();
  }
}
