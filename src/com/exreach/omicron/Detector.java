package com.exreach.omicron;

import org.bukkit.entity.*;
import org.bukkit.event.player.*;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.*;

public abstract class Detector
{
    public abstract String getName();
    
    public abstract void handleQuit(final Player p0);
    
    public abstract void handleJoin(final Player p0);
    
    public abstract String handleMove(final Player p0, final PlayerMoveEvent p1);
    
    public abstract String handlePlace(final Player p0, final BlockPlaceEvent p1);
    
    public abstract String handleBreak(final Player p0, final BlockBreakEvent p1);
    
    public abstract String handleCombat(final Player p0, final EntityDamageByEntityEvent p1);
}
