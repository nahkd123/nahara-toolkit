package nahara.spigot.test.demo.storage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import nahara.common.attachments.Attachments;
import nahara.common.localize.Message;
import nahara.spigot.items.BukkitStackBuilder;
import nahara.spigot.menus.Menu;
import nahara.spigot.menus.SlotsFillers;
import nahara.spigot.test.Main;

public class StorageMenu extends Menu {
	private static final Message STORAGE_TITLE = new Message("storage.title", "{} > My Storage");
	private static final Message STORAGE_ITEMS_STORED = new Message("storage.itemsStored", "&7You have: &f{} &7items");
	private static final Message STORAGE_LEFT_CLICK = new Message("storage.leftClick", "&6Left click &7to take all");
	private static final Message STORAGE_RIGHT_CLICK = new Message("storage.rightClick", "&6Right click &7to take a stack");
	private static final Message STORAGE_PUT_ALL = new Message("storage.putAll", "&6Left click &7to insert all materials from your inventory");

	private Player owner;
	private List<ItemStack> materials = new ArrayList<>();

	public StorageMenu(Main plugin, Player owner) {
		super(plugin, Bukkit.createInventory(null, 9 * 6, STORAGE_TITLE.of(owner.getDisplayName())));
		this.owner = owner;

		fill(0, 0, 9, 1, SlotsFillers::border);
		fill(0, 5, 9, 1, SlotsFillers::border);
		set(4, 5, new BukkitStackBuilder(Material.CHEST)
				.glow()
				.name(STORAGE_PUT_ALL.of()), (slot, event) -> {
					event.setCancelled(true);

					if (event.isLeftClick()) {
						var contents = owner.getInventory().getStorageContents();
						var data = getData();

						for (var refMat : materials) {
							var amount = data.get(refMat.getType());

							for (int i = 0; i < contents.length; i++) {
								if (refMat.isSimilar(contents[i])) {
									amount += contents[i].getAmount();
									contents[i] = null;
								}
							}

							data.set(refMat.getType(), amount);
						}

						owner.getInventory().setStorageContents(contents);
						refresh();
					}
				});

		refresh();
	}

	public void refresh() {
		addMaterial(0, 1, Material.COAL);
		addMaterial(1, 1, Material.IRON_INGOT);
		addMaterial(2, 1, Material.DIAMOND);
	}

	public StorageData getData() {
		var data = Attachments.getGlobal().get(owner, StorageData.class);
		if (data == null) Attachments.getGlobal().set(owner, StorageData.class, data = new StorageData(owner));
		return data;
	}

	public void addMaterial(int x, int y, Material mat) {
		set(x, y, new BukkitStackBuilder(mat)
				.newLore(emitter -> {
					emitter.accept("");
					emitter.accept(STORAGE_ITEMS_STORED.of(Integer.toString(getData().get(mat))));
					emitter.accept("");
					emitter.accept(STORAGE_LEFT_CLICK.toString());
					emitter.accept(STORAGE_RIGHT_CLICK.toString());
					emitter.accept("");
				}), (slot, event) -> {
					event.setCancelled(true);
					var data = getData();
					var amount = data.get(mat);

					var toGive = new ItemStack(mat, event.isLeftClick()? amount : Math.min(amount, mat.getMaxStackSize()));
					amount -= toGive.getAmount();

					var leftover = Optional.ofNullable(owner.getInventory().addItem(toGive)).map(v -> v.get(0)).orElse(null);
					if (leftover != null) amount += leftover.getAmount();
					data.set(mat, amount);
					refresh();
				});
		materials.add(new ItemStack(mat));
	}
}
