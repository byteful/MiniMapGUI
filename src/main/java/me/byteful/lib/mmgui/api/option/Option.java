package me.byteful.lib.mmgui.api.option;

import me.byteful.lib.mmgui.MiniMapGUI;
import org.bukkit.map.MapView;
import org.jetbrains.annotations.NotNull;

public interface Option {
  @NotNull
  MapView onRender();

  void onSelect(@NotNull final MiniMapGUI gui);
}
