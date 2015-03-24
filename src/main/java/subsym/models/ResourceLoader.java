package subsym.models;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

/**
 * Created by anon on 24.03.2015.
 */
public class ResourceLoader {

  private static ResourceLoader instance;
  private Map<String, Image> resource;

  private ResourceLoader() {
    resource = new HashMap<>();
  }

  public static ResourceLoader getInstance() {
    if (instance == null) {
      instance = new ResourceLoader();
    }
    return instance;
  }

  public Image getResource(String path) {
    if (!resource.containsKey(path)) {
      resource.put(path, loadResource(path));
    }
    return resource.get(path);
  }

  public Image loadResource(String path) {
    try {
      GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
      GraphicsDevice device = env.getDefaultScreenDevice();
      GraphicsConfiguration config = device.getDefaultConfiguration();
      BufferedImage buffy = config.createCompatibleImage(50, 50, Transparency.TRANSLUCENT);
      BufferedImage img = ImageIO.read(new File(path));
      Graphics2D g = buffy.createGraphics();
      g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
      g.drawImage(img, 0, 0, 50, 50, 0, 0, img.getWidth(), img.getHeight(), null);
      g.dispose();
      return buffy;
    } catch (IOException e) {
      throw new IllegalStateException("Unable to load resource: " + path);
    }
  }
}
