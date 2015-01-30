package subsym.broids;

import subsym.Models.AIAdapter;
import subsym.Models.Entity;
import subsym.broids.gui.BroidGui;
import subsym.broids.gui.Predator;

/**
 * Created by anon on 28.01.2015.
 */
public class Broids implements Runnable {

  private static final String TAG = Broids.class.getSimpleName();
  private int height = (int) (3000 * 1.2);
  private int width = (int) (3000 * 1.5);
  private AIAdapter<Entity> adapter;

  public void run() {
    System.out.println("hello worlds");
    BroidGui gui = new BroidGui();
    gui.addListener(this);

    adapter = new AIAdapter<>();

    adapter.setHeight(height);
    adapter.setWidth(width);

    gui.setAdapter(adapter);
    adapter.notifyDataChanged();

    adapter.add(new Obsticle(1400, 1200));
    adapter.add(new Predator(1200, 1400));

    new Thread(() -> {
      while (true) {
        try {
          Thread.sleep(10);
          while (adapter.notFull()) {
            Broid broid = new Broid((int) (Math.random() * width), (int) (Math.random() * height));
            adapter.add(broid);
          }

          adapter.notifyDataChanged();
          synchronized (adapter) {
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
      adapter.getItems().clear();
    }
  }
}
