package subsym.models;

import java.util.Arrays;

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

  // instance methods

  public Vec multiply(Vec v) {
    x *= v.x;
    y *= v.y;
    return this;
  }

  public Vec subtract(Vec v) {
    x -= v.x;
    y -= v.y;
    return this;
  }

  public Vec divide(Vec v) {
    x /= v.x;
    y /= v.y;
    return this;
  }

  public Vec add(Vec v) {
    x += v.x;
    y += v.y;
    return this;
  }

  public double lenght() {
    return Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
  }

  public Vec normalize() {
    return divide(lenght());
  }

  public Vec divide(double size) {
    x /= size;
    y /= size;
    return this;
  }

  public Vec multiply(double o) {
    x *= o;
    y *= o;
    return this;
  }

  // static methods

  public static Vec add(Vec u, Vec z, Vec... vecs) {
    Vec sum = new Vec(u.x, u.y).add(z);
    Arrays.asList(vecs).stream().forEach(v -> sum.add(v));
    return sum;
  }

  public static Vec subtract(Vec u, Vec z, Vec... vecs) {
    Vec first = new Vec(u.x, u.y).subtract(z);
    Arrays.asList(vecs).stream().forEach(v -> first.subtract(v));
    return first;
  }

  public static Vec multiply(Vec u, Vec z, Vec... vecs) {
    Vec sum = new Vec(u.x, u.y).multiply(z);
    Arrays.asList(vecs).stream().forEach(v -> sum.multiply(v));
    return sum;
  }

  public static Vec divide(Vec u, Vec z, Vec... vecs) {
    Vec sum = new Vec(u.x, u.y).divide(z);
    Arrays.asList(vecs).stream().forEach(v -> sum.divide(v));
    return sum;
  }

  public static Vec divide(Vec v, double size) {
    return new Vec(v.x / size, v.y / size);
  }

  public static Vec multiply(Vec v, double o) {
    return new Vec(v.x * o, v.y * o);
  }

  public static double lenght(Vec v) {
    return Math.sqrt(Math.pow(v.x, 2) + Math.pow(v.y, 2));
  }

  public static Vec normalize(Vec v) {
    return divide(v, lenght(v));
  }

  @Override
  public String toString() {
    return "(" + x + ", " + y + ") - | " + lenght() + " |";
  }

  public static Vec create(double x, double y) {
    return new Vec(x, y);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof Vec) {
      Vec o = (Vec) obj;
      return o.x == x && o.y == y;
    }
    return false;
  }

  public Vec copy() {
    return Vec.create(x, y);
  }
}
