package subsym.gui;

import java.awt.*;

/**
 * Created by Patrick on 27.08.2014.
 */
public class Theme {

  public static final Color BACKGROUND = new Color(165, 225, 46);
  public static final Color BACKGROUND_DARK = new Color(67, 70, 77);
  public static final Color BACKGROUND_INTERACTIVE = new Color(255, 128, 176);

  public static final Color TEXT = new Color(36, 36, 36);

  public static final Color ITEM_HOVER = new Color(128, 236, 255);
  public static final Color ITEM_BACKGROUND = new Color(247, 38, 114);
  public static final Color TEXT_ITEM = new Color(253, 253, 254);

  public static Color getBackground() {
    return BACKGROUND;
  }

  public static Color getBackgroundInteractive() {
    return BACKGROUND_INTERACTIVE;
  }

  public static Color getForeground() {
    return TEXT;
  }

  // BUTTONS
  public static Color getButtonHover() {
    return ITEM_HOVER;
  }

  public static Color getButtonBackground() {
    return ITEM_BACKGROUND;
  }

  public static Color getButtonClicked() {
    return ITEM_BACKGROUND;
  }

  public static Color getButtonForeground() {
    return TEXT_ITEM;
  }

}
