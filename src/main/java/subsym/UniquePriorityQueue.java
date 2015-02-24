package subsym;

import com.google.common.collect.MinMaxPriorityQueue;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;

import subsym.genetics.Genotype;

/**
 * Created by anon on 24.02.2015.
 */
public class UniquePriorityQueue {

  private final boolean ensureUnique;
  private MinMaxPriorityQueue<Genotype> queue;
  private Set<String> dupeSet;

  public UniquePriorityQueue(boolean ensureUnique) {
    this.ensureUnique = ensureUnique;
    queue = MinMaxPriorityQueue.create();
    dupeSet = new HashSet<>();
    checkConsistency();
  }

  public Stream<Genotype> stream() {
    return queue.stream();
  }

  public boolean add(Genotype child) {
    boolean b;
    if (!ensureUnique || !dupeSet.contains(child.getBitsString())) {
      queue.add(child);
      dupeSet.add(child.getBitsString());
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
    nextGeneration.stream().filter(v -> !ensureUnique || !dupeSet.contains(v.getBitsString())).forEach(v -> {
      queue.add(v);
      dupeSet.add(v.getBitsString());
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
    dupeSet.remove(queue.removeLast().getBitsString());
    checkConsistency();
  }

  public boolean removeIf(Predicate<Genotype> filter) {
    queue.stream().filter(filter).forEach(v -> dupeSet.remove(v.getBitsString()));
    boolean b = queue.removeIf(filter);
    checkConsistency();
    return b;
  }

  public Genotype peekLast() {
    return queue.peekLast();
  }

  public Genotype peekFirst() {
    return queue.peekFirst();
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
