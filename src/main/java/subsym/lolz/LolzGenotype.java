package subsym.lolz;

import subsym.ga.Genotype;

/**
 * Created by anon on 23.02.2015.
 */
public class LolzGenotype extends Genotype {

  protected LolzGenotype(int size) {
    super(size);
  }

  @Override
  protected Genotype newInstance(int size) {
    return new LolzGenotype(size);
  }
}
