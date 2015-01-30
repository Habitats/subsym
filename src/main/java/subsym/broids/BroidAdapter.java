package subsym.broids;

import java.util.List;
import java.util.stream.Collectors;

import subsym.Models.AIAdapter;
import subsym.broids.entities.Entity;

/**
 * Created by Patrick on 30.01.2015.
 */
public class BroidAdapter extends AIAdapter<Entity> {

  public static double sepWeight = 0.1;
  public static double alignWeight = 0.1;
  public static double cohWeight = 0.1;
  public static int dismissLimit = -3000;
  public static int radius = 1000;
  public static int maxSpeed = 50;
  public static int maxBroids = 50;
  public static int numPredators = 1;
  public static int numObsticles = 1;

  private void updateDirection(Entity boid) {
    List<Entity> neighbors = neighbors(boid, boid.getRadius());

    Vec sep = getSeperation(boid, neighbors).multiply(boid.getSepWeight());
    Vec align = getAlignment(boid, neighbors).multiply(boid.getAlignWeight());
    Vec coh = getCohesion(boid, neighbors).multiply(boid.getCohWeight());

    Vec newVelocity = boid.getVelocity().add(sep);

    if (neighbors.stream().noneMatch(n -> boid.isEvil(n))) {
      newVelocity.add(coh).add(align);
    }

    boid.update(newVelocity);
  }

  // a boid will steer towards the average position of other boids close to it
  private Vec getCohesion(Entity boid, List<Entity> neighbors) {
    Vec deltaVelocity = Vec.create();
    if (neighbors.size() > 1) {
      // find the center of mass
      neighbors.stream().filter(neighbor -> neighbor != boid).map(broid -> broid.p).forEach(p -> deltaVelocity.add(p));
      deltaVelocity.divide(neighbors.size() - 1);

      // move with a magnitude of 1% towards the center of mass
      deltaVelocity.subtract(boid.p).divide(100);
    }
    return deltaVelocity;
  }

  // a boid will steer towards the average heading of other boids close to it
  private Vec getAlignment(Entity boid, List<Entity> neighbors) {
    Vec deltaVelocity = Vec.create();
    if (neighbors.size() > 1) {
      neighbors.stream().filter(neighbor -> boid != neighbor).map(neighbor -> neighbor.v)
          .forEach(neighBorVelocity -> deltaVelocity.add(neighBorVelocity));

      // normalize
      deltaVelocity.divide(neighbors.size() - 1);

      deltaVelocity.subtract(boid.v).divide(8);
    }
    return deltaVelocity;
  }

  // a boid will steer to avoid crashing with other boids close to it
  private Vec getSeperation(Entity boid, List<Entity> neighbors) {
    Vec deltaVelocity = Vec.create();
    if (neighbors.size() > 0) {
      neighbors.stream().filter(neighbor -> neighbor != boid && neighbor.distance(boid) < boid.closeRadius())
          .forEach(neighbor -> {
            deltaVelocity.subtract(neighbor.p);
            deltaVelocity.add(boid.p);
          });
    }
    return deltaVelocity;
  }

  public void update() {
    getItems().stream().forEach(boid -> updateDirection(boid));
    getItems().removeIf(broid -> broid.isPurgable() && isOutOfBounds(broid) && getSize() > maxBroids);
    getItems().stream().filter(broid -> isOutOfBounds(broid)).forEach(broid -> broid.wrapAround(getWidth(), getHeight()));
    notifyDataChanged();
  }

  private boolean isOutOfBounds(Entity broid) {
    return (broid.getX() > dismissLimit + getWidth() || broid.getX() < -dismissLimit
            || broid.getY() > dismissLimit + getHeight() || broid.getY() < -dismissLimit);
  }

  public List<Entity> neighbors(Entity broid, int r) {
    return getItems().stream() //
        .filter(neighbor -> broid != neighbor && broid.distance(neighbor) < r) //
        .collect(Collectors.toList());
  }

  public boolean notFull() {
    return getSize() < maxBroids;
  }
}
