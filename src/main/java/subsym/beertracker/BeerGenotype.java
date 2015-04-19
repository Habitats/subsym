package subsym.beertracker;

import java.util.List;
import java.util.stream.Collectors;

import subsym.ann.AnnPreferences;
import subsym.ann.ArtificialNeuralNetwork;
import subsym.genetics.Genotype;
import subsym.genetics.Phenotype;

/**
 * Created by Patrick on 01.04.2015.
 */
public class BeerGenotype extends Genotype {

  private static final String TAG = BeerGenotype.class.getSimpleName();
  private BeerPhenotype phenotype;
  private AnnPreferences prefs;

  public BeerGenotype(AnnPreferences prefs) {
    super(prefs.shouldGrayCode());
    this.prefs = prefs;
    phenotype = new BeerPhenotype(this, prefs);
  }

  public void randomize() {
    int numNodes = phenotype.getNodeCount();
    int numWeights = phenotype.getNumWeights();
    setRandom((numWeights + numNodes * 2) * getBitGroupSize());
  }

  @Override
  public void mutate(double mutationRate) {
    List<Integer> vals = toList();
    List<Integer> mutated = vals.stream()//
        .map(v -> Math.random() < mutationRate ? ArtificialNeuralNetwork.random().nextInt(256) : v) //
//        .map(v -> mapThroughGaussian(mutationRate, v)) //
        .collect(Collectors.toList());
    bits = toBitSet(mutated, getBitGroupSize());
    resetFitness();
  }

  private Integer mapThroughGaussian(double mutationRate, Integer v) {
    int delta = (int) (ArtificialNeuralNetwork.random().nextGaussian() * v);
    int newVal = (v + delta) % 256;
//          Log.v(TAG, String.format("delta: %3d - old: %3d - new: %3d", delta, v, newVal));
    return Math.random() < mutationRate ? newVal : v;
  }

  @Override
  protected Genotype newInstance() {
    return new BeerGenotype(prefs);
  }

  @Override
  public void copy(Genotype copy) {
    BeerGenotype beerCopy = (BeerGenotype) copy;
    beerCopy.phenotype = new BeerPhenotype(beerCopy, prefs);
  }

  @Override
  public Phenotype getPhenotype() {
    return phenotype;
  }

  @Override
  public int getBitGroupSize() {
    return 8;
  }

  @Override
  public String toString() {
    return getPhenotype() + " > " + String.format("%.3f",fitness());
  }
}
