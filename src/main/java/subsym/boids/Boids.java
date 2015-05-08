package subsym.boids;

import subsym.Main;
import subsym.boids.entities.Broid;
import subsym.boids.entities.Obstacle;
import subsym.boids.entities.Predator;
import subsym.boids.gui.BoidGui;
import subsym.gui.ColorUtils;

/**
 * Created by anon on 28.01.2015.
 */
public class Boids implements Runnable {

  private static final String TAG = Boids.class.getSimpleName();
  private int height = (int) (5000 * 1.2);
  private int width = (int) (5000 * 1.5);
  private BoidAdapter adapter;

  public static long updateFrequency = 20;

  public void run() {
    System.out.println("hello worlds");
    BoidGui gui = new BoidGui();
    gui.addListener(this);

    adapter = new BoidAdapter();

    adapter.setHeight(height);
    adapter.setWidth(width);

    gui.setAdapter(adapter);
    adapter.notifyDataChanged();

//    adapter.add(new Obsticle(1400, 1200));
//    adapter.add(new Predator(1200, 1400));
    Broid specialBoid = new Broid(50, 50);
    specialBoid.setColor(ColorUtils.c(0));
    adapter.add(specialBoid);

    new Thread(() -> {
      while (true) {
        try {
          Thread.sleep(updateFrequency);
          while (adapter.notFull()) {
            Broid boid = new Broid((int) (Main.random().nextDouble() * width), (int) (Main.random().nextDouble() * height));
            adapter.add(boid);
          }

          synchronized (adapter) {
            adapter.getItems().stream().filter(b -> b != specialBoid).forEach(b -> b.setColor(b.getOriginalColor()));
            adapter.neighbors(specialBoid).stream().forEach(b -> b.setColor(ColorUtils.c(4)));

            adapter.update();
          }
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    }).start();
  }

  public void spawnPredator() {
    synchronized (adapter) {
      adapter.add(new Predator((int) (Main.random().nextDouble() * width), (int) (Main.random().nextDouble() * height)));
    }
  }

  public void spawnObsticle() {
    synchronized (adapter) {
      adapter.add(new Obstacle((int) (Main.random().nextDouble() * width), (int) (Main.random().nextDouble() * height)));
    }
  }

  public void clearAll() {
    synchronized (adapter) {
      adapter.getItems().removeIf(b -> !b.getColor().equals(ColorUtils.c(0)));
    }
  }
}
