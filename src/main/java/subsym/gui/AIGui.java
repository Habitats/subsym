package subsym.gui;

import java.awt.*;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.swing.*;

import subsym.Log;
import subsym.models.AIAdapter;
import subsym.models.entity.Entity;


/**
 * Created by Patrick on 08.09.2014.
 */
public abstract class AIGui<T extends Entity> extends JFrame {

  private static final String TAG = AIGui.class.getSimpleName();

  protected void buildFrame(JPanel mainPanel, AIContiniousScrollPane log, AITextField statusField) {
    mainPanel.setPreferredSize(getPreferredSize());
    Log.setLogField(log);
    Log.setStatusField(statusField);
    add(mainPanel);
    setDefaultCloseOperation(getDefaultCloseOperation());
    pack();
    setLocationRelativeTo(getRootPane());
    setVisible(true);
  }

  @Override
  public void paint(Graphics g) {
    super.paint(g);
    requestFocus();
  }

  public void setAdapter(AIAdapter<T> adapter) {
//    setAdapter.setOrigin(minX, minY);
    getDrawingCanvas().setAdapter(adapter);
    Log.v(TAG, "setting setAdapter!" + adapter);
    getMainPanel().repaint();
  }

  public String getInput() {
    return getInputField().getText().trim();
  }

  public abstract int getDefaultCloseOperation();

  public abstract Dimension getPreferredSize();

  protected static String readFile(String path, Charset encoding) {
    byte[] encoded = new byte[0];
    try {
      encoded = Files.readAllBytes(Paths.get(path));
    } catch (IOException e) {
      e.printStackTrace();
    }
    return new String(encoded, encoding);
  }

  protected void init() {
  }

  public abstract JPanel getMainPanel();

  public abstract AICanvas getDrawingCanvas();

  public abstract AITextArea getInputField();

  public void updateScale(double i) {
    getDrawingCanvas().getAdapter().updateScale(i);
//    getDrawingCanvas().updateScale(i);
  }
}
