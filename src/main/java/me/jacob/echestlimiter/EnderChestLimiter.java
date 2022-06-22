package me.jacob.echestlimiter;

import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

public final class EnderChestLimiter extends JavaPlugin {
	@Getter
	private static EnderChestLimiter instance;

	public void onEnable() {
		instance = this;

		this.saveDefaultConfig();
		Config.reload();

		getCommand("reloadechestlimiter").setExecutor(new ReloadCommand());
		getServer().getPluginManager().registerEvents(new ClickEvent(), this);
	}
}
