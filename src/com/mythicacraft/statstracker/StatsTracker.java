package com.mythicacraft.statstracker;

import java.io.File;
import java.util.LinkedList;
import java.util.Queue;
import java.util.logging.Logger;

import org.bukkit.plugin.java.JavaPlugin;

import com.mythicacraft.statstracker.Utilities.ConfigAccessor;

public class StatsTracker extends JavaPlugin{

	private static final Logger log = Logger.getLogger("Minecraft");
	
	private Queue<Statistics> liveQueue = new LinkedList<Statistics>();
	
	 public void onDisable() {
        log.info("[StatsTracker] Disabled!");
	 }
	 
	 public void onEnable() {
		 this.saveDefaultConfig();
		 
		 databaseSetup();
		 
		 getCommand("mystats").setExecutor(new Commands());
		 getCommand("stats").setExecutor(new Commands());
		 getServer().getPluginManager().registerEvents(new StatsListeners(this), this);
		 
		 log.info("[StatsTracker] Enabled!");
	 }
	
	 private void loadStatsData() {

         ConfigAccessor statsData = new ConfigAccessor("stats.yml");
         String pluginFolder = this.getDataFolder().getAbsolutePath() + File.separator + "data"; 
         (new File(pluginFolder)).mkdirs();
         File statsTrack = new File(pluginFolder + File.separator + "stats.yml");

         if (!statsTrack.exists()) {
                 log.info("No stats.yml, making one now...");
                 statsData.saveDefaultConfig();
                 log.info("Done!");
                 return;
         }
         log.info("stats.yml detected!");
	 }
	 
	 private void databaseSetup(){
		 if(this.getConfig().getBoolean("MySQL.Enabled") == false || this.getConfig().
				 getString("MySQL.username").equals("username")){
			 log.info("[StatsTracker] MySQL not being used, we recommend backing your statistics " +
				 "up in a database!");
			 loadStatsData();
		 }
		 else{
			 try{
				 //database connect/create table
			 } catch(Exception e){
				 log.info("[StatsTracker] Could not connect to the database. Please check your settings. " +
						 "Flatfile storage will be used.");
				 loadStatsData();
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
	 
}
