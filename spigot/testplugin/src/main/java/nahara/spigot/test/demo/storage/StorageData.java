package nahara.spigot.test.demo.storage;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.entity.Player;

public class StorageData {
	private Player player;
	private Map<Material, Integer> items = new HashMap<>();

	public StorageData(Player player) {
		this.player = player;
	}

	public Player getPlayer() {
		return player;
	}

	public int get(Material mat) {
		return items.getOrDefault(mat, 0);
	}

	public void set(Material mat, int amount) {
		items.put(mat, amount);
	}
}
