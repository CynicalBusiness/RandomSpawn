package me.capit.RandomSpawn;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class RandomSpawn extends JavaPlugin{
	
	public SpawnEventHandler handler = null;
	public FileConfiguration pList = null;
	public File pListFile = null;
	
	@Override
	public void onEnable(){
		handler = new SpawnEventHandler(this);
		this.getServer().getPluginManager().registerEvents(handler, this);
		this.getLogger().info("RandomSpawn ready.");
	}
	
	@Override
	public void onDisable(){
		this.getLogger().info("RandomSpawn shut down.");
	}
	
	public void reloadPList(){
		if (pListFile==null){
			pListFile = new File(getDataFolder(), "players.yml");
		}
		pList = YamlConfiguration.loadConfiguration(pListFile);
		
		InputStream defStream = this.getResource("players.yml");
		if (defStream != null){
			YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defStream);
			pList.setDefaults(defConfig);
		}
	}
	
	public FileConfiguration getPList(){
		if (pList==null){
			reloadPList();
		}
		return pList;
	}
	
	public void savePList(){
		if (pList==null || pListFile==null){
			return;
		}
		try {
			getPList().save(pListFile);
		} catch (IOException e) {
			getLogger().log(Level.SEVERE, "Could not save config to " + pListFile, e);
		}
	}
	
	public void saveDefaultPList(){
		if (pListFile == null){
			pListFile = new File(getDataFolder(), "players.yml");
		}
		if (!pListFile.exists()){
			this.saveResource("players.yml", false);
		}
	}
}
