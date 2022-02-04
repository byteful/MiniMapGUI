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
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Objects;
import java.util.function.Consumer;

public class ImageOption implements Option {
  @NotNull private final World world;
  @NotNull private final BufferedImage image;
  @NotNull private final Consumer<MiniMapGUI> onSelect;

  public ImageOption(
      @NotNull World world, @NotNull URL imageUrl, @NotNull Consumer<MiniMapGUI> onSelect) {
    this.world = world;
    this.onSelect = onSelect;

    try {
      this.image = ImageIO.read(imageUrl);
    } catch (IOException e) {
      throw new RuntimeException("Failed to load image from URL: " + imageUrl, e);
    }
  }

  public ImageOption(
      @NotNull World world, @NotNull File imageFile, @NotNull Consumer<MiniMapGUI> onSelect) {
    this.world = world;
    this.onSelect = onSelect;

    try {
      this.image = ImageIO.read(imageFile);
    } catch (IOException e) {
      throw new RuntimeException("Failed to load image from file: " + imageFile, e);
    }
  }

  public ImageOption(
      @NotNull World world,
      @NotNull InputStream imageStream,
      @NotNull Consumer<MiniMapGUI> onSelect) {
    this.world = world;
    this.onSelect = onSelect;

    try {
      this.image = ImageIO.read(imageStream);
    } catch (IOException e) {
      throw new RuntimeException("Failed to load image from stream: " + imageStream, e);
    }
  }

  public ImageOption(
      @NotNull World world, @NotNull BufferedImage image, @NotNull Consumer<MiniMapGUI> onSelect) {
    this.world = world;
    this.image = image;
    this.onSelect = onSelect;
  }

  @Override
  public @NotNull MapView onRender() {
    final MapView view = Bukkit.createMap(world);
    view.setTrackingPosition(false);
    view.getRenderers().forEach(view::removeRenderer);
    final ImageRenderer renderer = new ImageRenderer(image);
    view.addRenderer(renderer);
    view.setScale(MapView.Scale.NORMAL);

    return view;
  }

  @Override
  public void onSelect(@NotNull MiniMapGUI gui) {
    onSelect.accept(gui);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ImageOption that = (ImageOption) o;
    return world.equals(that.world) && image.equals(that.image) && onSelect.equals(that.onSelect);
  }

  @Override
  public int hashCode() {
    return Objects.hash(world, image, onSelect);
  }

  @Override
  public String toString() {
    return "ImageOption{" + "world=" + world + ", image=" + image + ", onSelect=" + onSelect + '}';
  }

  private static final class ImageRenderer extends MapRenderer {
    @NotNull private final BufferedImage image;
    // private boolean rendered = false;

    public ImageRenderer(@NotNull BufferedImage image) {
      super(true);
      this.image = MapPalette.resizeImage(image);
    }

    @Override
    public void render(@NotNull MapView map, @NotNull MapCanvas canvas, @NotNull Player player) {
      //      if(rendered) {
      //        return;
      //      }

      canvas.drawImage(0, 0, image);
      // rendered = true;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      ImageRenderer that = (ImageRenderer) o;
      return image.equals(that.image);
    }

    @Override
    public int hashCode() {
      return Objects.hash(image);
    }

    @Override
    public String toString() {
      return "ImageRenderer{" + "image=" + image + '}';
    }
  }
}
