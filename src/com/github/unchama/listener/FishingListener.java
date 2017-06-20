package com.github.unchama.listener;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import com.github.unchama.gigantic.Gigantic;
import com.github.unchama.gigantic.PlayerManager;
import com.github.unchama.gui.fishing.FishingMainMenuManager;
import com.github.unchama.player.GiganticPlayer;
import com.github.unchama.player.fishing.FishingManager;
import com.github.unchama.player.fishinglevel.FishingLevelManager;
import com.github.unchama.player.settings.PlayerSettingsManager;
import com.github.unchama.util.Util;
import com.github.unchama.yml.FishingYmlManager;

/**
 *
 * @author ten_niti
 *
 */
public class FishingListener implements Listener {

	// 放置釣り
	@EventHandler
	public void idleFishing(PlayerFishEvent event) {
		// player.sendMessage(event.getState().name());
		if (event.getState() != PlayerFishEvent.State.BITE) {
			return;
		}
		Player player = event.getPlayer();
		GiganticPlayer gp = PlayerManager.getGiganticPlayer(player);
		FishingYmlManager fishingYmlManager = Gigantic.yml
				.getManager(FishingYmlManager.class);
		// 放置釣りが有効なワールドでなければ終了
		if (!fishingYmlManager.isIdleFishingEnableWorld(player.getWorld()
				.getName())) {
			return;
		}
		FishingLevelManager fishingLevelManager = gp
				.getManager(FishingLevelManager.class);
		FishingManager fishingManager = gp.getManager(FishingManager.class);

		// 経験値付与とカウント
		int addExp = fishingYmlManager.getIdleFishingExp();
		fishingLevelManager.addExp(addExp);
		fishingManager.addIdleFishingCount();

		// アイテム付与
		ItemStack item = new ItemStack(Material.RAW_FISH);
		boolean isSuccess = fishingManager.addItem(item);
		if (!isSuccess) {
			Util.giveItem(player, item, true);
		}
	}

	// 自力釣り
	@EventHandler
	public void activeFishing(PlayerFishEvent event) {
		if (event.getState() != PlayerFishEvent.State.CAUGHT_FISH) {
			return;
		}
		Player player = event.getPlayer();
		GiganticPlayer gp = PlayerManager.getGiganticPlayer(player);
		FishingYmlManager fishingYmlManager = Gigantic.yml
				.getManager(FishingYmlManager.class);
		// 放置釣りが有効なワールドでなければ終了
		if (!fishingYmlManager.isIdleFishingEnableWorld(player.getWorld()
				.getName())) {
			return;
		}

		FishingLevelManager fishingLevelManager = gp
				.getManager(FishingLevelManager.class);
		FishingManager fishingManager = gp.getManager(FishingManager.class);

		int addExp = fishingYmlManager.getActiveFishingExp();
		fishingLevelManager.addExp(addExp);
		fishingManager.addActiveFishingCount();
	}

	// 釣りメニューショートカット メインハンドに釣竿を持って左クリック
	@EventHandler
	public void fishingMenuShortcut(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		GiganticPlayer gp = PlayerManager.getGiganticPlayer(player);
		PlayerSettingsManager settingsManager = gp
				.getManager(PlayerSettingsManager.class);
		if (!settingsManager.getFishingMenuShortcut()) {
			return;
		}

		// メインハンドでなければ終了
		EquipmentSlot hand = event.getHand();
		if (hand == null || hand.equals(EquipmentSlot.OFF_HAND)) {
			return;
		}

		// 釣竿でなければ終了
		ItemStack itemstack = event.getItem();
		if (itemstack == null || itemstack.getType() != Material.FISHING_ROD) {
			return;
		}

		// なぜか釣竿を空中に右クリックすると同TICKで左クリック判定が誤爆するのでチェック
		FishingManager fishingManager = gp.getManager(FishingManager.class);
		if (fishingManager.checkTick()) {
			return;
		}

		// 右クリックの時終了
		Action action = event.getAction();
		// Bukkit.getServer().getLogger().info(action.name() + " " +
		// player.getStatistic(org.bukkit.Statistic.PLAY_ONE_TICK));
		if (action.equals(Action.RIGHT_CLICK_AIR)
				|| action.equals(Action.RIGHT_CLICK_BLOCK)) {
			return;
		}

		Gigantic.guimenu.getManager(FishingMainMenuManager.class).open(player,
				0, true);
	}
}
