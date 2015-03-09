package subsym.genetics;

import java.util.Collection;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * Created by anon on 24.02.2015.
 */
public class PopulationList {

  private PriorityQueue<Genotype> c;

  public PopulationList() {
//    c = MinMaxPriorityQueue.create();
    c = new PriorityQueue<>();
  }

  public Stream<Genotype> stream() {
    return c.stream();
  }

  public boolean add(Genotype child) {
    return c.add(child);
  }

  public void clear() {
    c.clear();
  }

  public void addAll(PopulationList nextGeneration) {
    addAll(nextGeneration.get());
  }

  public void addAll(Collection<Genotype> nextGeneration) {
    c.addAll(nextGeneration);
  }

  public int size() {
    return c.size();
  }

  public void removeWorst() {
    Genotype v = c.stream().sorted(Comparator.<Genotype>reverseOrder()).findFirst().get();
    c.remove(v);
  }

  public boolean removeIf(Predicate<Genotype> filter) {
    return c.removeIf(filter);
  }

  public Genotype peekWorst() {
    return c.stream().sorted(Comparator.<Genotype>reverseOrder()).findFirst().get();
  }

  public Genotype peekBest() {
//    return c.peekFirst();
    return c.peek();
  }

  public Collection<Genotype> get() {
    return c;
  }

  @Override
  public String toString() {
    return String.format("Size: %d ", c.size());
  }

  public boolean contains(Genotype v) {
    return c.contains(v);
  }

  public Genotype removeBest() {
    return c.poll();
  }
}
