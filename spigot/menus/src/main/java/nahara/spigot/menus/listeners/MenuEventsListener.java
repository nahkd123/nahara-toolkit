package nahara.spigot.menus.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.plugin.Plugin;

import nahara.common.attachments.Attachments;
import nahara.spigot.menus.Menu;

public class MenuEventsListener implements Listener {
	private Plugin plugin;

	public MenuEventsListener(Plugin plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onClick(InventoryClickEvent event) {
		if (event.getWhoClicked() instanceof Player player) {
			var attachment = Attachments.getGlobal().get(player, Menu.class);
			if (attachment == null || attachment.getPlugin() != plugin) return;
			if (event.getView().getTopInventory() != attachment.getInventory()) return;
			attachment.click(event);
		}
	}

	@EventHandler
	public void onClose(InventoryCloseEvent event) {
		if (event.getPlayer() instanceof Player player) {
			var attachment = (Menu) Attachments.getGlobal().get(player, Menu.class);
			if (attachment == null || attachment.getPlugin() != plugin) return;
			if (event.getView().getTopInventory() != attachment.getInventory()) return;
			attachment.close(event);
		}
	}
}
