package me.capit.RandomSpawn;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class SpawnEventHandler implements Listener {
	RandomSpawn plugin;
	
	public SpawnEventHandler(RandomSpawn plugin){
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onPlayerLogon(final PlayerJoinEvent e){
		new BukkitRunnable(){

			@Override
			public void run() {
				plugin.getLogger().info("Player joined. Checking hasJoined data...");
				Player p = e.getPlayer();
				List<String> plist = plugin.getPList().getStringList("players");
				if (!plist.contains(p.getName())){
					plugin.getLogger().info("Player is new, creating new random location...");
					//newPlayerStuff(p);
		
					Location spawn = getRandomSpawnLocation(p, new Location(p.getWorld(),0,255,0), true);
					plugin.getLogger().info("Created "+spawn+" for location.");
					p.teleport(spawn);
					plugin.getLogger().info("Teleported player and applied effect.");
					p.addPotionEffect(PotionEffectType.REGENERATION.createEffect(4, 6), true);
					
					plist.add(p.getName());
					plugin.getPList().set("players", plist);
					plugin.savePList();
				}
			}
		}.runTaskLater(plugin, 1L);
	}
	
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent e){
		Player p = e.getEntity();
		e.setDeathMessage(ChatColor.DARK_RED+p.getName()+" was slain.");
	}
	
	@EventHandler
	public void onPlayerRespawn(final PlayerRespawnEvent e){
		new BukkitRunnable(){

			@Override
			public void run() {
				plugin.getLogger().info("Player died. Checking bed data...");
				Player p = e.getPlayer();
				if (e.isBedSpawn()==false){
					plugin.getLogger().info("Player has no bed, creating new random location...");
					Location spawn = getRandomSpawnLocation(p, new Location(p.getWorld(),0,255,0), true);
					plugin.getLogger().info("Created "+spawn+" for location.");
					p.teleport(spawn);
					plugin.getLogger().info("Teleported player and applied effect.");
					p.addPotionEffect(PotionEffectType.REGENERATION.createEffect(4, 6), true);
				}
			}
			
		}.runTaskLater(plugin, 1L);
			
	}
	
	public Location getRandomSpawnLocation(Player p, Location loc, boolean doOverride){
		if (!p.hasPermission("randomspawn.norandspawn")){
			if (doOverride){
				plugin.getLogger().info("Override true. New coordinates generated.");
				double randX = (Math.random()*12000)-6000;
				double randZ = (Math.random()*12000)-6000;
				loc.setX(randX);
				loc.setZ(randZ);
			}
			if (loc.getBlock().getType().isSolid()){
				plugin.getLogger().info("Got a good spawn location for "+p.getName()+"!");
				loc.setY(loc.getY()+2);
				if (loc.getBlock().getType()==Material.WATER || loc.getBlock().getType()==Material.LAVA){
					return getRandomSpawnLocation(p, loc, true);
				}
				return loc;
			} else {
				//plugin.getLogger().info("Got bad spawn location for "+p.getName()+": Non-Solid");
				loc.setY(loc.getY()-1);
				return getRandomSpawnLocation(p, loc, false);
			}
		} else {
			plugin.getLogger().info("Player had norandspawn permission!");
			return p.getLocation().getWorld().getSpawnLocation();
		}
	}
}
