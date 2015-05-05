package subsym.models;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import subsym.models.entity.TileEntity;

/**
 * Created by Patrick on 24.08.2014.
 */
public class Board<T extends TileEntity> extends AIAdapter<T> {

  private List<List<T>> tiles;
  private int itemHeight;
  private int itemWidth;

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
    return "Width: " + getWidth() + " Height: " + getHeight() + " Hash: " + hashCode();
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

  public boolean positionExist(int x, int y) {
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

  public int getItemHeight() {
    return itemHeight;
  }

  public int getItemWidth() {
    return itemWidth;
  }

  public void setItemHeight(int itemHeight) {
    this.itemHeight = itemHeight;
  }

  public void setItemWidth(int itemWidth) {
    this.itemWidth = itemWidth;
  }

  public List<T> getManhattanNeighborsWrapped(T tile) {
    List<T> manhattanNeighbors = new ArrayList<>();
    for (int x = tile.getX() - 1; x <= tile.getX() + 1; x++) {
      for (int y = tile.getY() - 1; y <= tile.getY() + 1; y++) {
        // do not put self to its own children
        if (x == tile.getX() && y == tile.getY()) {
          continue;
        }
        // disallow diagonal neighbors
        if (tile.getX() != x && tile.getY() != y) {
          continue;
        }
        manhattanNeighbors.add(get(x % getWidth(), y % getHeight()));
      }
    }
    return manhattanNeighbors;
  }

  public void setWrapped(T tile) {
    int x = (tile.getX() + getWidth()) % getWidth();
    int y = (tile.getY() + getHeight()) % getHeight();
    tiles.get(x).set(y, tile);
  }

  public T get(Vec oldPosition) {
    return get((int) oldPosition.x, (int) oldPosition.y);
  }

  public String getFormattedBoard() {
    String xRow = IntStream.range(0, getWidth())//
        .mapToObj(x -> String.format("%-10d", x)) //
        .collect(Collectors.joining("", "\n", "\n"));
    String board = xRow;
    for (int y = 0; y < getHeight(); y++) {
      for (int x = 0; x < getWidth(); x++) {
        board += String.format("%1$-10s", get(x, getHeight() - 1 - y).getDescription());
      }
      board += "\n";
    }
//    String board = "\n" + IntStream.range(0, getWidth()) //
//        .mapToObj(y -> IntStream.range(0, getHeight()) //
//            .mapToObj(x -> String.format("%1$-10s", get(x, getHeight() - 1 - y).getDescription()))
//            .collect(Collectors.joining(" | ", (getHeight() - 1 - y) + "[ ", " ]")))//
//        .collect(Collectors.joining("\n", "", "\n" + xRow));

    return board;
  }

  public String getId() {
    return getItems().stream() //
        .map(i -> new StringBuilder() //
            .append(i.getClass().getSimpleName().charAt(0)).append(i.getX()).append(":").append(i.getY()).append(" "))
        .collect(Collectors.joining());
  }

  @Override
  public int hashCode() {
    Function<T, String> toId = o -> (o.getX() + ":" + o.getY() + o.getClass().getSimpleName());
    return tiles.stream().flatMap(List::stream).map(toId).collect(Collectors.joining()).hashCode();
  }
}
