package subsym.gui;

import javax.swing.*;

/**
 * Created by Patrick on 24.09.2014.
 */
public class AILabel extends JLabel {

  public AILabel() {
    super();
    setForeground(Theme.getForeground());
    setBackground(Theme.getBackground());
    setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
  }

}
