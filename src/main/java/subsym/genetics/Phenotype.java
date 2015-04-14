package subsym.genetics;

/**
 * Created by anon on 21.02.2015.
 */
public interface Phenotype {

  double fitness();

  default void resetFitness() {
  }
}
