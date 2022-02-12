package me.byteful.lib.mmgui.listeners;

import me.byteful.lib.mmgui.MiniMapGUI;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class PlayerListener implements Listener {
  @NotNull private final MiniMapGUI gui;

  public PlayerListener(@NotNull MiniMapGUI gui) {
    this.gui = gui;
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onPlayerQuit(PlayerQuitEvent event) {
    final Player p = event.getPlayer();

    if (!gui.isOpened() || !p.getUniqueId().equals(gui.getPlayer().getUniqueId())) {
      return;
    }

    gui.close(true);
  }

  @EventHandler
  public void onPlayerInteract(PlayerInteractEvent event) {
    final Player p = event.getPlayer();
    final Action action = event.getAction();
    boolean b = false;

    if (!gui.isOpened() || !p.getUniqueId().equals(gui.getPlayer().getUniqueId())) {
      return;
    }

    if (action == Action.RIGHT_CLICK_AIR
        && event.hasItem()
        && event.getHand() == EquipmentSlot.OFF_HAND) {
      gui.onRightClick();
      b = true;
    }

    if (action == Action.LEFT_CLICK_AIR) {
      gui.onLeftClick();
      b = true;
    }

    if (b) {
      event.setCancelled(true);
      event.setUseInteractedBlock(Event.Result.DENY);
      event.setUseItemInHand(Event.Result.DENY);
    }
  }

  @EventHandler
  public void onPlayerSwapItemEvent(PlayerSwapHandItemsEvent event) {
    final Player p = event.getPlayer();

    if (!gui.isOpened() || !p.getUniqueId().equals(gui.getPlayer().getUniqueId())) {
      return;
    }

    event.setCancelled(true);
  }

  @EventHandler
  public void onPlayerItemDrop(PlayerDropItemEvent event) {
    final Player p = event.getPlayer();
    final ItemStack item = event.getItemDrop().getItemStack();

    if (!gui.isOpened()
        || !p.getUniqueId().equals(gui.getPlayer().getUniqueId())
        || !gui.isActiveMapItem(item)) {
      return;
    }

    event.setCancelled(true);
    gui.onDrop();
  }

  @EventHandler
  public void onPlayerDeath(PlayerDeathEvent event) {
    final Player p = event.getEntity();
    if (!gui.isOpened() || !p.getUniqueId().equals(gui.getPlayer().getUniqueId())) {
      return;
    }

    event.getDrops().removeIf(gui::isActiveMapItem);
    gui.close(true);
  }

  @EventHandler
  public void onPlayerWorldSwitch(PlayerChangedWorldEvent event) {
    final Player p = event.getPlayer();

    if (!gui.isOpened() || !p.getUniqueId().equals(gui.getPlayer().getUniqueId())) {
      return;
    }

    gui.close(true);
  }
}
