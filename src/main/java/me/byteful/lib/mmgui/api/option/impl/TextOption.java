package me.byteful.lib.mmgui.api.option.impl;

import me.byteful.lib.mmgui.MiniMapGUI;
import me.byteful.lib.mmgui.api.option.Option;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.map.*;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.Objects;
import java.util.function.Consumer;

public class TextOption implements Option {
  @NotNull private final MapView view;
  @NotNull private final String text;
  @NotNull private final Point renderAt;
  @NotNull private final MapFont font;
  @NotNull private final Consumer<MiniMapGUI> onSelect;

  public TextOption(
      @NotNull World world,
      @NotNull String text,
      @NotNull Point renderAt,
      @NotNull Consumer<MiniMapGUI> onSelect) {
    this.view = Bukkit.createMap(world);
    this.text = text;
    this.renderAt = renderAt;
    this.font = MinecraftFont.Font;
    this.onSelect = onSelect;
  }

  public TextOption(
      @NotNull MapView view,
      @NotNull String text,
      @NotNull Point renderAt,
      @NotNull Consumer<MiniMapGUI> onSelect) {
    this.view = view;
    this.text = text;
    this.renderAt = renderAt;
    this.font = MinecraftFont.Font;
    this.onSelect = onSelect;
  }

  public TextOption(
      @NotNull World world,
      @NotNull String text,
      @NotNull Point renderAt,
      @NotNull MapFont font,
      @NotNull Consumer<MiniMapGUI> onSelect) {
    this.view = Bukkit.createMap(world);
    this.text = text;
    this.renderAt = renderAt;
    this.font = font;
    this.onSelect = onSelect;
  }

  public TextOption(
      @NotNull MapView view,
      @NotNull String text,
      @NotNull Point renderAt,
      @NotNull MapFont font,
      @NotNull Consumer<MiniMapGUI> onSelect) {
    this.view = view;
    this.text = text;
    this.renderAt = renderAt;
    this.font = font;
    this.onSelect = onSelect;
  }

  @Override
  public @NotNull MapView onRender() {
    view.setTrackingPosition(false);
    view.getRenderers().forEach(view::removeRenderer);
    view.addRenderer(new TextRenderer(renderAt, text, font));
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
    TextOption that = (TextOption) o;
    return view.equals(that.view)
        && text.equals(that.text)
        && renderAt.equals(that.renderAt)
        && font.equals(that.font)
        && onSelect.equals(that.onSelect);
  }

  @Override
  public int hashCode() {
    return Objects.hash(view, text, renderAt, font, onSelect);
  }

  @Override
  public String toString() {
    return "TextOption{"
        + "view="
        + view
        + ", text='"
        + text
        + '\''
        + ", renderAt="
        + renderAt
        + ", font="
        + font
        + ", onSelect="
        + onSelect
        + '}';
  }

  private static class TextRenderer extends MapRenderer {
    @NotNull private final Point point;
    @NotNull private final String text;
    @NotNull private final MapFont font;

    public TextRenderer(@NotNull Point point, @NotNull String text, @NotNull MapFont font) {
      super(true);
      this.point = point;
      this.text = text;
      this.font = font;
    }

    @Override
    public void render(@NotNull MapView map, @NotNull MapCanvas canvas, @NotNull Player player) {
      canvas.drawText(point.x, point.y, font, text);
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      TextRenderer that = (TextRenderer) o;
      return point.equals(that.point) && text.equals(that.text) && font.equals(that.font);
    }

    @Override
    public int hashCode() {
      return Objects.hash(point, text, font);
    }

    @Override
    public String toString() {
      return "TextRenderer{" + "point=" + point + ", text='" + text + '\'' + ", font=" + font + '}';
    }
  }
}
