package subsym.models;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Created by Patrick on 08.09.2014.
 */
public class AIAdapter<T> {

  private int width;
  private int height;
  private AIAdapterListener listener;

  private List<T> items;
  public static double scale = 1;

  public AIAdapter() {
    items = Collections.synchronizedList(new ArrayList<>());
  }


  public void setListener(AIAdapterListener listener) {
    this.listener = listener;
  }

  public int getWidth() {
    return (int) (width * scale);
  }

  public int getHeight() {
    return (int) (height * scale);
  }

  public void notifyDataChanged() {
    if (listener != null) {
      listener.notifyDataChanged();
    }
  }

  public T getItem(int index) {
    return items.get(index);
  }

  public void setWidth(int width) {
    this.width = width;
  }


  public int getSize() {
    return items.size();
  }

  public Collection<T> getItems() {
    return items;
  }

  public void setHeight(int height) {
    this.height = height;
  }


  public void addAll(List<T> entities) {
    synchronized (items) {
      items.addAll(entities);
    }
  }

  public void add(T entity) {
    synchronized (items) {
      items.add(entity);
    }
  }

  public void updateScale(double i) {
    scale = i;
  }
}
