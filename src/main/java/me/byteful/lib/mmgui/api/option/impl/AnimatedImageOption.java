package me.byteful.lib.mmgui.api.option.impl;

import me.byteful.lib.mmgui.MiniMapGUI;
import me.byteful.lib.mmgui.api.option.Option;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapPalette;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public class AnimatedImageOption implements Option {
  @NotNull private final MapView view;
  @NotNull private final List<MinecraftGIFImage> images;
  @NotNull private final Consumer<MiniMapGUI> onSelect;
  private final boolean loop;

  public AnimatedImageOption(
      @NotNull World world,
      @NotNull URL imageUrl,
      boolean loop,
      @NotNull Consumer<MiniMapGUI> onSelect) {
    this.view = Bukkit.createMap(world);
    this.loop = loop;
    this.onSelect = onSelect;

    try {
      this.images = loadGIF(imageUrl.openStream());
    } catch (IOException e) {
      throw new RuntimeException("Failed to load image from URL: " + imageUrl, e);
    }
  }

  public AnimatedImageOption(
      @NotNull World world,
      @NotNull File imageFile,
      boolean loop,
      @NotNull Consumer<MiniMapGUI> onSelect) {
    this.view = Bukkit.createMap(world);
    this.onSelect = onSelect;
    this.loop = loop;
    this.images = loadGIF(imageFile);
  }

  public AnimatedImageOption(
      @NotNull World world,
      @NotNull InputStream imageStream,
      boolean loop,
      @NotNull Consumer<MiniMapGUI> onSelect) {
    this.view = Bukkit.createMap(world);
    this.onSelect = onSelect;
    this.loop = loop;
    this.images = loadGIF(imageStream);
  }

  public AnimatedImageOption(
      @NotNull MapView view,
      @NotNull URL imageUrl,
      boolean loop,
      @NotNull Consumer<MiniMapGUI> onSelect) {
    this.view = view;
    this.onSelect = onSelect;
    this.loop = loop;

    try {
      this.images = loadGIF(imageUrl.openStream());
    } catch (IOException e) {
      throw new RuntimeException("Failed to load image from URL: " + imageUrl, e);
    }
  }

  public AnimatedImageOption(
      @NotNull MapView view,
      @NotNull File imageFile,
      boolean loop,
      @NotNull Consumer<MiniMapGUI> onSelect) {
    this.view = view;
    this.onSelect = onSelect;
    this.images = loadGIF(imageFile);
    this.loop = loop;
  }

  public AnimatedImageOption(
      @NotNull MapView view,
      @NotNull InputStream imageStream,
      boolean loop,
      @NotNull Consumer<MiniMapGUI> onSelect) {
    this.view = view;
    this.onSelect = onSelect;
    this.images = loadGIF(imageStream);
    this.loop = loop;
  }

  @NotNull
  private static List<MinecraftGIFImage> loadGIF(@NotNull Object input) {
    final List<MinecraftGIFImage> list = new ArrayList<>();
    final ImageReader reader = ImageIO.getImageReadersByFormatName("gif").next();

    try (ImageInputStream stream = ImageIO.createImageInputStream(input)) {
      reader.setInput(stream);
      System.out.println(reader.getFormatName());

      int count = reader.getNumImages(true);
      for (int index = 0; index < count; index++) {
        final BufferedImage image = reader.read(index);
        final IIOMetadataNode node =
            (IIOMetadataNode)
                reader.getImageMetadata(index).getAsTree("javax_imageio_gif_image_1.0");
        final IIOMetadataNode gce =
            (IIOMetadataNode) node.getElementsByTagName("GraphicControlExtension").item(0);
        final int delayTime = Integer.parseInt(gce.getAttribute("delayTime"));

        list.add(new MinecraftGIFImage(image, delayTime));
      }
    } catch (IOException e) {
      e.printStackTrace();
    }

    return list;
  }

  @Override
  public @NotNull MapView onRender() {
    view.setTrackingPosition(false);
    view.getRenderers().forEach(view::removeRenderer);
    view.addRenderer(new AnimatedImageRenderer(images, loop));
    view.setScale(MapView.Scale.NORMAL);

    return view;
  }

  @Override
  public void onSelect(@NotNull MiniMapGUI gui) {
    onSelect.accept(gui);
  }

  public static final class MinecraftGIFImage {
    @NotNull private final BufferedImage image;
    private final int delay;

    public MinecraftGIFImage(@NotNull BufferedImage image, int delay) {
      this.image = MapPalette.resizeImage(image);
      this.delay = delay;
    }

    @NotNull
    public BufferedImage getImage() {
      return image;
    }

    public int getDelay() {
      return delay;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      MinecraftGIFImage gifImage = (MinecraftGIFImage) o;
      return delay == gifImage.delay && image.equals(gifImage.image);
    }

    @Override
    public int hashCode() {
      return Objects.hash(image, delay);
    }

    @Override
    public String toString() {
      return "GIFImage{" + "image=" + image + ", delay=" + delay + '}';
    }
  }

  private static class AnimatedImageRenderer extends MapRenderer {
    @NotNull private static final Timer TIMER = new Timer("MiniMapGUI-AnimatedImageRenderer", true);

    @NotNull private final List<MinecraftGIFImage> images;
    private final AtomicInteger currentIndex = new AtomicInteger(0);

    public AnimatedImageRenderer(@NotNull List<MinecraftGIFImage> images, boolean doLoop) {
      super(true);
      this.images = images;
      TIMER.scheduleAtFixedRate(
          new TimerTask() {
            private int count = 0;

            @Override
            public void run() {
              final MinecraftGIFImage currentImage = images.get(currentIndex.get());
              if (count++ >= currentImage.getDelay()) {
                count = 0;
                if (currentIndex.get() >= (images.size() - 1) && !doLoop) {
                  cancel();
                  return;
                }
                currentIndex.incrementAndGet();
              }
            }
          },
          0L,
          1L);
    }

    @Override
    public void render(@NotNull MapView map, @NotNull MapCanvas canvas, @NotNull Player player) {
      canvas.drawImage(0, 0, images.get(currentIndex.get()).getImage());
    }
  }
}
