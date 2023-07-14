package nahara.spigot.menus;

import org.bukkit.inventory.ItemStack;

import nahara.spigot.items.ItemStackConvertable;

public class MenuSlot {
	private Menu menu;
	private int index, x, y;

	public MenuSlot(Menu menu, int index) {
		this.menu = menu;
		this.index = index;
		this.x = index % menu.getWidth();
		this.y = index / menu.getWidth();
	}

	public Menu getMenu() { return menu; }
	public int getIndex() { return index; }
	public int getX() { return x; }
	public int getY() { return y; }
	public ItemStack getStack() { return menu.getStack(index); }
	public MenuClickHandler getHandler() { return menu.getHandler(index); }
	public void set(ItemStack stack) { menu.set(index, stack); }
	public void set(ItemStackConvertable stack) { menu.set(index, stack); }
	public void set(MenuClickHandler handler) { menu.set(index, handler); }
	public void clear() { menu.clear(index); }
}
