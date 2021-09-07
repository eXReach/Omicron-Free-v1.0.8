package com.exreach.omicron.checks.flight;

import org.bukkit.entity.*;
import com.exreach.omicron.logger.*;
import org.bukkit.event.*;
import com.exreach.omicron.checks.*;
import org.bukkit.potion.*;
import com.exreach.omicron.util.*;
import org.bukkit.block.*;
import org.bukkit.*;
import java.util.*;

public class RiseCheck extends Check
{
    private static Map<Player, Map<Integer, Double>> RiseTicks;
    
    static {
        RiseCheck.RiseTicks = new HashMap<Player, Map<Integer, Double>>();
    }
    
    @Override
    public String getEventCall() {
        return "PlayerMoveEvent";
    }
    
    @Override
    public CheckResult performCheck(final User u, final Event e) {
        int Count = 0;
        final Player p = u.getPlayer();
        Boolean bouncecheck = false;
        if (u.LastSlimeBounce() != -1L && u.LastSlimeBounce() < 1000L) {
            bouncecheck = true;
        }
        if (u.isBouncing() || bouncecheck || (p.isInsideVehicle() && p.getVehicle().getType().toString().contains("HORSE"))) {
            return new CheckResult("Fly", true);
        }
        if (u.getPlayer().hasPotionEffect(PotionEffectType.JUMP) || u.getPlayer().hasPotionEffect(PotionEffectType.SLOW_FALLING) || p.hasPotionEffect(PotionEffectType.LEVITATION)) {
            return new CheckResult("Fly", true);
        }
        if (RiseCheck.RiseTicks.containsKey(p) && p.getLocation().getY() > RiseCheck.RiseTicks.get(p).values().iterator().next() && !VersionUtil.isFlying(p)) {
            boolean nearBlocks = false;
            for (final Block block : UtilBlock.getSurrounding(p.getLocation().getBlock(), true)) {
                if (block.getType() != Material.AIR) {
                    nearBlocks = true;
                    break;
                }
            }
            if (!nearBlocks) {
                Count = RiseCheck.RiseTicks.get(p).keySet().iterator().next() + 1;
            }
        }
        if (Count > 4) {
            final Map<Integer, Double> R = new HashMap<Integer, Double>();
            R.put(2, p.getLocation().getY());
            RiseCheck.RiseTicks.put(p, R);
            return new CheckResult("Fly", false);
        }
        final Map<Integer, Double> R = new HashMap<Integer, Double>();
        R.put(Count, p.getLocation().getY());
        RiseCheck.RiseTicks.put(p, R);
        return new CheckResult("Fly", true);
    }
}
