package com.github.unchama.player.time;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.github.unchama.player.GiganticPlayer;
import com.github.unchama.player.moduler.DataManager;
import com.github.unchama.player.moduler.UsingSql;
import com.github.unchama.sql.PlayerTimeTableManager;
import com.github.unchama.util.Util;
import com.github.unchama.yml.DebugManager.DebugEnum;

public class PlayerTimeManager extends DataManager implements UsingSql {

	//プレイ時間差分計算用int
	private int servertick;
	//プレイ時間
	private int playtick;

	//現在座標
	private Location loc;
	//累計放置時間
	private int totalidletick;
	//放置時間
	private int idletime;

	PlayerTimeTableManager tm = sql.getManager(PlayerTimeTableManager.class);

	public PlayerTimeManager(GiganticPlayer gp) {
		super(gp);
	}

    @Override
    public void save(Boolean loginflag) {
    	//セーブ前に時間を更新
    	runUpdate();
        tm.save(gp, loginflag);
    }

    public void firstJoinInit(){
    	reloadSevertick();
		playtick = 0;
		loc = null;
		totalidletick = 0;
		idletime = 0;
		playtick = 0;
    }

    //1分毎にプレイ時間と待機時間を更新
    public void runUpdate(){
    	Player player = Bukkit.getServer().getPlayer(gp.uuid);
		int getservertick = player.getStatistic(org.bukkit.Statistic.PLAY_ONE_TICK);
		int getincrease = getservertick - servertick;
		servertick = getservertick;
		playtick += getincrease;

		//放置判定
		if(player.getLocation().equals(loc)){
			if(isIdle()){
				//既に放置中なら累計放置時間を追加
				totalidletick += getincrease;
			}
			idletime ++;
		}else{
			loc = player.getLocation();
			idletime = 0;
		}
		debug.sendMessage(player,DebugEnum.GUI,"プレイ時間更新"
				+ ":servertick " + servertick
				+ ":playtick " + playtick
				+ ":totalidletick " + totalidletick
				+ ":idletime " + idletime
				);
    }

	//10分間動きがなければ放置
	public boolean isIdle(){
		return idletime >= 10;
	}

	//放置時間を除いた累計プレイ時間を「HH時間MM分」の文字列で返す
	public String GetTotalLoginTimeToString(){
		return Util.toTimeString(Util.toSecond(playtick - totalidletick));
	}

	//累計ログイン時間のgetterとsetter
	public int getPlaytick(){
		return playtick;
	}
	public void setPlaytick(int playtick_){
		playtick = playtick_;
	}

	//累計放置時間のgetterとsetter
	public int getTotalIdletick(){
		return totalidletick;
	}
	public void setTotalIdletick(int totalidletick_){
		totalidletick = totalidletick_;
	}

	//servertickをリロード
	public void reloadSevertick(){
    	Player player = Bukkit.getServer().getPlayer(gp.uuid);
		servertick = player.getStatistic(org.bukkit.Statistic.PLAY_ONE_TICK);
	}
}
