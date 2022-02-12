package me.byteful.lib.mmgui.util;

import org.jetbrains.annotations.NotNull;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.ImageInputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GIFHelper {
  // https://stackoverflow.com/a/18425922
  @NotNull
  public static List<GIFImageFrame> readGIF(@NotNull Object input) {
    java.util.List<GIFImageFrame> frames = new ArrayList<>();

    int width = -1;
    int height = -1;

    final ImageReader reader = ImageIO.getImageReadersByFormatName("gif").next();

    try (ImageInputStream stream = ImageIO.createImageInputStream(input)) {
      reader.setInput(stream);

      if (!reader.getFormatName().equalsIgnoreCase("gif")) {
        throw new RuntimeException("Failed to load possible (likely not) GIF input: " + input);
      }

      IIOMetadata metadata = reader.getStreamMetadata();
      if (metadata != null) {
        IIOMetadataNode globalRoot =
            (IIOMetadataNode) metadata.getAsTree(metadata.getNativeMetadataFormatName());

        NodeList globalScreenDescriptor =
            globalRoot.getElementsByTagName("LogicalScreenDescriptor");

        if (globalScreenDescriptor.getLength() > 0) {
          IIOMetadataNode screenDescriptor = (IIOMetadataNode) globalScreenDescriptor.item(0);

          if (screenDescriptor != null) {
            width = Integer.parseInt(screenDescriptor.getAttribute("logicalScreenWidth"));
            height = Integer.parseInt(screenDescriptor.getAttribute("logicalScreenHeight"));
          }
        }
      }

      BufferedImage master = null;
      Graphics2D masterGraphics = null;

      for (int frameIndex = 0; ; frameIndex++) {
        BufferedImage image;
        try {
          image = reader.read(frameIndex);
        } catch (IndexOutOfBoundsException io) {
          break;
        }

        if (width == -1 || height == -1) {
          width = image.getWidth();
          height = image.getHeight();
        }

        IIOMetadataNode root =
            (IIOMetadataNode)
                reader.getImageMetadata(frameIndex).getAsTree("javax_imageio_gif_image_1.0");
        IIOMetadataNode gce =
            (IIOMetadataNode) root.getElementsByTagName("GraphicControlExtension").item(0);
        int delay = Integer.parseInt(gce.getAttribute("delayTime"));
        String disposal = gce.getAttribute("disposalMethod");

        int x = 0;
        int y = 0;

        if (master == null) {
          master = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
          masterGraphics = master.createGraphics();
          masterGraphics.setBackground(new Color(0, 0, 0, 0));
        } else {
          NodeList children = root.getChildNodes();
          for (int nodeIndex = 0; nodeIndex < children.getLength(); nodeIndex++) {
            Node nodeItem = children.item(nodeIndex);
            if (nodeItem.getNodeName().equals("ImageDescriptor")) {
              NamedNodeMap map = nodeItem.getAttributes();
              x = Integer.parseInt(map.getNamedItem("imageLeftPosition").getNodeValue());
              y = Integer.parseInt(map.getNamedItem("imageTopPosition").getNodeValue());
            }
          }
        }
        masterGraphics.drawImage(image, x, y, null);

        BufferedImage copy =
            new BufferedImage(
                master.getColorModel(), master.copyData(null), master.isAlphaPremultiplied(), null);
        frames.add(new GIFImageFrame(copy, delay, disposal));

        if (disposal.equals("restoreToPrevious")) {
          BufferedImage from = null;
          for (int i = frameIndex - 1; i >= 0; i--) {
            if (!frames.get(i).getDisposal().equals("restoreToPrevious") || frameIndex == 0) {
              from = frames.get(i).getImage();
              break;
            }
          }

          if (from == null) {
            throw new NullPointerException("Failure to load previous image for disposal process.");
          }

          master =
              new BufferedImage(
                  from.getColorModel() != null ? from.getColorModel() : ColorModel.getRGBdefault(),
                  from.copyData(null),
                  from.isAlphaPremultiplied(),
                  null);
          masterGraphics = master.createGraphics();
          masterGraphics.setBackground(new Color(0, 0, 0, 0));
        } else if (disposal.equals("restoreToBackgroundColor")) {
          masterGraphics.clearRect(x, y, image.getWidth(), image.getHeight());
        }
      }
      reader.dispose();
    } catch (IOException e) {
      e.printStackTrace();
    }

    return frames;
  }

  public static final class GIFImageFrame {
    private final int delay;
    private final BufferedImage image;
    private final String disposal;

    public GIFImageFrame(BufferedImage image, int delay, String disposal) {
      this.image = image;
      this.delay = delay;
      this.disposal = disposal;
    }

    public BufferedImage getImage() {
      return image;
    }

    public int getDelay() {
      return delay;
    }

    public String getDisposal() {
      return disposal;
    }
  }
}
