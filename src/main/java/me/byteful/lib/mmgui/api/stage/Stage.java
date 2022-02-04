package me.byteful.lib.mmgui.api.stage;

import me.byteful.lib.mmgui.MiniMapGUI;
import me.byteful.lib.mmgui.api.option.Option;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface Stage {
  @Nullable
  Option getCurrentOption();

  int getCurrentOptionIndex();

  @NotNull
  List<Option> getOptions();

  void nextOption();

  void previousOption();

  void selectCurrentOption(@NotNull final MiniMapGUI gui);
}
