package subsym.ailife.entity;

import java.awt.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import subsym.ann.ArtificialNeuralNetwork;
import subsym.gui.ColorUtils;
import subsym.gui.Direction;
import subsym.models.Board;
import subsym.models.Vec;
import subsym.models.entity.TileEntity;

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

  private Direction dir;

  public Robot(int x, int y, Board board) {
    super(x, y, board);
    dir = Direction.UP;
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
      case UP:
        neighbors = Arrays.asList(getWrapped(-1, 0), getWrapped(0, 1), getWrapped(1, 0));
        break;
      case RIGHT:
        neighbors = Arrays.asList(getWrapped(0, 1), getWrapped(1, 0), getWrapped(0, -1));
        break;
      case DOWN:
        neighbors = Arrays.asList(getWrapped(1, 0), getWrapped(0, -1), getWrapped(-1, 0));
        break;
      case LEFT:
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
        fitness += 1;
        break;
      case 2:
        moveRight();
        break;
      default:
        throw new IllegalStateException("Invalid index!");
    }
    TileEntity oldTile = getBoard().get(getX(), getY());
    fitness += oldTile instanceof Poison ? -40 : oldTile instanceof Food ? 20 : 0;
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
      case UP:
        setPositionWrapped(getX(), getY() + 1);
        break;
      case RIGHT:
        setPositionWrapped(getX() + 1, getY());
        break;
      case DOWN:
        setPositionWrapped(getX(), getY() - 1);
        break;
      case LEFT:
        setPositionWrapped(getX() - 1, getY());
        break;
    }
  }

  private void moveRight() {
//    Log.v(TAG, "Moving right ...");
    switch (dir) {
      case UP:
        setPositionWrapped(getX() + 1, getY());
        dir = Direction.RIGHT;
        break;
      case RIGHT:
        setPositionWrapped(getX(), getY() - 1);
        dir = Direction.DOWN;
        break;
      case DOWN:
        setPositionWrapped(getX() - 1, getY());
        dir = Direction.LEFT;
        break;
      case LEFT:
        setPositionWrapped(getX(), getY() + 1);
        dir = Direction.UP;
        break;
    }
  }

  private void moveLeft() {
//    Log.v(TAG, "Moving left ...");
    switch (dir) {
      case UP:
        setPositionWrapped(getX() - 1, getY());
        dir = Direction.LEFT;
        break;
      case RIGHT:
        setPositionWrapped(getX(), getY() + 1);
        dir = Direction.UP;
        break;
      case DOWN:
        setPositionWrapped(getX() + 1, getY());
        dir = Direction.RIGHT;
        break;
      case LEFT:
        setPositionWrapped(getX(), getY() - 1);
        dir = Direction.DOWN;
        break;
    }
  }

  @Override
  public void draw(Graphics g, int x, int y) {
    super.draw(g, x, y);
    drawStringCenter(g, getDescription(), x, y, getItemWidth(), getItemHeight());
    drawArrow(g, x, y, dir);
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
