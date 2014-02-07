package com.mythicacraft.statstracker.Utilities;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import com.mythicacraft.statstracker.Statistics;
import com.mythicacraft.statstracker.Statistics.Stats;

public class StatsDatabase extends DatabaseUtility{

	private final String worldColumns = "playerID int AUTO_INCREMENT KEY, PLAYER varchar(30)," +
			" MINUTES_PLAYED int, DAMAGE_DEALT int, DAMAGE_TAKEN int, MOB_KILLS int," +
			"PLAYER_DEATHS int, PLAYER_KILLS int, BLOCKS_PLACED int, BLOCKS_DESTROYED int," +
			"BLOCKS_TRAVELED int, BAT_KILLS int, BLAZE_KILLS int, CAVE_SPIDER_KILLS int," +
			"CHICKEN_KILLS int, COW_KILLS int, CREEPER_KILLS int, ENDER_DRAGON_KILLS int," +
			"ENDERMAN_KILLS int, GHAST_KILLS int, HORSE_KILLS int, IRON_GOLEM_KILLS int," +
			"MAGMA_CUBE_KILLS int, MUSHROOM_COW_KILLS int, OCELOT_KILLS int, PIG_KILLS int," +
			"PIG_ZOMBIE_KILLS int, SHEEP_KILLS int, SILVERFISH_KILLS int, SKELETON_KILLS int," +
			"SLIME_KILLS int, SNOWMAN_KILLS int, SPIDER_KILLS int, SQUID_KILLS int," +
			"VILLAGER_KILLS int, WITCH_KILLS int, WITHER_KILLS int, WOLF_KILLS";
	private FileConfiguration config;

	public StatsDatabase(FileConfiguration config) {
		super(config);
		this.config = config;
	}
	
	public void addStat(Statistics stats) throws SQLException{
		String table = "stats_" + getUniverse(stats.getPlayer().getWorld().toString());
		String column = (stats.getType() == Stats.MOB_KILLS) ? stats.getMobType():
			stats.getType().toString();
		String value = stats.getValue() + "";
		if(!exists(stats.getPlayer()))
			insertDefaults(stats.getPlayer().toString(), table);
		super.addToRow(table, "player", "'" + stats.getPlayer().getName() + "'", column, value);	
	}
	
	public void addStat(String player,  String universe, String stat, String value) throws SQLException{
		if(!exists(Bukkit.getPlayer(player)))
			insertDefaults(player, "stats_" + universe);
		super.addToRow("stats_" + universe, "player", "'" + player + "'", stat, value);	
	}
	
	public HashMap<String, Integer> getStatistics(Player player) throws SQLException{
		ResultSet results = super.getRows("stats_" + getUniverse(player.getWorld().toString()), "player = '" +
				player.getName().toString() + "'");
		HashMap<String, Integer> stats = new HashMap<String, Integer>(64);
		if(!results.next())
			return null;
		else{
			ResultSetMetaData resultData = results.getMetaData();
			for(int i=2;i<resultData.getColumnCount();i++){
				stats.put(resultData.getColumnName(i), results.getInt(i));
			}
		}
		return stats;
	}
	
	public int getStatistics(Player player, Stats stat) throws SQLException{
		ResultSet results = super.getRows("stats_" + getUniverse(player.getWorld().toString()), "player = '" +
				player.getName().toString() + "'");
		return Integer.parseInt(results.getString(stat.toString()));
	}
	
	public void createTables() throws SQLException{
		String[] universeSet = (String[]) getUniverse().toArray();
		for(String universe : universeSet){
			super.CreateTable("stats_" + universe, worldColumns);
		}
	}
	
	private boolean exists(Player player) throws SQLException{
		boolean exists = false;
		ResultSet results = super.getRows("stats_" + getUniverse(player.getWorld().toString()),
				"player_name = '" + player + "'");
		if(results.next())
			exists = true;
		super.close();
		return exists;
	}
	
	private void insertDefaults(String player, String table) throws SQLException{
		super.addRows(table, player + ",0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0," +
				"0,0,0,0,0,0,0,0,0,0,0");
	}
	
	/**
	 * Gets universe for given world
	 * 
	 * @param world		World to be evaluated
	 * @return			Returns universe parent of the given world
	 * 					Returns "default" if no universes specified
	 * 					Returns null if world is not tracked
	 */
	private String getUniverse(String world){
		ConfigurationSection cs = config.getConfigurationSection("Universes");
		 for(String universeName : cs.getKeys(false)) {
			 if(universeName.equalsIgnoreCase("default"))
				 return "default";
			 List<String> universeWorlds = cs.getStringList(universeName);
			 if(universeWorlds.contains(world))
				 return universeName;
		 }
		return null;
	}
	
	private Set<String> getUniverse(){
		return config.getConfigurationSection("Universes").getKeys(false);
	}
}
