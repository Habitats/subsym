package subsym.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Patrick on 24.08.2014.
 */
public class Board<T extends Entity> extends AIAdapter<T> {

  private List<List<T>> tiles;

  public Board(int width, int height) {
    clear(width, height);
  }

  private void clear(int width, int height) {
    setWidth(width);
    setHeight(height);
    tiles = new ArrayList<>();
    for (int x = 0; x < width; x++) {
      List<T> column = new ArrayList<>(height);
      for (int y = 0; y < height; y++) {
        column.add(y, null);
      }
      tiles.add(column);
    }
  }

  public T get(int x, int y) {
    return tiles.get(x).get(y);
  }

  public void set(T tile) {
    tiles.get(tile.getX()).set(tile.getY(), tile);
  }

  @Override
  public String toString() {
    return "Width: " + getWidth() + " Height: " + getHeight();
  }


  @Override
  public int getSize() {
    return getWidth() * getHeight();
  }

  @Override
  public List<T> getItems() {
    List<T> items = new ArrayList<>();
    tiles.forEach(items::addAll);
    return items;
  }


  private boolean positionExist(int x, int y) {
    try {
      T colorTile = tiles.get(x).get(y);
      return (colorTile != null);
    } catch (IndexOutOfBoundsException e) {
      return false;
    }
  }

  public void clear() {
    clear(getWidth(), getHeight());
  }

  public List<T> getManhattanNeighbors(T tile) {
    List<T> manhattanNeighbors = new ArrayList<>();
    for (int x = tile.getX() - 1; x <= tile.getX() + 1; x++) {
      for (int y = tile.getY() - 1; y <= tile.getY() + 1; y++) {
        // if the position is out of bounds, disregard
        if (!positionExist(x, y)) {
          continue;
        }
        // do not put self to its own children
        if (x == tile.getX() && y == tile.getY()) {
          continue;
        }
        // disallow diagonal neighbors
        if (tile.getX() != x && tile.getY() != y) {
          continue;
        }
        manhattanNeighbors.add(get(x, y));
      }
    }
    return manhattanNeighbors;
  }
}