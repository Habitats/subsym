package subsym.broids;

/**
 * Created by anon on 28.01.2015.
 */
public class Vec {

  public double x;
  public double y;

  private Vec(double x, double y) {
    this.x = x;
    this.y = y;
  }

  public Vec add(Vec o) {
    x += o.x;
    y += o.y;
    return this;
  }

  public Vec divide(double size) {
    x /= size;
    y /= size;
    return this;
  }

  @Override
  public String toString() {
    return "(" + x + ", " + y + ")";
  }

  public Vec multiply(Vec o) {
    x *= o.x;
    y *= o.y;
    return this;
  }

  public Vec multiply(double o) {
    x *= o;
    y *= o;
    return this;
  }

  public Vec subtract(Vec o) {
    x -= o.x;
    y -= o.y;
    return this;
  }

  public double lenght() {
    return Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
  }

  public static Vec create() {
    return new Vec(0, 0);
  }

  public static Vec create(double x, double y) {
    return new Vec(x, y);
  }
}
