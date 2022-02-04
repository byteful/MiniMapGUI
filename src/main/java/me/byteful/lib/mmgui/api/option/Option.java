package me.byteful.lib.mmgui.api.option;

import me.byteful.lib.mmgui.MiniMapGUI;
import org.bukkit.map.MapView;
import org.jetbrains.annotations.NotNull;

/**
 * An option that can be selected. Provides the view for the minimap to render. Also provides code
 * to run when this is selected in the stage.
 */
public interface Option {
  /**
   * Returns a MapView to be applied to the minimap in the offhand.
   *
   * @return the mapview
   */
  @NotNull
  MapView onRender();

  /**
   * Ran when this option is selected in its stage.
   *
   * @param gui the GUI that is calling this
   */
  void onSelect(@NotNull final MiniMapGUI gui);
}
