package com.mythicacraft.statstracker;

import java.sql.SQLException;
import java.util.HashMap;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import com.mythicacraft.statstracker.Utilities.*;

public class StatsRetriever {

	StatsTracker plugin;
	FileConfiguration config;
	StatsDatabase db;
	ConfigAccessor statsFile = new ConfigAccessor("stats.yml");
	
	public StatsRetriever(StatsTracker stats, FileConfiguration config){
		this.plugin = stats;
		this.config = config;
		db = new StatsDatabase(config);
	}
	
	public HashMap<String, Integer> getStats(Player player, String universe) throws SQLException{
		HashMap<String, Integer> statsMap = new HashMap<String, Integer>(32);
		if(plugin.sqlEnabled){
			statsMap = db.getStatistics(player, universe);
		}
		else{
			ConfigurationSection playerCS = statsFile.getConfig().getConfigurationSection(player.getName().toString());
			for(String universes : playerCS.getKeys(false)){
				if(!universes.equalsIgnoreCase(universe))
					continue;
				ConfigurationSection universeCS = statsFile.getConfig().getConfigurationSection(universes);
				for(String key : universeCS.getKeys(false)){
					statsMap.put(key, universeCS.getInt(key));
				}
			}
		}
		return statsMap;
	}
	
	
	
}
