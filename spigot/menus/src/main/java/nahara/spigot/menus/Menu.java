package nahara.spigot.menus;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import nahara.common.attachments.Attachments;
import nahara.spigot.items.ItemStackConvertable;
import nahara.spigot.menus.listeners.MenuEventsListener;

public class Menu {
	private Plugin plugin;
	private Inventory inventory;
	private MenuClickHandler[] handlers;
	private int width, height;
	private boolean allowInventoryEditing = false;

	public BiConsumer<Menu, InventoryClickEvent> onClick;
	public BiConsumer<Menu, InventoryCloseEvent> onClose;

	public Menu(Plugin plugin, Inventory inventory) {
		this.plugin = plugin;
		this.inventory = inventory;
		this.handlers = new MenuClickHandler[inventory.getSize()];

		var sizes = getViewSizes(inventory.getType(), inventory.getSize());
		width = sizes[0];
		height = sizes[1];
	}

	public Menu(Plugin plugin, InventoryType type, String title) {
		this(plugin, Bukkit.createInventory(null, type, title));
	}

	public Inventory getInventory() { return inventory; }
	public int getWidth() { return width; }
	public int getHeight() { return height; }
	public Plugin getPlugin() { return plugin; }
	public boolean isAllowInventoryEditing() { return allowInventoryEditing; }
	public void setAllowInventoryEditing(boolean allowInventoryEditing) { this.allowInventoryEditing = allowInventoryEditing; }

	public void set(int slot, ItemStack stack) { inventory.setItem(slot, stack); }
	public void set(int slot, ItemStackConvertable stack) { set(slot, stack != null? stack.toItemStack() : null); }
	public void set(int slot, MenuClickHandler handler) { handlers[slot] = handler; }

	public void set(int x, int y, ItemStack stack) { set(x + y * getWidth(), stack); }
	public void set(int x, int y, ItemStackConvertable stack) { set(x + y * getWidth(), stack); }
	public void set(int x, int y, MenuClickHandler handler) { set(x + y * getWidth(), handler); }

	public void set(int slot, ItemStack stack, MenuClickHandler handler) {
		set(slot, stack);
		set(slot, handler);
	}

	public void set(int slot, ItemStackConvertable stack, MenuClickHandler handler) {
		set(slot, stack);
		set(slot, handler);
	}

	public void set(int x, int y, ItemStack stack, MenuClickHandler handler) {
		set(x, y, stack);
		set(x, y, handler);
	}

	public void set(int x, int y, ItemStackConvertable stack, MenuClickHandler handler) {
		set(x, y, stack);
		set(x, y, handler);
	}

	public MenuSlot get(int index) { return new MenuSlot(this, index); }
	public MenuSlot get(int x, int y) { return get(x + y * getWidth()); }
	public ItemStack getStack(int slot) { return inventory.getItem(slot); }
	public ItemStack getStack(int x, int y) { return getStack(x + y * getWidth()); }
	public MenuClickHandler getHandler(int slot) { return handlers[slot]; }
	public MenuClickHandler getHandler(int x, int y) { return getHandler(x + y * getWidth()); }

	public void fill(int x, int y, int width, int height, Consumer<MenuSlot> filler) {
		for (int yy = 0; yy < height; yy++) {
			for (int xx = 0; xx < width; xx++) {
				filler.accept(get(x + xx, y + yy));
			}
		}
	}

	public void clear(int slot) {
		set(slot, (ItemStack) null);
		set(slot, (MenuClickHandler) null);
	}

	public void clear(int x, int y) {
		set(x, y, (ItemStack) null);
		set(x, y, (MenuClickHandler) null);
	}

	public void click(InventoryClickEvent event) {
		if (event.getWhoClicked() instanceof Player player) {
			if (onClick != null) onClick.accept(this, event);
			if (event.isCancelled()) return;

			if (event.getClickedInventory() == inventory) {
				var slot = get(event.getSlot());
				if (slot.getHandler() != null) slot.getHandler().onClick(slot, event);
			}
		}

		if (!allowInventoryEditing) event.setCancelled(true);
	}

	public void close(InventoryCloseEvent event) {
		if (onClose != null) onClose.accept(this, event);
		if (event.getPlayer() instanceof Player player) Attachments.getGlobal().remove(player, Menu.class);
	}

	public void openMenu(Player player) {
		ensureListenerRegistered(plugin);
		player.openInventory(getInventory());
		Attachments.getGlobal().set(player, Menu.class, this);
	}

	public static void ensureListenerRegistered(Plugin plugin) {
		if (!Attachments.getGlobal().has(plugin, MenuEventsListener.class)) {
			var listener = new MenuEventsListener(plugin);
			Bukkit.getServer().getPluginManager().registerEvents(listener, plugin);
			Attachments.getGlobal().set(plugin, MenuEventsListener.class, listener);
		}
	}

	public static int[] getViewSizes(InventoryType type, int size) {
		return switch (type) {
		case CHEST -> new int[] { 9, size / 9 };
		case HOPPER, DROPPER -> new int[] { 3, 3 };
		case BARREL, ENDER_CHEST -> new int[] { 9, 3 };
		case PLAYER -> new int[] { 9, 4 };
		default -> new int[] { size, 1 };
		};
	}
}
