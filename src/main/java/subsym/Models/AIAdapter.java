package subsym.Models;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Patrick on 08.09.2014.
 */
public class AIAdapter<T extends Entity> {

  private int width;
  private int height;
  private AIAdapterListener listener;
  private int minX;
  private int minY;

  private List<T> items;

  private Vec sepWeight = new Vec(0.000001, 0.000001);
  private Vec alignWeight = new Vec(0.001, 0.001);
  private Vec cohWeight = new Vec(0.0001, 0.0001);
  private int dismissLimit = 1000;
  private int radius = 1000;

  public AIAdapter() {
    items = Collections.synchronizedList(new ArrayList<>());
  }


  public void setListener(AIAdapterListener listener) {
    this.listener = listener;
  }

  public int getWidth() {
    return width;
  }

  public int getHeight() {
    return height;
  }

  public void notifyDataChanged() {
    if (listener != null) {
      listener.notifyDataChanged();
    }
  }

  public T getItem(int index) {
    return items.get(index);
  }

  public void setWidth(int width) {
    this.width = width;
  }

  public int getSize() {
    return items.size();
  }

  public Collection<T> getItems() {
    return items;
  }

  public void setHeight(int height) {
    this.height = height;
  }

  private void updateDirection(T boid) {
    List<T> neighbors = neighbors(boid, radius);

    Vec sep = getSeperation(boid, neighbors).multiply(sepWeight);
    Vec align = getAlignment(boid, neighbors).multiply(alignWeight);
    Vec coh = getCohesion(boid, neighbors).multiply(cohWeight);

    Vec newVelocity = boid.getVelocity() //
        .add(coh) //
        .add(align) //
        .add(sep) //
        ;
    boid.setVelocity(newVelocity);
    boid.limitVelocity();

    boid.update();
  }

  // a boid will steer towards the average position of other boids close to it
  private Vec getCohesion(T boid, List<T> neighbors) {
    Vec newPos = new Vec(0, 0);
    if (neighbors.size() > 0) {
      neighbors.stream().filter(neighbor -> neighbor != boid).map(broid -> broid.p).forEach(p -> newPos.add(p));
      newPos.divide(neighbors.size());

      newPos.subtract(boid.p);
    }
    return newPos;
  }

  // a boid will steer towards the average heading of other boids close to it
  private Vec getAlignment(T boid, List<T> neighbors) {
    Vec newVelocity = new Vec(0, 0);
    if (neighbors.size() > 0) {
      neighbors.stream().filter(neighbor -> boid != neighbor).map(neighbor -> neighbor.v)
          .forEach(neighBorVelocity -> newVelocity.add(neighBorVelocity));
      newVelocity.divide(neighbors.size());
    }
    return newVelocity;
  }

  // a boid will steer to avoid crashing with other boids close to it
  private Vec getSeperation(T boid, List<T> neighbors) {
    Vec newPos = new Vec(0, 0);
    if (neighbors.size() > 0) {
      neighbors.stream().filter(neighbor -> neighbor != boid && neighbor.distance(boid) < 400)
          .forEach(neighbor -> newPos.subtract(neighbor.p));
    }
    return newPos;

  }


  public void update() {
    items.stream().forEach(boid -> updateDirection(boid));
    items.removeIf(broid -> (broid.getX() > dismissLimit + getWidth() || broid.getX() < -dismissLimit
                             || broid.getY() > dismissLimit + getHeight() || broid.getY() < -dismissLimit));
    notifyDataChanged();
  }

  public List<T> neighbors(T broid, int r) {
    return items.stream().filter(neighbor -> {
      if (broid == neighbor) {
        return false;
      }
      return broid.distance(neighbor) < r;


    }).collect(Collectors.toList());
  }

  public void addAll(List<T> broids) {
    synchronized (items) {
      items.addAll(broids);
    }
  }

  public void add(T broid) {
    synchronized (items) {
      items.add(broid);
    }
  }


}
