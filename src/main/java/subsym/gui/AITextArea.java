package subsym.gui;

import java.awt.*;

import javax.swing.*;

/**
 * Created by Patrick on 27.08.2014.
 */
public class AITextArea extends JTextArea {

  public AITextArea() {
    setBackground(Theme.getBackgroundInteractive());
    setForeground(Theme.getForeground());
    setPreferredSize(new Dimension(100, 0));
    setLineWrap(true);
  }
}
