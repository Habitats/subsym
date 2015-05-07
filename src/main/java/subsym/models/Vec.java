package subsym.models;

import java.util.Arrays;

/**
 * Created by anon on 28.01.2015.
 */
public class Vec {

  private double x;
  private double y;
  private String id;

  private Vec(double x, double y) {
    this.setX(x);
    this.setY(y);
  }

  // instance methods

  public Vec multiply(Vec v) {
    setX(getX() * v.getX());
    setY(getY() * v.getY());
    return this;
  }

  public Vec subtract(Vec v) {
    setX(getX() - v.getX());
    setY(getY() - v.getY());
    return this;
  }

  public Vec divide(Vec v) {
    setX(getX() / v.getX());
    setY(getY() / v.getY());
    return this;
  }

  public Vec add(Vec v) {
    setX(getX() + v.getX());
    setY(getY() + v.getY());
    return this;
  }

  public double lenght() {
    return Math.sqrt(Math.pow(getX(), 2) + Math.pow(getY(), 2));
  }

  public Vec normalize() {
    return divide(lenght());
  }

  public Vec divide(double size) {
    setX(getX() / size);
    setY(getY() / size);
    return this;
  }

  public Vec multiply(double o) {
    setX(getX() * o);
    setY(getY() * o);
    return this;
  }

  // static methods

  public static Vec add(Vec u, Vec z, Vec... vecs) {
    Vec sum = new Vec(u.getX(), u.getY()).add(z);
    Arrays.asList(vecs).stream().forEach(v -> sum.add(v));
    return sum;
  }

  public static Vec subtract(Vec u, Vec z, Vec... vecs) {
    Vec first = new Vec(u.getX(), u.getY()).subtract(z);
    Arrays.asList(vecs).stream().forEach(v -> first.subtract(v));
    return first;
  }

  public static Vec multiply(Vec u, Vec z, Vec... vecs) {
    Vec sum = new Vec(u.getX(), u.getY()).multiply(z);
    Arrays.asList(vecs).stream().forEach(v -> sum.multiply(v));
    return sum;
  }

  public static Vec divide(Vec u, Vec z, Vec... vecs) {
    Vec sum = new Vec(u.getX(), u.getY()).divide(z);
    Arrays.asList(vecs).stream().forEach(v -> sum.divide(v));
    return sum;
  }

  public static Vec divide(Vec v, double size) {
    return new Vec(v.getX() / size, v.getY() / size);
  }

  public static Vec multiply(Vec v, double o) {
    return new Vec(v.getX() * o, v.getY() * o);
  }

  public static double lenght(Vec v) {
    return Math.sqrt(Math.pow(v.getX(), 2) + Math.pow(v.getY(), 2));
  }

  public static Vec normalize(Vec v) {
    return divide(v, lenght(v));
  }

  @Override
  public String toString() {
    return "(" + getX() + ", " + getY() + ") - | " + lenght() + " |";
  }

  public static Vec create(double x, double y) {
    return new Vec(x, y);
  }

  @Override
  public int hashCode() {
    return getId().hashCode();
  }

  public String getId() {
    return x + ":" + y;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof Vec) {
      Vec o = (Vec) obj;
      return o.getX() == getX() && o.getY() == getY();
    }
    return false;
  }

  public Vec copy() {
    return Vec.create(getX(), getY());
  }

  public double getX() {
    return x;
  }

  public void setX(double x) {
    this.x = x;
  }

  public double getY() {
    return y;
  }

  public void setY(double y) {
    this.y = y;
  }
}
