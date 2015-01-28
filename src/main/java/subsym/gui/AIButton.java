package subsym.gui;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.*;

/**
 * Created by Patrick on 27.08.2014.
 */
public class AIButton extends JButton {

  public AIButton(String name) {
    this();
    setName(name);
    setText(name);
  }

  public AIButton() {
    super();
    setPreferredSize(new Dimension(100, 25));
    setBorder(BorderFactory.createEmptyBorder());
    setBackground(Theme.getButtonBackground());
    setForeground(Theme.getButtonForeground());
    addMouseListener(new MouseListener() {

      @Override
      public void mouseClicked(MouseEvent e) {
        setBackground(Theme.getButtonClicked());
      }

      @Override
      public void mousePressed(MouseEvent e) {
        setBackground(Theme.getButtonClicked());
      }

      @Override
      public void mouseReleased(MouseEvent e) {
        setBackground(Theme.getButtonHover());

      }

      @Override
      public void mouseEntered(MouseEvent e) {
        setBackground(Theme.getButtonHover());
      }

      @Override
      public void mouseExited(MouseEvent e) {
        setBackground(Theme.getButtonBackground());
      }
    });
  }
}
