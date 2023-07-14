package nahara.spigot.menus;

import org.bukkit.Material;

import nahara.spigot.items.BukkitStackBuilder;

public class SlotsFillers {
	public static void border(MenuSlot slot) {
		slot.set(new BukkitStackBuilder(Material.BLACK_STAINED_GLASS_PANE).name("&f "));
		slot.set((s2, event) -> event.setCancelled(true));
	}
}
