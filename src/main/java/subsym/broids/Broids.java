package subsym.broids;

import java.awt.*;

import subsym.Models.AIAdapter;
import subsym.broids.gui.BroidGui;
import subsym.gui.AIGui;

/**
 * Created by anon on 28.01.2015.
 */
public class Broids implements Runnable {

  private static final String TAG = Broids.class.getSimpleName();
  private int height = (int) (3000 * 1.2);
  private int width = (int) (3000 * 1.5);

  public void run() {
    System.out.println("hello worlds");
    AIGui<Broid> gui = new BroidGui();

    AIAdapter<Broid> adapter = new AIAdapter<>();

    adapter.setHeight(height);
    adapter.setWidth(width);

    gui.setAdapter(adapter);
    adapter.notifyDataChanged();

    new Thread(() -> {
      while (true) {
        try {
          Thread.sleep(10);
          while (adapter.notFull()) {
            Broid broid = new Broid((int) (Math.random() * width), (int) (Math.random() * height));
            adapter.add(broid);
          }

          adapter.getItems().stream().forEach(b -> b.setColor(Color.yellow));

          adapter.notifyDataChanged();
          adapter.update();
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    }).start();
  }
}
