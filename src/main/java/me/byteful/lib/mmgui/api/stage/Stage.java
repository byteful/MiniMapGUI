package me.byteful.lib.mmgui.api.stage;

import me.byteful.lib.mmgui.MiniMapGUI;
import me.byteful.lib.mmgui.api.option.Option;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/** The stage class that holds options to be selected via the GUI. */
public interface Stage {
  /**
   * Returns the current option that is selected in the stage.
   *
   * @return the current option
   */
  @Nullable
  Option getCurrentOption();

  /**
   * Returns the current index the current option is in the list of options.
   *
   * @return the current option index
   */
  int getCurrentOptionIndex();

  /**
   * Returns all the options registered to this stage.
   *
   * @return the options
   */
  @NotNull
  List<Option> getOptions();

  /** Skips to the next option in the list. Wraps around if option is enabled. */
  void nextOption();

  /** Skips back the previous option in the list. Wraps around if option is enabled. */
  void previousOption();

  /**
   * Runs the select code for the currently selected option.
   *
   * @param gui the GUI to run the code on
   */
  void selectCurrentOption(@NotNull final MiniMapGUI gui);
}
