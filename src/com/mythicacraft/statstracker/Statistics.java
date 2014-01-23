package com.mythicacraft.statstracker;

import org.bukkit.entity.Player;

public class Statistics{

	private Player player;
	private int value = 1;
	private String world;
	private Stats statEnum;
	private Mobs mobsEnum;
	public enum Stats{MINUTES_PLAYED, DAMAGE_DEALT, DAMAGE_TAKEN, MOB_KILLS, ANIMALS_BRED,
		PLAYER_DEATHS, PLAYER_KILLS, BLOCKS_PLACED, BLOCKS_DESTROYED}
	public enum Mobs{CREEPER, SKELETON, ZOMBIE, SLIME, GHAST, ENDERMAN, CAVE_SPIDER,
		SILVERFISH, ENDERDRAGON, BLAZE, MAGMA_CUBE, BAT, WITCH, PIG, COW, SHEEP, CHICKEN,
		SQUID, HORSE}
	
	public Statistics(Player player, Stats stats, String world){
		this.player = player;
		this.statEnum = stats;
		this.world = world;
	}
	
	public Statistics(Player player, Stats stats, int value, String world){
		this.player = player;
		this.statEnum = stats;
		this.value = value;
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
	
	public String getWorld(){
		return world;
	}
	
	public Stats getStats(){
		return statEnum;
	}
	
	public Mobs getMobs(){
		return mobsEnum;
	}
}