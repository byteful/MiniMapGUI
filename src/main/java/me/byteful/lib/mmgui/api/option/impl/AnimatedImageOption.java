package me.byteful.lib.mmgui.api.option.impl;

import me.byteful.lib.mmgui.MiniMapGUI;
import me.byteful.lib.mmgui.api.option.Option;
import me.byteful.lib.mmgui.util.GIFHelper;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public class AnimatedImageOption implements Option {
  @NotNull private final MapView view;
  @NotNull private final List<GIFHelper.GIFImageFrame> images;
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
  private static List<GIFHelper.GIFImageFrame> loadGIF(@NotNull Object input) {
    //    final List<MinecraftGIFImage> list = new ArrayList<>();
    //    final ImageReader reader = ImageIO.getImageReadersByFormatName("gif").next();
    //
    //    try (ImageInputStream stream = ImageIO.createImageInputStream(input)) {
    //      reader.setInput(stream);
    //
    //      if(!reader.getFormatName().equalsIgnoreCase("gif")) {
    //        throw new RuntimeException("Failed to load possible (likely not) GIF input: " +
    // input);
    //      }
    //
    //      int count = reader.getNumImages(true);
    //      for (int index = 0; index < count; index++) {
    //        final BufferedImage image = reader.read(index);
    //        final IIOMetadataNode node =
    //            (IIOMetadataNode)
    //                reader.getImageMetadata(index).getAsTree("javax_imageio_gif_image_1.0");
    //        final IIOMetadataNode gce =
    //            (IIOMetadataNode) node.getElementsByTagName("GraphicControlExtension").item(0);
    //        final int delayTime = Integer.parseInt(gce.getAttribute("delayTime"));
    //
    //        list.add(new MinecraftGIFImage(image, delayTime));
    //      }
    //    } catch (IOException e) {
    //      e.printStackTrace();
    //    }
    //
    //    return list;

    return GIFHelper.readGIF(input);
  }

  @Override
  public @NotNull MapView onRender() {
    view.setTrackingPosition(false);
    view.getRenderers().forEach(view::removeRenderer);
    view.addRenderer(new AnimatedImageRenderer(images, loop));
    view.setScale(MapView.Scale.CLOSEST);

    return view;
  }

  @Override
  public void onSelect(@NotNull MiniMapGUI gui) {
    onSelect.accept(gui);
  }

  private static class AnimatedImageRenderer extends MapRenderer {
    @NotNull private static final Timer TIMER = new Timer("MiniMapGUI-AnimatedImageRenderer", true);

    @NotNull private final List<GIFHelper.GIFImageFrame> images;
    private final AtomicInteger currentIndex = new AtomicInteger(0);

    public AnimatedImageRenderer(@NotNull List<GIFHelper.GIFImageFrame> images, boolean doLoop) {
      super(true);
      this.images = images;
      TIMER.scheduleAtFixedRate(
          new TimerTask() {
            private int count = 0;

            @Override
            public void run() {
              if (currentIndex.get() >= (images.size() - 1) && !doLoop) {
                cancel();
                return;
              } else if (currentIndex.get() >= (images.size() - 1)) {
                count = 0;
                currentIndex.set(0);
              }

              final GIFHelper.GIFImageFrame currentImage = images.get(currentIndex.get());
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
          10L);
    }

    @Override
    public void render(@NotNull MapView map, @NotNull MapCanvas canvas, @NotNull Player player) {
      canvas.drawImage(0, 0, images.get(currentIndex.get()).getImage());
    }
  }
}
