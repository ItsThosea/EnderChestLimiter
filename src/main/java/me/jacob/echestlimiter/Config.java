package me.jacob.echestlimiter;

import lombok.Getter;
import lombok.val;
import org.bukkit.ChatColor;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class Config {
	private static final EnderChestLimiter plugin = EnderChestLimiter.getInstance();

	@Getter
	private static List<Material> blockedItems;
	@Getter
	private static String blockedMessage;

	@Getter
	private static boolean bypassWithOp;
	@Getter
	private static String bypassPermission;

	private Config() {}

	public static void reload() {
		plugin.reloadConfig();
		val config = plugin.getConfig();

		blockedItems = new ArrayList<>();
		for(String materialName : config.getStringList("blocked-items")) {
			Material m = getMaterial(
					materialName.replace(' ', '_').toUpperCase(Locale.ENGLISH)
			);

			if(m == null) {
				plugin.getLogger().warning("Material \"" + materialName + "\" does not exist!");
				continue;
			}

			blockedItems.add(m);
		}

		String message = config.getString("blocked-message");
		if(message == null) {
			plugin.getLogger().warning("Blocked message is empty!");
			message = ChatColor.RED + "You can't put that item in your ender chest!";
		} else {
			message = ChatColor.translateAlternateColorCodes(
					'&',
					message
			);
		}

		blockedMessage = message;

		bypassWithOp = config.getBoolean("bypass-with-op");
		bypassPermission = config.getString("bypass-permission");

		if(bypassPermission != null && (bypassPermission.isEmpty() || bypassPermission.equals(" "))) {
			bypassPermission = null; // No permission
		}

		plugin.getLogger().info("Config reloaded!");
	}

	private static Material getMaterial(String materialName) {
		try {
			return Material.valueOf(materialName);
		} catch(Exception e) {
			return null;
		}
	}
}
