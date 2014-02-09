package com.mythicacraft.statstracker.Utilities;

import java.io.*;
import java.util.List;
import java.util.Queue;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.scheduler.BukkitRunnable;

import com.mythicacraft.statstracker.Statistics;
import com.mythicacraft.statstracker.StatsTracker;
import com.mythicacraft.statstracker.Statistics.Stats;

public class StatsSyncTask extends BukkitRunnable{

	private StatsTracker plugin;
	private Queue<Statistics> statsQueue;
	
	public StatsSyncTask(StatsTracker plugin){
		this.plugin = plugin;
		this.statsQueue = plugin.getLiveQueue();
		plugin.emptyQueue();
	}
	@Override
	public void run() {
			saveTempQueue();
		if(plugin.sqlEnabled){
			StatsDatabase db = new StatsDatabase(plugin.getConfig());
			try{
				db.connect();
				while(!statsQueue.isEmpty()){
						db.addStat(statsQueue.remove());
				}
				db.close();
			} catch(Exception e){
				ConfigAccessor tempData = loadTempData();
				while(!statsQueue.isEmpty())
					addStats(tempData, statsQueue.remove());
				tempData.saveConfig();
			}
		}
		else{
			ConfigAccessor statsData = new ConfigAccessor("stats.yml");
			while(!statsQueue.isEmpty()){
				addStats(statsData, statsQueue.remove());
			}
			statsData.saveConfig();
		}
	}
	
	private ConfigAccessor loadTempData() {
        ConfigAccessor tempData = new ConfigAccessor("tempStorage.yml");
        String pluginFolder = plugin.getDataFolder().getAbsolutePath() + File.separator + "data"; 
        (new File(pluginFolder)).mkdirs();
        File tempTrack = new File(pluginFolder + File.separator + "tempStorage.yml");
        if (!tempTrack.exists()) {
                tempData.saveDefaultConfig();
                return tempData;
        }
        return tempData;
	 }
	
	private void addStats(ConfigAccessor config, Statistics stat){
		String typeName = stat.getType().toString();
		String universe = getUniverse(stat.getWorld());
		if(universe == null)
			return;
		if(stat.getType() == Stats.MOB_KILLS)
			typeName = stat.getMobType();
		config.getConfig().set(stat.getPlayer().getName().toString() + "." + universe + "." + typeName,
				stat.getValue());
	}
	
	private String getUniverse(String world){
		ConfigurationSection cs = plugin.getConfig().getConfigurationSection("Universes");
		 for(String universeName : cs.getKeys(false)) {
			 if(universeName.equalsIgnoreCase("default"))
				 return "default";
			 List<String> universeWorlds = cs.getStringList(universeName);
			 if(universeWorlds.contains(world))
				 return universeName;
		 }
		return null;
	}

	private void saveTempQueue(){
		StatsDatabase db = null;
		ConfigAccessor statsData = new ConfigAccessor("stats.yml");
		ConfigAccessor tempData = new ConfigAccessor("tempStorage.yml");
		File temp = new File(plugin.getDataFolder().getAbsolutePath() + File.separator + "data" +
				File.separator + "tempStorage.yml");
		if(plugin.sqlEnabled)
			db = new StatsDatabase(plugin.getConfig());
		try{
			for(String player : tempData.getConfig().getKeys(false)){
				ConfigurationSection playerCS = tempData.getConfig().getConfigurationSection(player);
				for(String universeName : playerCS.getKeys(false)) {
					ConfigurationSection universeCS = playerCS.getConfigurationSection(universeName);
					for(String statName : universeCS.getKeys(false)){
						if(db != null){
								db.addStat(player, universeName, statName, universeCS.getInt(statName) + "");
						}
						else{
							int updateValue = universeCS.getInt(statName);
							Integer oldValue = statsData.getConfig().getInt(player + "." + universeName +
									"." + statName);
							updateValue = (oldValue == null) ? updateValue : (updateValue + oldValue);
							statsData.getConfig().set(player + "." + universeName + "." + statName,
									updateValue);
						}
					}
				}
			 }
			@SuppressWarnings("resource")
			OutputStream out = new FileOutputStream(temp);
			out.write((new String()).getBytes());
			tempData.saveConfig();
		} catch(Exception e){StatsTracker.log.info("[StatsTracker] Something went wrong when saving temp data. Keeping temp.");}
	}
}
