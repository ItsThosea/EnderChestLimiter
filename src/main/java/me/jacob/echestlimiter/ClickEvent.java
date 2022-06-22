package me.jacob.echestlimiter;

import lombok.val;
import org.bukkit.Material;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.BundleMeta;

public final class ClickEvent implements Listener {
	private static final boolean isCavesAndCliffs = materialExists("MUSIC_DISC_OTHERSIDE");
	private static final boolean isCombatUpdate = isCavesAndCliffs || materialExists("ELYTRA");

	@EventHandler(ignoreCancelled = true)
	public void onClick(InventoryClickEvent event) {
		if(event.getClickedInventory() == null)
			return;

		if(event.getClickedInventory().getType() != InventoryType.ENDER_CHEST)
			return;

		Player p = (Player) event.getWhoClicked();

		if(p.isOp() && Config.isBypassWithOp())
			return;

		if(Config.getBypassPermission() != null && p.hasPermission(Config.getBypassPermission()))
			return;

		val item = event.getCurrentItem();
		val cursorItem = event.getCursor();

		if(shouldBlock(item) || shouldBlock(cursorItem)) {
			handleBlocked(event);
		} else if(event.getClick() == ClickType.NUMBER_KEY &&
				shouldBlock(p.getInventory().getItem(event.getHotbarButton()))) {
			handleBlocked(event);
		}
	}

	private void handleBlocked(InventoryClickEvent event) {
		event.setCancelled(true);
		event.getWhoClicked().sendMessage(Config.getBlockedMessage());
	}

	private boolean shouldBlock(ItemStack item) {
		if(item == null)
			return false;
		val type = item.getType();
		if(Config.getBlockedItems().contains(type))
			return true;

		if(!isCombatUpdate)
			return false;

		val meta = item.getItemMeta();
		if(meta instanceof BlockStateMeta) {
			val blockMeta = (BlockStateMeta) meta;

			if(blockMeta.getBlockState() instanceof ShulkerBox) {
				val shulker = (ShulkerBox) blockMeta.getBlockState();

				for(val stack : shulker.getInventory().getContents()) {
					if(shouldBlock(stack))
						return true;
				}
			}

			return false;
		}

		if(!isCavesAndCliffs)
			return false;

		if(meta instanceof BundleMeta) {
			val bundle = (BundleMeta) meta;

			for(val stack : bundle.getItems()) {
				if(shouldBlock(stack))
					return true;
			}
		}

		return false;
	}

	private static boolean materialExists(String name) {
		try {
			Material.valueOf(name);
			return true;
		} catch(IllegalArgumentException e) {
			return false;
		}
	}
}
