package subsym.ailife;

import subsym.genetics.Genotype;
import subsym.genetics.Phenotype;
import subsym.models.Board;
import subsym.models.TileEntity;

/**
 * Created by anon on 21.03.2015.
 */
public class AiLifeGenotype extends Genotype {

  private Board<TileEntity> board;
  private AiLifePhenotype phenotype;

  public AiLifeGenotype(Board<TileEntity> board) {
    this.board = board;
    phenotype = new AiLifePhenotype(this);
  }

  @Override
  protected Genotype newInstance() {
    return new AiLifeGenotype(board);
  }

  @Override
  public void copy(Genotype copy) {
    AiLifeGenotype aiCopy = (AiLifeGenotype) copy;
    aiCopy.phenotype = new AiLifePhenotype(this);
    aiCopy.board = board;
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
