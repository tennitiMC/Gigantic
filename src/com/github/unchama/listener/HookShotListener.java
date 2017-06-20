package com.github.unchama.listener;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.util.Vector;

import com.github.unchama.util.Util;

/**
 *
 * @author ten_niti
 *
 */
public class HookShotListener implements Listener{
	@EventHandler
	public void test(PlayerFishEvent event){
		Bukkit.getServer().getLogger().info(event.getState().name());
		if (event.getState() != PlayerFishEvent.State.FISHING) {
			return;
		}
		Player player = event.getPlayer();
		Block block = player.getTargetBlock(Util.getFluidMaterials(), 32);
		if(block == null || block.getType() == Material.AIR){
			event.setCancelled(true);
			return;
		}
		Vector vec = event.getHook().getVelocity();
		vec.setX(0);
		vec.setY(0);
		vec.setZ(0);
		event.getHook().setVelocity(vec);
		Location loc = block.getLocation();
		loc.add(0.5, 1, 0.5);
		event.getHook().teleport(loc);
	}

	@EventHandler
	public void test2(PlayerFishEvent event){
//		if (event.getState() == PlayerFishEvent.State.FAILED_ATTEMPT) {
//			event.setCancelled(true);
//			return;
//		}
//		if (event.getState() != PlayerFishEvent.State.IN_GROUND) {
//			return;
//		}
		if (event.getState() == PlayerFishEvent.State.FISHING) {
			return;
		}
		Player player = event.getPlayer();
		Location playerLoc = player.getLocation();
		Location hookLoc = event.getHook().getLocation();
		hookLoc = hookLoc.subtract(playerLoc);
		Vector vec = new Vector(0,0,0);
		Vector add = new Vector(hookLoc.getX(), hookLoc.getY(), hookLoc.getZ());
		vec.add(add);
		vec.multiply(0.16);
		vec.add(player.getVelocity());
		Bukkit.getServer().getLogger().info(vec.toString());
		event.getPlayer().setVelocity(vec);

	}
}
