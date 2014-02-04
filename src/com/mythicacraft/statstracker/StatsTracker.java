package com.mythicacraft.statstracker;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.logging.Logger;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

import com.mythicacraft.statstracker.Statistics.Stats;
import com.mythicacraft.statstracker.Utilities.ConfigAccessor;
import com.mythicacraft.statstracker.Utilities.StatsDatabase;

public class StatsTracker extends JavaPlugin{

	private static final Logger log = Logger.getLogger("Minecraft");
	
	private Queue<Statistics> liveQueue = new LinkedList<Statistics>();
	
	public boolean sqlEnabled = true;
	
	 public void onDisable() {
		 if(!liveQueue.isEmpty()){
			 log.info("[StatsTracker] Storing queued statistics...");
			 loadStatsData("tempStorage.yml");
			 ConfigAccessor tempData = new ConfigAccessor("tempStorage.yml");
			 while(!liveQueue.isEmpty()){
				 addStats(tempData, liveQueue.remove());
			 }
			 tempData.saveConfig();
		 }
		 log.info("[StatsTracker] Disabled!");      
	 }
	 
	 public void onEnable() {
		 this.saveDefaultConfig();
		 
		 databaseSetup();
		 
		 getCommand("mystats").setExecutor(new Commands());
		 getCommand("stats").setExecutor(new Commands());
		 getServer().getPluginManager().registerEvents(new StatsListeners(this), this);
		 
		 /*
		  * start scheduler, scheduler checks for temp data before continuing with other stuff
		  * 
		  * 
		  */

		 log.info("[StatsTracker] Enabled!");
	 }
	
	 private void loadStatsData(String filename) {
         ConfigAccessor statsData = new ConfigAccessor(filename);
         String pluginFolder = this.getDataFolder().getAbsolutePath() + File.separator + "data"; 
         (new File(pluginFolder)).mkdirs();
         File statsTrack = new File(pluginFolder + File.separator + filename);

         if (!statsTrack.exists()) {
                 log.info("No " + filename + ", making one now...");
                 statsData.saveDefaultConfig();
                 return;
         }
         log.info(filename + " detected!");
	 }
	 
	 private void databaseSetup(){
		 if(this.getConfig().getBoolean("MySQL.Enabled") == false || this.getConfig().
				 getString("MySQL.username").equals("username")){
			 log.info("[StatsTracker] MySQL not being used, we recommend backing your statistics " +
				 "up in a database!");
			 sqlEnabled = false;
			 loadStatsData("stats.yml");
		 }
		 else{
			 try{
				 StatsDatabase db = new StatsDatabase(getConfig());
				 db.createTables();
			 } catch(Exception e){
				 log.info("[StatsTracker] Could not connect to the database. Please check your settings. " +
						 "Flatfile storage will be used.");
				 loadStatsData("stats.yml");
			 }
		 }
	 }
	
	 public Queue<Statistics> getLiveQueue(){
		 return liveQueue;
	 }
	 
	 public void emptyQueue(){
		 liveQueue.clear();
	 }
	 
	 public void addToLiveQueue(Statistics stats){
		 liveQueue.add(stats);
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
		ConfigurationSection cs = getConfig().getConfigurationSection("Universes");
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
		 if(sqlEnabled){
			 //tempStorage to database
		 }
		 else{
			 //tempStorage to stats.yml
		 }
	}
	 
}
