package subsym.ailife;

import subsym.genetics.Genotype;
import subsym.genetics.Phenotype;

/**
 * Created by anon on 21.03.2015.
 */
public class AiLifeGenotype extends Genotype {

  private AiLifePhenotype phenotype;

  public AiLifeGenotype() {
    phenotype = new AiLifePhenotype(this, new AiLifeRobot());
  }

  @Override
  protected Genotype newInstance() {
    return new AiLifeGenotype();
  }

  @Override
  public void copy(Genotype copy) {
    AiLifeGenotype aiCopy = (AiLifeGenotype) copy;
    aiCopy.phenotype = new AiLifePhenotype(this, new AiLifeRobot());
  }

  @Override
  public Phenotype getPhenotype() {
    return phenotype;
  }

  @Override
  public int getBitGroupSize() {
    return 10;
  }
}
