package subsym.gui;

import javax.swing.*;

/**
 * Created by Patrick on 28.08.2014.
 */
public class AISlider extends JSlider {

  public AISlider() {
    super();
    setBorder(BorderFactory.createEmptyBorder(3, 3, 0, 0));
    setForeground(Theme.getForeground());
    setBackground(Theme.getBackground());
  }
}
