package com.mythicacraft.statstracker;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class Statistics{

	private Player player;
	private int value = 1;
	private String world;
	private Stats statEnum;
	private String mobName;
	public enum Stats{MINUTES_PLAYED, DAMAGE_DEALT, DAMAGE_TAKEN, MOB_KILLS,
		PLAYER_DEATHS, PLAYER_KILLS, BLOCKS_PLACED, BLOCKS_DESTROYED, BLOCKS_TRAVELED;
		
		public String toString(){
			String name = null;
			switch(this){
			case BLOCKS_DESTROYED:
				name = "BLOCKS_DESTROYED";
				break;
			case BLOCKS_PLACED:
				name = "BLOCKS_PLACED";
				break;
			case DAMAGE_DEALT:
				name = "DAMAGE_DEALT";
				break;
			case DAMAGE_TAKEN:
				name = "DAMAGE_TAKEN";
				break;
			case MINUTES_PLAYED:
				name = "MINUTES_PLAYED";
				break;
			case MOB_KILLS:
				name = "MOB_KILLS";
				break;
			case PLAYER_DEATHS:
				name = "PLAYER_DEATHS";
				break;
			case PLAYER_KILLS:
				name = "PLAYER_KILLS";
				break;
			case BLOCKS_TRAVELED:
				name = "BLOCKS_TRAVELED";
				break;
			default:
				break;
			}
			return name;
		}
	}
	
	public Statistics(Player player, Stats stats, String world){
		this.player = player;
		this.statEnum = stats;
		this.world = world;
	}
	
	public Statistics(Player player, Stats stats, EntityType type, String world){
		this.player = player;
		this.statEnum = stats;
		this.world = world;
		this.mobName = getMobType(type);
	}
	
	public Statistics(Player player, Stats stats, int value, String world){
		this.player = player;
		this.statEnum = stats;
		this.value = value;
		this.world = world;
	}
	
	public Statistics(Player player, Stats stats, double value, String world){
		this.player = player;
		this.statEnum = stats;
		this.value = (int) value;
		this.world = world;
	}

	public Player getPlayer(){
		return player;
	}
	
	public String getPlayerName(){
		return player.getName().toString();
	}
	
	public int getValue(){
		return value;
	}
	
	public void setValue(int value){
		this.value = value;
	}
	
	public String getWorld(){
		return world;
	}
	
	public Stats getType(){
		return statEnum;
	}
	
	public String getMobType(){
		return mobName;
	}
	
	private String getMobType(EntityType type){
		return type.toString();
	}
}