package com.github.unchama.task;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.github.unchama.gigantic.Gigantic;
import com.github.unchama.gigantic.PlayerManager;
import com.github.unchama.player.GiganticPlayer;
import com.github.unchama.player.GiganticStatus;
import com.github.unchama.player.time.PlayerTimeManager;
import com.github.unchama.yml.ConfigManager;
import com.github.unchama.yml.DebugManager;

public class PlayerTimeTaskRunnable extends BukkitRunnable{
	Gigantic plugin = Gigantic.plugin;
	ConfigManager config = Gigantic.yml.getManager(ConfigManager.class);
	DebugManager debug = Gigantic.yml.getManager(DebugManager.class);

	private List<Player> playerlist;
	private int size;
	private int count;

	public PlayerTimeTaskRunnable(List<Player> playerlist) {
		this.playerlist = new ArrayList<Player>(playerlist);
		this.size = this.playerlist.size();
		this.count = -1;
	}

	@Override
	public void run() {
		//1分毎にプレイ時間と待機時間を更新
		Bukkit.getScheduler().runTask(plugin, new Runnable() {
			@Override
			public void run(){
				count++;
				if(count >= size || playerlist.isEmpty()){
					cancel();
					return;
				}else{
					Player player = Bukkit.getServer().getPlayer(playerlist.get(count).getUniqueId());
					if(player != null){
						GiganticPlayer gp = PlayerManager.getGiganticPlayer(player);
						GiganticStatus gs = PlayerManager.getStatus(gp);
						if(gs.equals(GiganticStatus.AVAILABLE)){
							PlayerTimeManager timeMng = gp.getManager(PlayerTimeManager.class);
							timeMng.runUpdate();
						}
					}
				}
			}
		});
	}
}
