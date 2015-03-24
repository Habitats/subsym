package subsym.ailife.entity;

import java.awt.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import subsym.ann.ArtificialNeuralNetwork;
import subsym.gui.ColorUtils;
import subsym.models.Board;
import subsym.models.TileEntity;
import subsym.models.Vec;

/**
 * Created by Patrick on 23.03.2015.
 */
public class Robot extends TileEntity {

  private static final String TAG = Robot.class.getSimpleName();
  private double fitness;

  public List<Double> getSensoryInput() {
    List<Double> sensoryInput = Stream.of(getFoodSensorInput(), getPoisonSensorInput()) //
        .flatMap(List::stream).mapToDouble(Double::valueOf).boxed().collect(Collectors.toList());
    return sensoryInput;
  }

  public List<Double> getRandomSensoryInput() {
    List<Double> randomInput = Collections.nCopies(6, 0.).stream().collect(Collectors.toList());
    randomInput.set(ArtificialNeuralNetwork.random().nextInt(3), 1.);
    randomInput.set(3 + ArtificialNeuralNetwork.random().nextInt(3), 1.);
    return randomInput;
  }

  private enum Direction {
    NORTH, EAST, SOUTH, WEST;
  }

  private Direction dir;

  public Robot(int x, int y, Board board) {
    super(x, y, board);
    dir = Direction.NORTH;
  }

  public List<Integer> getFoodSensorInput() {
    List<TileEntity> neighbors = getSensorNeighbors();
    return neighbors.stream().mapToInt(i -> i instanceof Food ? 1 : 0).boxed().collect(Collectors.toList());
  }

  public List<Integer> getPoisonSensorInput() {
    List<TileEntity> neighbors = getSensorNeighbors();
    return neighbors.stream().mapToInt(i -> i instanceof Poison ? 1 : 0).boxed().collect(Collectors.toList());
  }

  private List<TileEntity> getSensorNeighbors() {
    List<TileEntity> neighbors;
    switch (dir) {
      case NORTH:
        neighbors = Arrays.asList(getWrapped(-1, 0), getWrapped(0, 1), getWrapped(1, 0));
        break;
      case EAST:
        neighbors = Arrays.asList(getWrapped(0, 1), getWrapped(1, 0), getWrapped(0, -1));
        break;
      case SOUTH:
        neighbors = Arrays.asList(getWrapped(1, 0), getWrapped(0, -1), getWrapped(-1, 0));
        break;
      case WEST:
        neighbors = Arrays.asList(getWrapped(0, -1), getWrapped(1, 0), getWrapped(0, 1));
        break;
      default:
        throw new IllegalStateException("No direction set!");
    }
    return neighbors;
  }

  private TileEntity getWrapped(int dx, int dy) {
    return getBoard().get((getX() + dx + getBoard().getWidth()) % getBoard().getWidth(),
                          (getY() + dy + getBoard().getHeight()) % getBoard().getHeight());
  }

  public double fitness() {
    return fitness;
  }

  public void move(int index) {
    Vec oldPosition = getPosition().copy();

    TileEntity tile = new Empty((int) oldPosition.x, (int) oldPosition.y, getBoard());
    getBoard().set(tile);
    switch (index) {
      case 0:
        moveLeft();
        break;
      case 1:
        moveForward();
        break;
      case 2:
        moveRight();
        break;
      default:
        throw new IllegalStateException("Invalid index!");
    }
    TileEntity oldTile = getBoard().get(getX(), getY());
    fitness += oldTile instanceof Poison ? -20 : oldTile instanceof Food ? 10 : 0;
//    Log.v(TAG, "Robot ate: " + oldTile.getClass().getSimpleName());
    getBoard().set(this);
    getBoard().notifyDataChanged();
  }

  private void setPositionWrapped(int x, int y) {

    int newX = (x + getBoard().getWidth()) % getBoard().getWidth();
    int newY = (y + getBoard().getHeight()) % getBoard().getHeight();
    setPosition(newX, newY);
//    Log.v(TAG, "New position: " + newX + ", " + newY);
  }

  private void moveForward() {
//    Log.v(TAG, "Moving forward ...");
    switch (dir) {
      case NORTH:
        setPositionWrapped(getX(), getY() + 1);
        break;
      case EAST:
        setPositionWrapped(getX() + 1, getY());
        break;
      case SOUTH:
        setPositionWrapped(getX(), getY() - 1);
        break;
      case WEST:
        setPositionWrapped(getX() - 1, getY());
        break;
    }
  }

  private void moveRight() {
//    Log.v(TAG, "Moving right ...");
    switch (dir) {
      case NORTH:
        setPositionWrapped(getX() + 1, getY());
        dir = Direction.EAST;
        break;
      case EAST:
        setPositionWrapped(getX(), getY() - 1);
        dir = Direction.SOUTH;
        break;
      case SOUTH:
        setPositionWrapped(getX() - 1, getY());
        dir = Direction.WEST;
        break;
      case WEST:
        setPositionWrapped(getX(), getY() + 1);
        dir = Direction.NORTH;
        break;
    }
  }

  private void moveLeft() {
//    Log.v(TAG, "Moving left ...");
    switch (dir) {
      case NORTH:
        setPositionWrapped(getX() - 1, getY());
        dir = Direction.WEST;
        break;
      case EAST:
        setPositionWrapped(getX(), getY() + 1);
        dir = Direction.NORTH;
        break;
      case SOUTH:
        setPositionWrapped(getX() + 1, getY());
        dir = Direction.EAST;
        break;
      case WEST:
        setPositionWrapped(getX(), getY() - 1);
        dir = Direction.SOUTH;
        break;
    }
  }

  @Override
  public String getDescription() {
    return String.valueOf(fitness);
  }

  @Override
  public Color getColor() {
    return ColorUtils.c(4);
  }
}
