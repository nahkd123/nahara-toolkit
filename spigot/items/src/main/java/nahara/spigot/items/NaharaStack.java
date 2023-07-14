package nahara.spigot.items;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;

import nahara.common.nbtstring.Nbt;
import nahara.common.nbtstring.NbtArray;
import nahara.common.nbtstring.NbtCompound;
import nahara.common.nbtstring.NbtString;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.chat.ComponentSerializer;

/**
 * <p>A special item stack clone. This clone allows you to manipulate stack's NBT.</p>
 * <p>If you need to edit the item's NBT data extensively, you can use this.</p>
 * @author nahkd
 *
 */
public class NaharaStack implements ItemStackConvertable {
	private NamespacedKey id;
	private int amount;
	private NbtCompound nbt;

	public NaharaStack(Material type, int amount) {
		this(type.getKey(), amount);
	}

	public NaharaStack(NamespacedKey id, int amount) {
		this.id = id;
		this.amount = amount;
		nbt = new NbtCompound();
	}

	public NaharaStack(Material type) {
		this(type, 1);
	}

	public NaharaStack(NamespacedKey id) {
		this(id, 1);
	}

	public NaharaStack(ItemStack from) {
		this.id = from.getType().getKey();
		this.amount = from.getAmount();
		this.nbt = Nbt.fromString(from.getItemMeta().getAsString()).compound();
	}

	public NaharaStack(NaharaStack cloneOf) {
		this.id = cloneOf.id;
		this.amount = cloneOf.amount;
		this.nbt = cloneOf.nbt.copy();
	}

	public NamespacedKey getId() {
		return id;
	}

	public void setId(NamespacedKey id) {
		this.id = id;
	}

	public Material getMaterial() {
		return id.getNamespace().equalsIgnoreCase("minecraft")? Material.getMaterial(id.getKey()) : null;
	}

	public void setMaterial(Material type) {
		id = type.getKey();
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public NbtCompound getNbt() {
		return nbt;
	}

	@Override
	public ItemStack toItemStack() {
		var stack = Bukkit.getItemFactory().createItemStack(getId().toString() + getNbt().serializeAsString());
		stack.setAmount(getAmount());
		return stack;
	}

	public NaharaStack copy() {
		return new NaharaStack(this);
	}

	// NBT edit thing
	public NbtCompound getDisplayData() {
		var nbt = getNbt().get("display");
		return nbt != null? nbt.compound() : null;
	}

	public String getRawDisplayName() {
		var nbt = getDisplayData();
		var name = nbt != null? nbt.get("Name") : null;
		return name != null? name.string().getText() : null;
	}

	public void setRawDisplayName(String json) {
		if (json == null) {
			getNbt().getOrCreate("display", NbtCompound::new).compound().remove("Name");
			return;
		} else {
			getNbt()
			.getOrCreate("display", NbtCompound::new).compound()
			.getOrCreate("Name", NbtString::new).string()
			.setText(json);
		}
	}

	public BaseComponent[] getDisplayName() {
		var raw = getRawDisplayName();
		return raw != null? ComponentSerializer.parse(raw) : null;
	}

	public void setDisplayName(BaseComponent... components) {
		setRawDisplayName(components != null? ComponentSerializer.toString(components) : null);
	}

	public void clearDisplayName() {
		setRawDisplayName(null);
	}

	public void setDisplayName(ComponentBuilder builder) {
		setDisplayName(builder.create());
	}

	public NbtArray getNbtLore() {
		var nbt = getDisplayData();
		var arr = nbt.get("Lore");
		return arr != null? arr.array() : null;
	}

	public void setNbtLore(NbtArray lore) {
		getNbt().getOrCreate("display", NbtCompound::new).compound().set("Lore", lore);
	}

	public List<String> getRawLore() {
		var nbt = getNbtLore();

		if (nbt != null) {
			var list = new ArrayList<String>();
			nbt.children.forEach(v -> { if (v != null) list.add(v.string().getText()); });
			return list;
		}

		return null;
	}

	public void setRawLore(List<String> jsons) {
		var arr = new NbtArray();
		var disp = getNbt().getOrCreate("display", NbtCompound::new).compound();

		if (jsons == null) {
			disp.remove("Lore");
			return;
		} else {
			jsons.forEach(v -> { if (v != null) arr.add(new NbtString(v)); });
			disp.set("Lore", arr);
		}
	}

	public List<BaseComponent[]> getLore() {
		var raw = getRawLore();
		if (raw == null) return null;

		var components = new ArrayList<BaseComponent[]>();
		raw.forEach(json -> components.add(ComponentSerializer.parse(json)));
		return components;
	}

	public void setLore(List<BaseComponent[]> lore) {
		setRawLore(lore.stream().map(v -> ComponentSerializer.toString(v)).toList());
	}

	public void setLoreFromBuilders(List<ComponentBuilder> builders) {
		setRawLore(builders.stream().map(v -> ComponentSerializer.toString(v.create())).toList());
	}

	public void modifyLore(Consumer<Consumer<BaseComponent[]>> appender, boolean isAppending) {
		var list = isAppending? getLore() : new ArrayList<BaseComponent[]>();
		appender.accept(list::add);
		setLore(list);
	}

	public void buildLore(Consumer<Consumer<ComponentBuilder>> appender, boolean isAppending) {
		var list = isAppending? getLore() : new ArrayList<BaseComponent[]>();
		appender.accept(builder -> list.add(builder.create()));
		setLore(list);
	}

	public void clearLore() {
		setRawLore(null);
	}
}
