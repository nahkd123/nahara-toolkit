package nahara.spigot.menus;

import org.bukkit.event.inventory.InventoryClickEvent;

public interface MenuClickHandler {
	public void onClick(MenuSlot slot, InventoryClickEvent event);
}
