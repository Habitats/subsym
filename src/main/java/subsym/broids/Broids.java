package subsym.broids;

import subsym.broids.entities.Broid;
import subsym.broids.entities.Obsticle;
import subsym.broids.entities.Predator;
import subsym.broids.gui.BroidGui;
import subsym.gui.ColorUtils;

/**
 * Created by anon on 28.01.2015.
 */
public class Broids implements Runnable {

  private static final String TAG = Broids.class.getSimpleName();
  private int height = (int) (5000 * 1.2);
  private int width = (int) (5000 * 1.5);
  private BroidAdapter adapter;

  public static long updateFrequency = 20;

  public void run() {
    System.out.println("hello worlds");
    BroidGui gui = new BroidGui();
    gui.addListener(this);

    adapter = new BroidAdapter();

    adapter.setHeight(height);
    adapter.setWidth(width);

    gui.setAdapter(adapter);
    adapter.notifyDataChanged();

//    adapter.add(new Obsticle(1400, 1200));
//    adapter.add(new Predator(1200, 1400));
    Broid specialBroid = new Broid(50, 50);
    specialBroid.setColor(ColorUtils.c(0));
    adapter.add(specialBroid);

    new Thread(() -> {
      while (true) {
        try {
          Thread.sleep(updateFrequency);
          while (adapter.notFull()) {
            Broid broid = new Broid((int) (Math.random() * width), (int) (Math.random() * height));
            adapter.add(broid);
          }

          synchronized (adapter) {
            adapter.getItems().stream().filter(b -> b != specialBroid).forEach(b -> b.setColor(b.getOriginalColor()));
            adapter.neighbors(specialBroid).stream().forEach(b -> b.setColor(ColorUtils.c(4)));

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
      adapter.add(new Predator((int) (Math.random() * width), (int) (Math.random() * height)));
    }
  }

  public void spawnObsticle() {
    synchronized (adapter) {
      adapter.add(new Obsticle((int) (Math.random() * width), (int) (Math.random() * height)));
    }
  }

  public void clearAll() {
    synchronized (adapter) {
      adapter.getItems().removeIf(b -> !b.getColor().equals(ColorUtils.c(0)));
    }
  }
}
