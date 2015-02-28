package subsym;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;

import subsym.genetics.Genotype;

/**
 * Created by anon on 24.02.2015.
 */
public class UniquePriorityQueue {

  private final boolean ensureUnique;
  private PriorityQueue<Genotype> queue;
  private Set<Genotype> dupeSet;

  public UniquePriorityQueue(boolean ensureUnique) {
    this.ensureUnique = ensureUnique;
//    queue = MinMaxPriorityQueue.create();
    queue = new PriorityQueue<>();
    dupeSet = new HashSet<>();
    checkConsistency();
  }

  public Stream<Genotype> stream() {
    return queue.stream();
  }

  public boolean add(Genotype child) {
    boolean b;
    if (!ensureUnique || !dupeSet.contains(child)) {
      queue.add(child);
      dupeSet.add(child);
      b = true;
    } else {
      b = false;
    }
    checkConsistency();
    return b;
  }

  public void clear() {
    queue.clear();
    dupeSet.clear();
    checkConsistency();
  }

  public void addAll(UniquePriorityQueue nextGeneration) {
    addAll(nextGeneration.get());
    checkConsistency();
  }

  public void addAll(Collection<Genotype> nextGeneration) {
    Predicate<Genotype> shouldGet = v -> !ensureUnique || !dupeSet.contains(v);
    nextGeneration.stream().filter(shouldGet).forEach(v -> {
      queue.add(v);
      dupeSet.add(v);
    });
    checkConsistency();
  }

  public int size() {
    checkConsistency();
    return queue.size();
  }

  private void checkConsistency() {
    if (ensureUnique && (queue.size() != dupeSet.size())) {
      throw new IllegalStateException(
          String.format("Should be same size ... Q: %d - S: %d", queue.size(), dupeSet.size()));
    }
  }

  public void removeLast() {
//    dupeSet.remove(queue.removeLast());
    Genotype v = queue.stream().sorted(Comparator.<Genotype>reverseOrder()).findFirst().get();
    queue.remove(v);
    dupeSet.remove(v);
    checkConsistency();
  }

  public boolean removeIf(Predicate<Genotype> filter) {
    checkConsistency();
    dupeSet.removeIf(filter);
    boolean b = queue.removeIf(filter);
    try {
      checkConsistency();
    } catch (Exception e) {
      e.printStackTrace();
    }
    return b;
  }

  public Genotype peekWorst() {
    return queue.stream().sorted(Comparator.<Genotype>reverseOrder()).findFirst().get();
  }

  public Genotype peekBest() {
//    return queue.peekFirst();
    return queue.peek();
  }

  public Collection<Genotype> get() {
    return queue;
  }

  public int unique() {
    return dupeSet.size();
  }

  public boolean contains(Genotype v) {
    return queue.contains(v);
  }
}
