package com.mythicacraft.statstracker;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.mythicacraft.statstracker.Statistics.Stats;

public class StatsListeners implements Listener{

	StatsTracker plugin;
	EntityType[] trackedMobs = {EntityType.BAT, EntityType.BLAZE, EntityType.CAVE_SPIDER,
			EntityType.CHICKEN, EntityType.COW, EntityType.CREEPER, EntityType.ENDER_DRAGON,
			EntityType.ENDERMAN, EntityType.GHAST, EntityType.HORSE, EntityType.IRON_GOLEM,
			EntityType.MAGMA_CUBE, EntityType.MUSHROOM_COW, EntityType.OCELOT, EntityType.PIG,
			EntityType.PIG_ZOMBIE, EntityType.SHEEP, EntityType.SILVERFISH, EntityType.SKELETON,
			EntityType.SLIME, EntityType.SNOWMAN, EntityType.SPIDER, EntityType.SQUID,
			EntityType.VILLAGER, EntityType.WITCH, EntityType.WITHER, EntityType.WOLF};
	
	public StatsListeners(StatsTracker plugin){
		this.plugin = plugin;
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
    public void onBlockBreak(final BlockBreakEvent event){
		plugin.addToLiveQueue(new Statistics(event.getPlayer(), Stats.BLOCKS_DESTROYED,
				event.getPlayer().getWorld().toString()));
    }
	
	@EventHandler(priority = EventPriority.MONITOR)
    public void onBlockPlace(final BlockPlaceEvent event){
        plugin.addToLiveQueue(new Statistics(event.getPlayer(), Stats.BLOCKS_PLACED,
        		event.getPlayer().getWorld().toString()));
    }
	
	@EventHandler(priority = EventPriority.MONITOR)
    public void onDamageDealt(final EntityDamageByEntityEvent event){
		if(event.getDamager().getType() == EntityType.PLAYER){
	        plugin.addToLiveQueue(new Statistics((Player) event.getDamager(), Stats.DAMAGE_DEALT,
	        		event.getDamage(), event.getDamager().getWorld().toString()));
		}
        if(event.getEntityType() == EntityType.PLAYER){
	        plugin.addToLiveQueue(new Statistics((Player) event.getEntity(), Stats.DAMAGE_TAKEN,
	        		event.getDamage(), event.getEntity().getWorld().toString()));
        }
    }
	
	@EventHandler(priority = EventPriority.MONITOR)
    public void onMobDeath(final EntityDeathEvent event){
            if(event.getEntity().getKiller().getType() == EntityType.PLAYER &&
            		isTracked(event.getEntityType())){
    	        plugin.addToLiveQueue(new Statistics(event.getEntity().getKiller(), Stats.MOB_KILLS,
    	        		event.getEntityType(), event.getEntity().getWorld().toString()));
            }
    }
	
	@EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerDeath(final PlayerDeathEvent event){
		plugin.addToLiveQueue(new Statistics(event.getEntity().getPlayer(), Stats.PLAYER_DEATHS,
        		event.getEntity().getPlayer().getWorld().toString()));
		if(event.getEntity().getKiller().getType() == EntityType.PLAYER){
			plugin.addToLiveQueue(new Statistics(event.getEntity().getKiller(), Stats.PLAYER_KILLS,
					event.getEntity().getKiller().getWorld().toString()));
		}
    }
	
	@EventHandler(priority = EventPriority.MONITOR)
    public void onAnimalBred(final CreatureSpawnEvent event){
            //if SpawnReason.BREEDING
    }
	
	@EventHandler(priority = EventPriority.HIGH)
    public void onPlayerDisconnect(final PlayerQuitEvent event){
            //get online-time from /playerinfo in mythsentials
    }
	
	private boolean isTracked(EntityType type){
		for(int i=0; i<trackedMobs.length;i++){
			if(trackedMobs[i] == type){
				return true;
			}
		}
		return false;
	}
}
