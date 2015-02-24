package subsym.boids;

import java.util.List;
import java.util.stream.Collectors;

import subsym.boids.entities.Entity;
import subsym.boids.entities.Predator;
import subsym.gui.ColorUtils;
import subsym.yoloswag.AIAdapter;
import subsym.yoloswag.Vec;

/**
 * Created by Patrick on 30.01.2015.
 */
public class BoidAdapter extends AIAdapter<Entity> {

  private static final String TAG = BoidAdapter.class.getSimpleName();
  public static double SCALE = 2;
  private static double multiplier = 100;

  public static double sepWeight = 10 / multiplier;
  public static double alignWeight = 100 / multiplier;
  public static double cohWeight = 10 / multiplier;
  public static double obsticleSepWeight = 1 / (multiplier / 10);

  public static int dismissLimit = 0;
  public static int radius = 1000;
  public static int maxSpeed = 30;
  public static int maxBroids = 500;
  public static int numPredators = 1;
  public static int numObsticles = 1;

  public static boolean WRAP_AROUND_PHYSICS_ENABLED = false;
  public static boolean VECTORS_ENABLED = false;

  public static int getSepWeight() {
    return (int) (sepWeight * multiplier);
  }

  public static void setSepWeight(double sepWeight) {
    BoidAdapter.sepWeight = sepWeight / multiplier;
  }

  public static void setObsticleSepWeight(double obsticleSepWeight) {
    BoidAdapter.obsticleSepWeight = obsticleSepWeight / (multiplier / 10);
  }

  public static int getAlignWeight() {
    return (int) (alignWeight * multiplier);
  }

  public static void setAlignWeight(double alignWeight) {
    BoidAdapter.alignWeight = alignWeight / multiplier;
  }

  public static int getCohWeight() {
    return (int) (cohWeight * multiplier);
  }

  public static void setCohWeight(double cohWeight) {
    BoidAdapter.cohWeight = cohWeight / multiplier;
  }

  public static int getObsticleSepWeight() {
    return (int) (obsticleSepWeight * (multiplier / 10));
  }

  private void updateDirection(Entity boid) {
    List<Entity> neighbors = neighbors(boid);

//    Vec sep = Vec.multiply(getSeperation(boid, neighbors), boid.getSepWeight());
    Vec sep = getSeperation(boid, neighbors);
    Vec align = Vec.multiply(getAlignment(boid, neighbors), boid.getAlignWeight());
    Vec coh = Vec.multiply(getCohesion(boid, neighbors), boid.getCohWeight());

    double alignLength = align.lenght();
    double cohLength = coh.lenght();
    double sepLength = sep.lenght();

    Vec newVelocity = Vec.add(boid.getVelocity(), sep);

    if (neighbors.stream().noneMatch(n -> boid.isEvil(n))) {
      newVelocity.add(coh).add(align);
    }

    boid.update(newVelocity);

    if (boid.getColor().equals(ColorUtils.c(0))) {
//      Log.v(TAG, String
//          .format("Align: %9.3f - Seperation: %9.3f - Coherence: %9.3f - V: %s", alignLength, sepLength, cohLength,
//                  boid.getVelocity()));
    }
  }

  // a boid will steer towards the average position of other boids close to it
  private Vec getCohesion(Entity boid, List<Entity> neighbors) {
    Vec deltaVelocity = Vec.create(0, 0);
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
    Vec deltaVelocity = Vec.create(0, 0);
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
    Vec deltaVelocity = Vec.create(0, 0);
    if (neighbors.size() > 0) {
      neighbors.stream().filter(neighbor -> neighbor != boid && distance(boid, neighbor) < neighbor.closeRadius())
          .forEach(neighbor -> {
            deltaVelocity.subtract(neighbor.p);
            deltaVelocity.add(boid.p);
            deltaVelocity.multiply(neighbor.getSepWeight());
          });
    }
    return deltaVelocity;
  }

  public double distance(Entity neighbor, Entity boid) {
    int dx = neighbor.getX() - boid.getX();
    int dy = neighbor.getY() - boid.getY();
    double sqrt = pythagoras(dx, dy);
    return sqrt;
  }

  public double distanceWithXWrap(Entity neighbor, Entity boid) {
    int dy = neighbor.getY() - boid.getY();
    int dx = delta(neighbor.getX(), boid.getX(), getWidth());
    return pythagoras(dx, dy);
  }

  public double distanceWithYWrap(Entity neighbor, Entity boid) {
    int dy = delta(neighbor.getY(), boid.getY(), getHeight());
    int dx = neighbor.getX() - boid.getX();
    return pythagoras(dx, dy);
  }

  public double distanceWithBothWrap(Entity neighbor, Entity boid) {
    int dy = delta(neighbor.getY(), boid.getY(), getHeight());
    int dx = delta(neighbor.getX(), boid.getX(), getWidth());
    return pythagoras(dx, dy);
  }

  private int delta(int z1, int z2, int pad) {
    return Math.min(z1, z2) + pad - (Math.max(z1, z2));
  }

  private double pythagoras(int dx, int dy) {
    return Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2));
  }


  public void update() {
    getItems().stream().forEach(boid -> updateDirection(boid));
    getItems().removeIf(
        broid -> broid.isPurgable() && isOutOfBounds(broid) && getSize() > maxBroids && !broid.getColor()
            .equals(ColorUtils.c(0)));
    getItems().stream().filter(broid -> isOutOfBounds(broid))
        .forEach(broid -> broid.wrapAround(getWidth(), getHeight()));
    notifyDataChanged();
  }

  private boolean isOutOfBounds(Entity broid) {
    return (broid.getX() > dismissLimit + getWidth() || broid.getX() < -dismissLimit
            || broid.getY() > dismissLimit + getHeight() || broid.getY() < -dismissLimit);
  }

  public List<Entity> neighbors(Entity broid) {
    return getItems().stream() //
        .filter(neighbor -> broid != neighbor && (!(broid instanceof Predator) || !broid.isEvil(neighbor)) && //
                            (distance(neighbor, broid) < broid.getRadius() || (WRAP_AROUND_PHYSICS_ENABLED && (
                                distanceWithXWrap(neighbor, broid) < broid.getRadius() ||//
                                distanceWithYWrap(neighbor, broid) < broid.getRadius() ||//
                                distanceWithBothWrap(neighbor, broid) < broid.getRadius())) //
                            ))//
        .collect(Collectors.toList());
  }

  public boolean notFull() {
    return getSize() < maxBroids;
  }

}
