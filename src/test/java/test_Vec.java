import org.junit.Test;


import subsym.MODELS.Vec;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


/**
 * Created by Patrick on 13.02.2015.
 */
public class test_Vec {

  @Test
  public void test_equals() {
    Vec v = Vec.create(1, 2);
    Vec u = Vec.create(1, 2);
    assertTrue(v.equals(u));
  }

  @Test
  public void test_static_create() {
    Vec v = Vec.create(1, 2);
    assertEquals(v.x, 1., 0);
    assertEquals(v.y, 2., 0);
  }

  @Test
  public void test_static_add() {
    Vec v = Vec.create(3, 1);
    Vec u = Vec.create(2, 1);
    assertTrue(Vec.add(v, u).equals(Vec.create(5, 2)));
  }

  @Test
  public void test_static_divide() {
    Vec v = Vec.create(9, 4);
    Vec u = Vec.create(3, 2);
    assertTrue(Vec.divide(v, u).equals(Vec.create(3, 2)));
  }

  @Test
  public void test_static_subtract() {
    Vec v = Vec.create(3, 1);
    Vec u = Vec.create(2, 1);
    assertTrue(Vec.subtract(v, u).equals(Vec.create(1, 0)));
  }

  @Test
  public void test_static_multiply() {
    Vec v = Vec.create(3, 2);
    Vec u = Vec.create(2, 8);
    assertTrue(Vec.multiply(v, u).equals(Vec.create(6, 16)));
  }
  @Test
  public void test_static_multiply_scalar() {
    Vec v = Vec.create(3, 2);
    assertTrue(Vec.multiply(v, 9).equals(Vec.create(27, 18)));
  }

  @Test
  public void test_length() {
    Vec v = Vec.create(3, 4);
    assertEquals(v.lenght(), 5., 0);
  }

  @Test
  public void test_static_length() {
    Vec v = Vec.create(3, 4);
    assertEquals(Vec.lenght(v), 5., 0);
  }

  @Test
  public void test_normalize() {
    Vec v = Vec.create(3, 4);
    v.normalize();
    assertEquals(v.lenght(), 1., 0);
  }

  @Test
  public void test_static_normalize() {
    Vec v = Vec.create(3, 4);
    assertEquals(Vec.normalize(v).lenght(), 1., 0);
  }

  @Test
  public void test_multiply() {
    Vec v = Vec.create(3, 2);
    Vec u = Vec.create(2, 8);
    assertTrue(v.multiply(u).equals(Vec.create(6, 16)));
  }
  @Test
  public void test_multiply_scalar() {
    Vec v = Vec.create(3, 2);
    assertTrue(v.multiply(9).equals(Vec.create(27, 18)));
  }

  @Test
  public void test_add() {
    Vec v = Vec.create(3, 2);
    Vec u = Vec.create(2, 8);
    assertTrue(v.add(u).equals(Vec.create(5, 10)));
  }

  @Test
  public void test_subtract() {
    Vec v = Vec.create(3, 2);
    Vec u = Vec.create(2, 8);
    assertTrue(v.subtract(u).equals(Vec.create(1, -6)));
  }

  @Test
  public void test_divide() {
    Vec v = Vec.create(9, 4);
    Vec u = Vec.create(3, 2);
    assertTrue(v.divide(u).equals(Vec.create(3, 2)));
  }
}
