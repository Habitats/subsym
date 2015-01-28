package subsym.gui;

import javax.swing.*;

/**
 * Created by Patrick on 27.08.2014.
 */
public class AIComboBox<E> extends JComboBox<E> {

  public AIComboBox() {
    super();
    setForeground(Theme.getForeground());
    setBackground(Theme.getBackgroundInteractive());
  }
}
