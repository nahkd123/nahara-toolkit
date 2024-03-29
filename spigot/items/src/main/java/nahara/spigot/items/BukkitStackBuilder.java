package nahara.spigot.items;

import java.util.ArrayList;
import java.util.function.Consumer;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import nahara.common.nbtstring.NbtCompound;
import net.md_5.bungee.api.ChatColor;

public class BukkitStackBuilder implements ItemStackConvertable {
	private ItemStack base;
	private ItemMeta meta;

	public BukkitStackBuilder(ItemStack base) {
		this.base = base;
		this.meta = base.getItemMeta();
	}

	public BukkitStackBuilder(Material material, int amount) {
		this(new ItemStack(material, amount));
	}

	public BukkitStackBuilder(Material material) {
		this(material, 1);
	}

	public BukkitStackBuilder name(String name) {
		this.meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
		return this;
	}

	public BukkitStackBuilder lore(Consumer<Consumer<String>> emitter, boolean clear) {
		var lore = (clear || !meta.hasLore())? new ArrayList<String>() : meta.getLore();
		emitter.accept(v -> lore.add(ChatColor.translateAlternateColorCodes('&', v)));
		meta.setLore(lore);
		return this;
	}

	public BukkitStackBuilder newLore(Consumer<Consumer<String>> emitter) {
		lore(emitter, true);
		return this;
	}

	public BukkitStackBuilder appendLore(Consumer<Consumer<String>> emitter) {
		lore(emitter, false);
		return this;
	}

	public BukkitStackBuilder glow() {
		meta.addEnchant(base.getType() == Material.FISHING_ROD? Enchantment.ARROW_FIRE : Enchantment.LURE, 0, true);
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		return this;
	}

	public BukkitStackBuilder unbreakable() {
		meta.setUnbreakable(true);
		return this;
	}

	public BukkitStackBuilder damage(int amount) {
		if (meta instanceof Damageable damageable) damageable.setDamage(amount);
		return this;
	}

	public BukkitStackBuilder nbt(NbtCompound nbt) {
		if (nbt == null) nbt = new NbtCompound();
		base = Bukkit.getItemFactory().createItemStack(base.getType().getKey() + nbt.serializeAsString());
		return this;
	}

	@Override
	public ItemStack toItemStack() {
		base.setItemMeta(meta);
		return base;
	}
}
