package subsym.flatland.entity;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import subsym.Main;
import subsym.flatland.Flatland;
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
  private long numPoison;
  private long numFood;
  private final Board<TileEntity> board;
  private final boolean isDirectional;
  private final Flatland flatland;
  private final int startY;
  private final int startX;
  private Map<Vec, TileEntity> poison;
  private Map<TileEntity, Integer> food;
  private int poisonCount;
  private int foodCount;
  private Direction dir;
  private BitSet foodId;
  private BitSet robotId;

  private int travelDistance;
  private double lastStepReward;

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

  public Robot(int x, int y, Board<TileEntity> board, boolean isDirectional, Flatland flatland) {
    super(x, y, board);
    startX = x;
    startY = y;
    this.board = board;
    this.isDirectional = isDirectional;
    this.flatland = flatland;
    setDirection(Direction.UP);
  }

  public void init(Map<TileEntity, Integer> foods) {
    List<TileEntity> items = getBoard().getItems();
    numPoison = items.stream().filter(i -> i instanceof Poison).count();
    numFood = items.stream().filter(i -> i instanceof Food).count();
    poison = items.stream().filter(i -> i instanceof Poison).collect(Collectors.toMap(i -> i.getPosition(), i -> i));
    food = Collections.unmodifiableMap(foods);

    foodId = new BitSet();
    foodId.set(0, foods.size(), true);
    robotId = new BitSet();
    robotId.set(getLocation1D(), true);

    travelDistance = 0;
    lastStepReward = 0;
    foodCount = 0;
    poisonCount = 0;
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
    switch (getDir()) {
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

  public void move(Direction dir) {
    Vec oldPosition = getPosition().copy();

    robotId.set(getLocation1D(), false);
    TileEntity tile = new Empty((int) oldPosition.getX(), (int) oldPosition.getY(), getBoard());
    getBoard().set(tile);
    switch (dir) {
      case LEFT:
        moveLeft();
        break;
      case UP:
        moveForward();
        break;
      case RIGHT:
        moveRight();
        break;
      case DOWN:
        moveBack();
        break;
      default:
        throw new IllegalStateException("Invalid index!");
    }
    TileEntity oldTile = getBoard().get(getX(), getY());
    Vec newPosition = oldTile.getPosition();
    if (oldTile instanceof Poison) {
      poisonCount++;
      poison.remove(newPosition);
      flatland.onPoisonConsumed(oldTile);
    } else if (oldTile instanceof Food) {
      foodCount++;
      foodId.set(food.get(oldTile), false);
      flatland.onFoodConsumed(oldTile);
    } else {
      flatland.onNormalMove(oldTile);
    }
    robotId.set(getLocation1D(), true);
    travelDistance++;
//    Log.v(TAG, "Robot ate: " + oldTile.getClass().getSimpleName());
    getBoard().set(this);
//    Log.v(TAG, getBoard().getFormattedBoard());
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
    switch (getDir()) {
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
    switch (getDir()) {
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
    switch (getDir()) {
      case UP:
        setPositionWrapped(getX() + 1, getY());
        setDirection(Direction.RIGHT);
        break;
      case RIGHT:
        setPositionWrapped(getX(), getY() - 1);
        setDirection(Direction.DOWN);
        break;
      case DOWN:
        setPositionWrapped(getX() - 1, getY());
        setDirection(Direction.LEFT);
        break;
      case LEFT:
        setPositionWrapped(getX(), getY() + 1);
        setDirection(Direction.UP);
        break;
    }
  }

  private void moveLeft() {
//    Log.v(TAG, "Moving left ...");
    switch (getDir()) {
      case UP:
        setPositionWrapped(getX() - 1, getY());
        setDirection(Direction.LEFT);
        break;
      case RIGHT:
        setPositionWrapped(getX(), getY() + 1);
        setDirection(Direction.UP);
        break;
      case DOWN:
        setPositionWrapped(getX() + 1, getY());
        setDirection(Direction.RIGHT);
        break;
      case LEFT:
        setPositionWrapped(getX(), getY() - 1);
        setDirection(Direction.DOWN);
        break;
    }
  }

  @Override
  public void draw(Graphics g, int x, int y) {
    super.draw(g, x, y);
//    drawStringCenter(g, getDescription(), x, y, getItemWidth(), getItemHeight());
    if (isDirectional) {
      drawArrow(g, x, y, getDir());
    }
  }

  @Override
  public String getDescription() {
    return "Robot " + getDir().name().charAt(0);
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

  public Map<Vec, TileEntity> getPoison() {
    return poison;
  }

  public Map<TileEntity, Integer> getFood() {
    return food;
  }

  public double getLastStepReward() {
    return lastStepReward;
  }

  public int getTravelDistance() {
    return travelDistance;
  }

  public Direction getDir() {
    return dir;
  }

  public void setDirection(Direction dir) {
    this.dir = isDirectional ? dir : Direction.UP;
  }

  public boolean isDirectional() {
    return isDirectional;
  }

  public int getStartX() {
    return startX;
  }

  public int getStartY() {
    return startY;
  }

  public int getLocation1D() {
    return getY() * getBoard().getWidth() + getX();
  }

  public static Vec getLocationFromBits(BitSet location, int width, int height) {
    int x = location.nextSetBit(0) % width;
    int y = (location.nextSetBit(0) - x) / width;
    return Vec.create(x, y);
  }

  public BitSet getFoodId() {
    return foodId;
  }

  public BitSet getRobotId() {
    return robotId;
  }
}
