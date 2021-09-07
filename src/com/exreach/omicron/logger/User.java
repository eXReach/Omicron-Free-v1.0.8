package com.exreach.omicron.logger;

import org.bukkit.*;
import org.bukkit.block.*;
import org.bukkit.entity.*;

public class User
{
    private Player p;
    
    public User(final Player p) {
        this.p = p;
    }
    
    public Location LastNormalBoatLoc() {
        return PlayerLogger.getLogger().getLastRegularBoatLocation(this.p);
    }
    
    public long LastElytraFly() {
        return PlayerLogger.getLogger().getLastElytraFly(this.p);
    }
    
    public long LastFall() {
        return PlayerLogger.getLogger().getLastFall(this.p);
    }
    
    public long LastSlimeBounce() {
        return PlayerLogger.getLogger().getLastSlimeBounce(this.p);
    }
    
    public long LastTeleport() {
        return PlayerLogger.getLogger().getLastTeleport(this.p);
    }
    
    public long LastGroundTime() {
        return PlayerLogger.getLogger().getLastGroundTime(this.p);
    }
    
    public boolean isFalling() {
        return PlayerLogger.getLogger().isFalling(this.p);
    }
    
    public boolean isBouncing() {
        return PlayerLogger.getLogger().isBouncing(this.p);
    }
    
    public Player getPlayer() {
        return this.p;
    }
    
    public Block getBlock() {
        return this.p.getLocation().getBlock();
    }
    
    public Block getBlockBelow() {
        return this.getBlock().getRelative(BlockFace.DOWN);
    }
    
    public boolean InVehicle() {
        return this.p.isInsideVehicle();
    }
    
    public Entity getVehicle() {
        return this.p.getVehicle();
    }
    
    public Block getVehicleBlock() {
        return this.getVehicle().getLocation().getBlock();
    }
    
    public void teleport(final Location l) {
        this.p.teleport(l);
    }
    
    public void eject() {
        this.p.eject();
    }
    
    public Location LastGroundLocation() {
        return PlayerLogger.getLogger().getLastGroundLocation(this.p);
    }
    
    public Location LastRegularLocation() {
        return PlayerLogger.getLogger().getLastRegularMove(this.p);
    }
}
