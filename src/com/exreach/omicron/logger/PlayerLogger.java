package com.exreach.omicron.logger;

import java.util.*;
import com.exreach.omicron.*;
import org.bukkit.event.*;
import org.bukkit.event.vehicle.*;
import org.bukkit.entity.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.*;
import org.bukkit.block.*;
import com.exreach.omicron.util.*;
import org.bukkit.*;

public class PlayerLogger implements Listener
{
    private static Map<Player, Long> LastElytraFly;
    private static Map<Player, Long> LastFly;
    private static Map<Player, Long> LastFall;
    private static Map<Player, Long> LastSlimeBounce;
    private static Map<Player, Long> LastTeleport;
    private static Map<Player, Long> LastGroundTime;
    private static List<Player> Falling;
    private static List<Player> Bouncing;
    private static Map<Player, Location> LastGroundLocation;
    private static Map<Player, Location> LastRegularMove;
    private static Map<Player, Location> LastRegularBoatLocation;
    private static PlayerLogger instance;
    
    static {
        PlayerLogger.LastElytraFly = new HashMap<Player, Long>();
        PlayerLogger.LastFly = new HashMap<Player, Long>();
        PlayerLogger.LastFall = new HashMap<Player, Long>();
        PlayerLogger.LastSlimeBounce = new HashMap<Player, Long>();
        PlayerLogger.LastTeleport = new HashMap<Player, Long>();
        PlayerLogger.LastGroundTime = new HashMap<Player, Long>();
        PlayerLogger.Falling = new ArrayList<Player>();
        PlayerLogger.Bouncing = new ArrayList<Player>();
        PlayerLogger.LastGroundLocation = new HashMap<Player, Location>();
        PlayerLogger.LastRegularMove = new HashMap<Player, Location>();
        PlayerLogger.LastRegularBoatLocation = new HashMap<Player, Location>();
        PlayerLogger.instance = null;
    }
    
    public PlayerLogger() {
        PlayerLogger.instance = this;
    }
    
    public static PlayerLogger getLogger() {
        return PlayerLogger.instance;
    }
    
    public void updateLastRegularBoatLocation(final Player p) {
        PlayerLogger.LastRegularBoatLocation.put(p, p.getVehicle().getLocation());
    }
    
    public void updateLastRegularMove(final Player p) {
        PlayerLogger.LastRegularMove.put(p, p.getLocation());
    }
    
