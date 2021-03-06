package subsym.gui;

import java.awt.*;

/**
 * Created by Patrick on 03.10.2014.
 */
public class ColorUtils {

  private static final String TAG = ColorUtils.class.getSimpleName();
  public static final int NUM_COLORS = 5;

  public static Color toHsv(double normalizedValue, double brightness) {
    // all values are in the range of [0, 1]
    float hue = (float) normalizedValue;
    float value = (float) (1f * brightness);
    float sat = 0.75f;
//    Log.v(TAG, "H: " + hue + ", S: " + sat + ", V: " + value);

    return Color.getHSBColor(hue, sat, value);
  }

  public static Color toHsv(double normalizedValue, double brightness, double saturation) {
    // all values are in the range of [0, 1]
    float hue = (float) normalizedValue;
    float value = (float) (1f * brightness);
    float sat = (float) saturation;
//    Log.v(TAG, "H: " + hue + ", S: " + sat + ", V: " + value);

    return Color.getHSBColor(hue, sat, value);
  }

  public static Color toHsv(int value, int numberOfColors, double brightness) {
    float percentage = ((float) value) / numberOfColors;
    return toHsv(percentage, brightness);
  }

  public static Color c(int i) {
    if (i > NUM_COLORS) {
      throw new IllegalArgumentException("Not that many colors!");
    }
    return toHsv(i, NUM_COLORS, 1);
  }
}
