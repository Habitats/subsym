package subsym.ailife;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import subsym.ailife.entity.Empty;
import subsym.ailife.entity.Food;
import subsym.ailife.entity.Poison;
import subsym.ailife.entity.Robot;
import subsym.models.Board;
import subsym.models.entity.TileEntity;

/**
 * Created by mail on 04.05.2015.
 */
public class AiLifeReinforcementSimulator implements AiLifeSimulator {

  private  Board<TileEntity> board;
  private int startX;
  private int startY;

  public AiLifeReinforcementSimulator() {
    board = fromFile("1-simple.txt");
//    board = fromFile("2-still-simple.txt");
//    board = fromFile("3-dont-be-greedy.txt");
//    board = fromFile("4-big-one.txt");
//    board = fromFile("5-even-bigger.txt");

    AiLifeGui.show(board, this, startX, startY);
  }

  public Board<TileEntity> fromFile(String fileName) {
    try {
      Path path = FileSystems.getDefault().getPath("q",fileName );
      List<String> content = Files.readAllLines(path);
      String[] specs = content.remove(0).split("\\s");
      int width = Integer.parseInt(specs[0]);
      int height = Integer.parseInt(specs[1]);
      startX = Integer.parseInt(specs[2]);
      startY = Integer.parseInt(specs[3]);
      int numFood = Integer.parseInt(specs[4]);
      Board<TileEntity> board = new Board<>(width, height);

      for (int y = 0; y < height; y++) {
        for (int x = 0; x < width; x++) {
          String s = content.get(y).split("\\s")[x];
          TileEntity tile;
          switch (s) {
            case "0":
              tile = new Empty(x, y, board);
              break;
            case "-1":
              tile = new Poison(x, y, board);
              break;
            case "-2":
              tile = new Robot(x, y, board);
              break;
            default:
              tile = new Food(x, y, board);
          }
          board.set(tile);
        }
      }

      return board;
    } catch (IOException e) {
      throw new IllegalStateException("Unable to read AiLife map from file!");
    }
  }

  @Override
  public void move(Robot robot) {
  }

  @Override
  public int getMaxSteps() {
    return Integer.MAX_VALUE;
  }
}