    public Location getLastRegularMove(final Player p) {
        if (!PlayerLogger.LastRegularMove.containsKey(p)) {
            return null;
        }
        return PlayerLogger.LastRegularMove.get(p);
    }
    
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onGamemode(final PlayerGameModeChangeEvent event) {
        if (event.getNewGameMode() != GameMode.CREATIVE && event.getNewGameMode() != GameMode.SPECTATOR) {
            this.updateLastFly(event.getPlayer());
            Omicron.getOmicron().addExemption(event.getPlayer(), 3000L);
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEnter(final VehicleEnterEvent event) {
        if (event.getEntered() instanceof Player && event.getVehicle().getType() == EntityType.BOAT) {
            this.updateLastRegularBoatLocation((Player)event.getEntered());
        }
    }
    
    public Location getLastRegularBoatLocation(final Player p) {
        if (!PlayerLogger.LastRegularBoatLocation.containsKey(p)) {
            return null;
        }
        return PlayerLogger.LastRegularBoatLocation.get(p);
    }
    
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onDamage(final EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            final Player p = (Player)event.getEntity();
            if (event.getCause().toString().toUpperCase().contains("EXPLOSION")) {
                Omicron.getOmicron().addExemption(p, 3000L);
            }
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onFly(final PlayerToggleFlightEvent event) {
        if (!event.isFlying()) {
            this.updateLastFly(event.getPlayer());
            Omicron.getOmicron().addExemption(event.getPlayer(), 3000L);
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onTeleport(final PlayerTeleportEvent event) {
        this.updateLastTeleport(event.getPlayer());
        Omicron.getOmicron().addExemption(event.getPlayer(), 5000L);
    }
    
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onMove(final PlayerMoveEvent event) {
        final Player p = event.getPlayer();
        final Location f = event.getFrom();
        final Location t = event.getTo();
        if (UtilBlock.onBlock(p)) {
            this.updateLastGroundTime(p);
            if (!p.getLocation().getBlock().getRelative(BlockFace.DOWN).isLiquid()) {
                PlayerLogger.LastGroundLocation.put(p, p.getLocation());
            }
        }
        if (p.isInsideVehicle() && p.getVehicle().getLocation().getBlock().isLiquid()) {
            this.updateLastRegularBoatLocation(p);
        }
        if (t.getY() < f.getY() && !VersionUtil.isFlying(p)) {
            this.updateFalling(p, true);
            this.updateLastFall(p);
            this.updateBouncing(p, false);
            this.updateLastSlimeBounce(p);
        }
        else {
            if (p.isGliding()) {
                this.updateLastElytraFly(p);
            }
            this.updateFalling(p, false);
            if (this.getDown(f) == Material.SLIME_BLOCK) {
                this.updateBouncing(p, true);
                this.updateLastSlimeBounce(p);
            }
            else if (this.isBouncing(p) && this.getDown(t) == Material.AIR) {
                this.updateLastSlimeBounce(p);
            }
            else {
                this.updateBouncing(p, false);
            }
        }
    }
    
    private Material getDown(final Location p) {
        return p.getBlock().getRelative(BlockFace.DOWN).getType();
    }
    
    public Location getLastGroundLocation(final Player p) {
        if (PlayerLogger.LastGroundLocation.containsKey(p)) {
            return PlayerLogger.LastGroundLocation.get(p);
        }
        return null;
    }
    
    public Long getLastElytraFly(final Player p) {
        if (!PlayerLogger.LastElytraFly.containsKey(p)) {
            return -1L;
        }
        return System.currentTimeMillis() - PlayerLogger.LastElytraFly.get(p);
    }
    
    public Long getLastFly(final Player p) {
        if (!PlayerLogger.LastFly.containsKey(p)) {
            return -1L;
        }
        return System.currentTimeMillis() - PlayerLogger.LastFly.get(p);
    }
    
    public Long getLastFall(final Player p) {
        if (!PlayerLogger.LastFall.containsKey(p)) {
            return -1L;
        }
        return System.currentTimeMillis() - PlayerLogger.LastFall.get(p);
    }
    
    public Long getLastSlimeBounce(final Player p) {
        if (!PlayerLogger.LastSlimeBounce.containsKey(p)) {
            return -1L;
        }
        return System.currentTimeMillis() - PlayerLogger.LastSlimeBounce.get(p);
    }
    
    public Long getLastTeleport(final Player p) {
        if (!PlayerLogger.LastTeleport.containsKey(p)) {
            return -1L;
        }
        return System.currentTimeMillis() - PlayerLogger.LastTeleport.get(p);
    }
    
    public Long getLastGroundTime(final Player p) {
        if (!PlayerLogger.LastGroundTime.containsKey(p)) {
            return -1L;
        }
        return System.currentTimeMillis() - PlayerLogger.LastGroundTime.get(p);
    }
    
    public Boolean isFalling(final Player p) {
        return PlayerLogger.Falling.contains(p);
    }
    
    public Boolean isBouncing(final Player p) {
        return PlayerLogger.Bouncing.contains(p);
    }
    
    private void updateLastElytraFly(final Player p) {
        PlayerLogger.LastElytraFly.put(p, System.currentTimeMillis());
    }
    
    private void updateLastFly(final Player p) {
        PlayerLogger.LastFly.put(p, System.currentTimeMillis());
    }
    
    private void updateLastFall(final Player p) {
        PlayerLogger.LastFall.put(p, System.currentTimeMillis());
    }
    
    private void updateLastSlimeBounce(final Player p) {
        PlayerLogger.LastSlimeBounce.put(p, System.currentTimeMillis());
    }
    
    private void updateLastTeleport(final Player p) {
        PlayerLogger.LastTeleport.put(p, System.currentTimeMillis());
    }
    
    private void updateLastGroundTime(final Player p) {
        PlayerLogger.LastGroundTime.put(p, System.currentTimeMillis());
    }
    
    private void updateFalling(final Player p, final Boolean f) {
        if (f) {
            if (!PlayerLogger.Falling.contains(p)) {
                PlayerLogger.Falling.add(p);
            }
            return;
        }
        if (PlayerLogger.Falling.contains(p)) {
            PlayerLogger.Falling.remove(p);
        }
    }
    
    private void updateBouncing(final Player p, final Boolean b) {
        if (b) {
            if (!PlayerLogger.Bouncing.contains(p)) {
                PlayerLogger.Bouncing.add(p);
            }
            return;
        }
        if (PlayerLogger.Bouncing.contains(p)) {
            PlayerLogger.Bouncing.remove(p);
        }
    }
}
