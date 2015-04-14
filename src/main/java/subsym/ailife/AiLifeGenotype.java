package subsym.ailife;

import subsym.ann.AnnPreferences;
import subsym.genetics.Genotype;
import subsym.genetics.Phenotype;

/**
 * Created by anon on 21.03.2015.
 */
public class AiLifeGenotype extends Genotype {

  private final AnnPreferences prefs;
  private AiLifePhenotype phenotype;

  public AiLifeGenotype(AnnPreferences prefs) {
    super(true);
    this.prefs = prefs;
    phenotype = new AiLifePhenotype(this, prefs);
  }

  public void randomize() {
    setRandom(phenotype.getNumWeights() * getBitGroupSize());
  }

  @Override
  protected Genotype newInstance() {
    return new AiLifeGenotype(prefs);
  }

  @Override
  public void copy(Genotype copy) {
    AiLifeGenotype aiCopy = (AiLifeGenotype) copy;
    aiCopy.phenotype = new AiLifePhenotype(this, prefs);
  }

  @Override
  public boolean equals(Object obj) {
    if (super.equals(obj)) {
      AiLifeGenotype other = (AiLifeGenotype) obj;
      return other.phenotype.equals(phenotype);
    }
    return false;
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
