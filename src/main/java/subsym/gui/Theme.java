package subsym.gui;

import java.awt.*;

/**
 * Created by Patrick on 27.08.2014.
 */
public class Theme {

  public static final Color BACKGROUND = new Color(208, 216, 231);
  public static final Color BACKGROUND_DARK = new Color(67, 70, 77);
  public static final Color BACKGROUND_INTERACTIVE = new Color(253, 253, 254);

  public static final Color TEXT = new Color(36, 36, 36);

  public static final Color ITEM_HOVER = new Color(118, 143, 206);
  public static final Color ITEM_BACKGROUND = new Color(72, 87, 125);
  public static final Color TEXT_ITEM = new Color(245, 242, 231);

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
