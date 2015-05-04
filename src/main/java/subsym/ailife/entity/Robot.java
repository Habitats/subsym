package subsym.ailife.entity;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import subsym.Main;
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
  private final long numPoison;
  private final long numFood;
  private int poisonCount;
  private int foodCount;
  private double score;

  public List<Double> getSensoryInput() {
    List<Double> sensoryInput = new ArrayList<>();
    sensoryInput.addAll(getFoodSensorInput());
    sensoryInput.addAll(getPoisonSensorInput());
    return sensoryInput;
  }

  public List<Double> getRandomSensoryInput() {
    List<Double> randomInput = Collections.nCopies(6, 0.).stream().collect(Collectors.toList());
    randomInput.set(Main.random().nextInt(3), 1.);
    randomInput.set(3 + Main.random().nextInt(3), 1.);
    return randomInput;
  }

  private Direction dir;

  public Robot(int x, int y, Board board) {
    super(x, y, board);
    dir = Direction.UP;
   numPoison = board.getItems().stream().filter(i -> i instanceof Poison).count();
   numFood = board.getItems().stream().filter(i -> i instanceof Food).count();
  }

  public List<Double> getFoodSensorInput() {
    List<TileEntity> neighbors = getSensorNeighbors();
    return neighbors.stream().mapToDouble(i -> i instanceof Food ? 1. : 0.).boxed().collect(Collectors.toList());
  }

  public List<Double> getPoisonSensorInput() {
    List<TileEntity> neighbors = getSensorNeighbors();
    return neighbors.stream().mapToDouble(i -> i instanceof Poison ? 1. : 0.).boxed().collect(Collectors.toList());
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
        neighbors = Arrays.asList(getWrapped(0, -1), getWrapped(-1, 0), getWrapped(0, 1));
        break;
      default:
        throw new IllegalStateException("No direction set!");
    }
    return neighbors;
  }

  private TileEntity getWrapped(int dx, int dy) {
    int x = (getX() + dx + getBoard().getWidth()) % getBoard().getWidth();
    int y = (getY() + dy + getBoard().getHeight()) % getBoard().getHeight();
    return getBoard().get(x, y);
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
      case 3:
        moveBack();
        break;
      default:
        throw new IllegalStateException("Invalid index!");
    }
    TileEntity oldTile = getBoard().get(getX(), getY());
    if (oldTile instanceof Poison) {
      poisonCount++;
    } else if (oldTile instanceof Food) {
      foodCount++;
    }
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

  private void moveBack() {
//    Log.v(TAG, "Moving forward ...");
    switch (dir) {
      case UP:
        setPositionWrapped(getX(), getY() - 1);
        break;
      case RIGHT:
        setPositionWrapped(getX() - 1, getY());
        break;
      case DOWN:
        setPositionWrapped(getX(), getY() + 1);
        break;
      case LEFT:
        setPositionWrapped(getX() + 1, getY());
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
//    drawStringCenter(g, getDescription(), x, y, getItemWidth(), getItemHeight());
    drawArrow(g, x, y, dir);
  }

  @Override
  public String getDescription() {
    return "Robot " + dir.name().charAt(0);
  }

  public int getFoodCount() {
    return foodCount;
  }

  public int getPoisonCount() {
    return poisonCount;
  }

  @Override
  public Color getColor() {
    return ColorUtils.c(4);
  }

  public double getScore() {
    long deltaPoison = numPoison - getBoard().getItems().stream().filter(i -> i instanceof Poison).count();
    long deltaFood = numFood - getBoard().getItems().stream().filter(i -> i instanceof Food).count();
//      Log.v(TAG, "" + deltaFood + deltaPoison);
    double delta = deltaFood * 2 + deltaPoison * -3;
    double max = numFood * 2;
    return delta / max;
  }

  public void setScore(double score) {
    this.score = score;
  }
}
