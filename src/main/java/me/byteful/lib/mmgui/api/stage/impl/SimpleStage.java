package me.byteful.lib.mmgui.api.stage.impl;

import me.byteful.lib.mmgui.MiniMapGUI;
import me.byteful.lib.mmgui.api.option.Option;
import me.byteful.lib.mmgui.api.stage.Stage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class SimpleStage implements Stage {
  @NotNull private final List<Option> options;
  private final boolean wraparoundOptions;
  @Nullable private Option currentOption;
  private int currentOptionIndex = -1;

  public SimpleStage(@NotNull List<Option> options, boolean wraparoundOptions) {
    this.options = options;
    this.wraparoundOptions = wraparoundOptions;
  }

  @NotNull
  public static Builder newBuilder() {
    return new Builder();
  }

  @Override
  public @Nullable Option getCurrentOption() {
    return currentOption;
  }

  @Override
  public int getCurrentOptionIndex() {
    return currentOptionIndex;
  }

  @Override
  public @NotNull List<Option> getOptions() {
    return options;
  }

  @Override
  public void nextOption() {
    if (options.isEmpty()) {
      throw new IllegalStateException("Options cannot be empty!");
    }

    if (currentOptionIndex + 1 > options.size() - 1) {
      if (!wraparoundOptions) {
        return;
      }

      currentOptionIndex = 0;
    } else {
      currentOptionIndex++;
    }
    currentOption = options.get(currentOptionIndex);
  }

  @Override
  public void previousOption() {
    if (options.isEmpty()) {
      throw new IllegalStateException("Options cannot be empty!");
    }

    if (currentOptionIndex - 1 < 0) {
      if (!wraparoundOptions) {
        return;
      }

      currentOptionIndex = options.size() - 1;
    } else {
      currentOptionIndex--;
    }
    currentOption = options.get(currentOptionIndex);
  }

  @Override
  public void selectCurrentOption(@NotNull final MiniMapGUI gui) {
    if (currentOption != null) {
      currentOption.onSelect(gui);
    }
  }

  public static final class Builder {
    private List<Option> options = new ArrayList<>();
    private boolean wraparoundOptions = true;

    private Builder() {}

    @NotNull
    public Builder wraparoundOptions(boolean value) {
      wraparoundOptions = value;

      return this;
    }

    public boolean wraparoundOptions() {
      return wraparoundOptions;
    }

    public Builder options(@NotNull List<Option> value) {
      options = new ArrayList<>(value);

      return this;
    }

    @NotNull
    public List<Option> options() {
      return options;
    }

    @NotNull
    public Builder addOption(@NotNull final Option option) {
      options.add(option);

      return this;
    }

    @NotNull
    public SimpleStage build() {
      return new SimpleStage(options, wraparoundOptions);
    }
  }
}
