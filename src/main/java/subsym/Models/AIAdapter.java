package subsym.models;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import subsym.broids.entities.Entity;

/**
 * Created by Patrick on 08.09.2014.
 */
public class AIAdapter<T extends Entity> {

  private int width;
  private int height;
  private AIAdapterListener listener;

  private List<T> items;

  public AIAdapter() {
    items = Collections.synchronizedList(new ArrayList<>());
  }


  public void setListener(AIAdapterListener listener) {
    this.listener = listener;
  }

  public int getWidth() {
    return width;
  }

  public int getHeight() {
    return height;
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


  public void addAll(List<T> broids) {
    synchronized (items) {
      items.addAll(broids);
    }
  }

  public void add(T broid) {
    synchronized (items) {
      items.add(broid);
    }
  }
}
