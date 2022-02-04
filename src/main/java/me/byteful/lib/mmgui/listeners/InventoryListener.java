package me.byteful.lib.mmgui.listeners;

import me.byteful.lib.mmgui.MiniMapGUI;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.jetbrains.annotations.NotNull;

public class InventoryListener implements Listener {
  @NotNull private final MiniMapGUI gui;

  public InventoryListener(@NotNull MiniMapGUI gui) {
    this.gui = gui;
  }

  @EventHandler(ignoreCancelled = true)
  public void onInventoryClick(InventoryClickEvent event) {
    final HumanEntity clicker = event.getWhoClicked();

    if (!gui.isOpened()
        || event.getRawSlot() != 45
        || !(clicker instanceof Player)
        || !clicker.getUniqueId().equals(gui.getPlayer().getUniqueId())) {
      return;
    }

    event.setCancelled(true);
    event.setResult(Event.Result.DENY);
  }

  @EventHandler
  public void onInventoryDrag(InventoryDragEvent event) {
    final HumanEntity clicker = event.getWhoClicked();

    if (!gui.isOpened()
        || !event.getRawSlots().contains(45)
        || !(clicker instanceof Player)
        || !clicker.getUniqueId().equals(gui.getPlayer().getUniqueId())) {
      return;
    }

    event.setCancelled(true);
  }
}
