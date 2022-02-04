package me.byteful.lib.mmgui;

import me.byteful.lib.mmgui.api.stage.Stage;
import me.byteful.lib.mmgui.listeners.InventoryListener;
import me.byteful.lib.mmgui.listeners.PlayerListener;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapView;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class MiniMapGUI {
  private static final int VERSION;

  static {
    // Code from Redempt's RedLib Spigot library
    // (https://github.com/byteful/RedLib/blob/master/src/redempt/redlib/RedLib.java)
    Pattern pattern = Pattern.compile("1\\.([0-9]+)");
    Matcher matcher = pattern.matcher(Bukkit.getBukkitVersion());
    matcher.find();
    VERSION = Integer.parseInt(matcher.group(1));
  }

  @NotNull private final List<Stage> stages = new ArrayList<>();
  @NotNull private final List<Listener> listeners = new ArrayList<>();
  @NotNull private final JavaPlugin plugin;
  @NotNull private final Player player;
  protected boolean wraparoundStages = true;
  @Nullable private Stage currentStage;
  private int currentStageIndex = -1;
  @Nullable private ItemStack previousOffhandItem, activeMapItem;

  // CONFIGURABLE OPTIONS (RECOMMENDED TO CHANGE THROUGH CONSTRUCTOR OF IMPLEMENTING CLASS)
  private boolean isOpened = false;

  public MiniMapGUI(@NotNull final JavaPlugin plugin, @NotNull final Player player) {
    if (VERSION < 9) {
      throw new RuntimeException(
          "Spigot v1."
              + VERSION
              + " is not supported by MiniMapGUI (offhand is required). Please use v1.9+.");
    }

    this.plugin = plugin;
    this.player = player;
  }

  public abstract void setup();

  public abstract void onLeftClick();

  public abstract void onRightClick();

  public abstract void onDrop();

  protected void selectCurrentOption() {
    if (currentStage != null) {
      currentStage.selectCurrentOption(this);
    }
  }

  protected void addStage(
      @Range(from = 0, to = Integer.MAX_VALUE) final int index, @NotNull final Stage stage) {
    if (!isOpened) {
      stages.add(index, stage);
    }
  }

  protected void nextStage() {
    if (stages.isEmpty()) {
      throw new IllegalStateException("Stages cannot be empty!");
    }

    if (currentStageIndex + 1 > stages.size() - 1) {
      if (!wraparoundStages) {
        return;
      }

      currentStageIndex = 0;
    } else {
      currentStageIndex++;
    }
    currentStage = stages.get(currentStageIndex);
  }

  protected void previousStage() {
    if (stages.isEmpty()) {
      throw new IllegalStateException("Stages cannot be empty!");
    }

    if (currentStageIndex - 1 < 0) {
      if (!wraparoundStages) {
        return;
      }

      currentStageIndex = stages.size() - 1;
    } else {
      currentStageIndex--;
    }
    currentStage = stages.get(currentStageIndex);
  }

  public void open() {
    previousOffhandItem = player.getInventory().getItemInOffHand().clone();

    setup();
    setupListeners();

    if (currentStage == null) {
      nextStage();
    }

    render();
    isOpened = true;
  }

  public void render() {
    activeMapItem = buildMap();
    player.getInventory().setItemInOffHand(activeMapItem);
  }

  private void setupListeners() {
    listeners.add(new InventoryListener(this));
    listeners.add(new PlayerListener(this));
    listeners.add(
        new Listener() {
          @EventHandler
          public void onPluginDisable(PluginDisableEvent event) {
            if (event.getPlugin() == plugin) {
              MiniMapGUI.this.close(true);
            }
          }
        });
    listeners.forEach(listener -> Bukkit.getPluginManager().registerEvents(listener, plugin));
  }

  @NotNull
  private ItemStack buildMap() {
    assert currentStage != null : "Stage should not be null while rendering a MapView!";
    if (currentStage.getCurrentOption() == null) {
      currentStage.nextOption();
    }

    final MapView view = currentStage.getCurrentOption().onRender();
    final ItemStack item = new ItemStack(Material.FILLED_MAP);
    MapMeta meta = (MapMeta) item.getItemMeta();
    if (meta == null) {
      throw new RuntimeException("Failed to get MapMeta for " + item);
    }
    meta.setMapView(view);
    item.setItemMeta(meta);

    return item;
  }

  public void close() {
    close(false);
  }

  public void close(boolean now) {
    Runnable run =
        () -> {
          player.getInventory().setItemInOffHand(previousOffhandItem);
          previousOffhandItem = null;
          currentStage = null;
          currentStageIndex = -1;
          isOpened = false;
          listeners.forEach(HandlerList::unregisterAll);
        };

    if (now) {
      run.run();
    } else {
      Bukkit.getScheduler().runTaskLater(plugin, run, 1L);
    }
  }

  @NotNull
  public Player getPlayer() {
    return player;
  }

  public boolean isOpened() {
    return isOpened;
  }

  @NotNull
  public List<Stage> getStages() {
    return stages;
  }

  @Nullable
  public Stage getCurrentStage() {
    return currentStage;
  }

  public int getCurrentStageIndex() {
    return currentStageIndex;
  }

  public boolean isActiveMapItem(@NotNull final ItemStack check) {
    return check.isSimilar(activeMapItem);
  }
}
