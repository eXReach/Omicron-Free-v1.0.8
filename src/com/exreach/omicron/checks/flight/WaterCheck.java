package com.exreach.omicron.checks.flight;

import com.exreach.omicron.logger.*;
import org.bukkit.event.*;
import com.exreach.omicron.checks.*;
import org.bukkit.*;
import com.exreach.omicron.*;
import org.bukkit.block.*;
import org.bukkit.entity.*;
import com.exreach.omicron.util.*;
import java.util.*;

public class WaterCheck extends Check
{
    private static Map<Player, Map<Integer, Double>> FloatTicks;
    
    static {
        WaterCheck.FloatTicks = new HashMap<Player, Map<Integer, Double>>();
    }
    
    @Override
    public String getEventCall() {
        return "PlayerMoveEvent";
    }
    
    @Override
    public CheckResult performCheck(final User u, final Event ev) {
        final Player p = u.getPlayer();
        int Count = 0;
        if (WaterCheck.FloatTicks.containsKey(p)) {
            Count = WaterCheck.FloatTicks.get(p).keySet().iterator().next() + 1;
            if (this.isWaterWalking(p, ev)) {
                ++Count;
            }
            else {
                --Count;
            }
        }
        if (Count > 15) {
            final Map<Integer, Double> R = new HashMap<Integer, Double>();
            R.put(0, p.getLocation().getY());
            WaterCheck.FloatTicks.put(p, R);
            final Map<Integer, Double> RE = new HashMap<Integer, Double>();
            int nc = Count;
            if (--nc < 0) {
                nc = 0;
            }
            RE.put(5, p.getLocation().getY());
            WaterCheck.FloatTicks.put(p, RE);
            return new CheckResult("Water Walk", false);
        }
        final Map<Integer, Double> RE2 = new HashMap<Integer, Double>();
        int nc2 = Count;
        if (--nc2 < 0) {
            nc2 = 0;
        }
        if (p.getLocation().getBlock().getRelative(BlockFace.UP).getType() == Material.WATER && p.getLocation().getBlock().getType() == Material.WATER) {
            nc2 = 0;
        }
        RE2.put(nc2, p.getLocation().getY());
        WaterCheck.FloatTicks.put(p, RE2);
        return new CheckResult("Water Walk", true);
    }
    
    public boolean isWaterWalking(final Player p, final Event ev) {
        final User u = Omicron.getOmicron().getUser(p);
        final List<Block> b = UtilBlock.getSurroundingIgnoreAir(u.getBlock(), true);
        boolean sneak = false;
        boolean haswater = false;
        for (final Block be : b) {
            if (be.getType() == Material.WATER) {
                haswater = true;
            }
            if (be.getLocation().getY() < u.getBlock().getY() && be.getType() != Material.WATER && be.getType() != Material.KELP) {
                sneak = true;
            }
        }
        if (!haswater) {
            return false;
        }
        double vel = p.getVelocity().getY();
        if (vel < 0.0) {
            vel += vel + vel;
        }
        return (p.getLocation().getBlock().getRelative(BlockFace.UP).getType() != Material.WATER || p.getLocation().getBlock().getType() != Material.WATER) && (!sneak && (new StringBuilder(String.valueOf(p.getLocation().getY())).toString().contains(".00250") || new StringBuilder(String.valueOf(p.getLocation().getY())).toString().contains(".99")) && !Utilities.isOnEntity(p, EntityType.BOAT) && !Utilities.isOnLilyPad(p) && !Utilities.isOnSteps(p) && !Utilities.isOnVine(p) && !Utilities.isGlidingWithElytra(p) && !u.getBlock().getType().toString().contains("CARPET"));
    }
}
