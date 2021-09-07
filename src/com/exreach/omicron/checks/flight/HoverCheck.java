package com.exreach.omicron.checks.flight;

import org.bukkit.entity.*;
import com.exreach.omicron.logger.*;
import org.bukkit.event.*;
import com.exreach.omicron.checks.*;
import org.bukkit.block.*;
import org.bukkit.*;
import com.exreach.omicron.util.*;
import org.bukkit.potion.*;
import com.exreach.omicron.*;
import java.util.*;

public class HoverCheck extends Check
{
    private static Map<Player, Map<Integer, Double>> HoverTicks;
    
    static {
        HoverCheck.HoverTicks = new HashMap<Player, Map<Integer, Double>>();
    }
    
    @Override
    public String getEventCall() {
        return "PlayerMoveEvent";
    }
    
    @Override
    public CheckResult performCheck(final User u, final Event e) {
        final Player p = u.getPlayer();
        int Count = 0;
        if (HoverCheck.HoverTicks.containsKey(p)) {
            Count = HoverCheck.HoverTicks.get(p).keySet().iterator().next();
            if (!UtilBlock.onBlock(p) && p.getLocation().getY() == HoverCheck.HoverTicks.get(p).values().iterator().next()) {
                ++Count;
                for (final Block b : UtilBlock.getSurrounding(p.getLocation().getBlock(), true)) {
                    if (b.getType() != Material.AIR && p.getLocation().distance(b.getLocation()) < 1.25) {
                        Count = 0;
                    }
                }
            }
        }
        if (Count > 2 && !p.isInsideVehicle() && !VersionUtil.isFlying(p) && !p.hasPotionEffect(PotionEffectType.LEVITATION)) {
            final Map<Integer, Double> R = new HashMap<Integer, Double>();
            R.put(0, p.getLocation().getY());
            HoverCheck.HoverTicks.put(p, R);
            Omicron.getOmicron().addExemptionBlock(p, 20L);
            return new CheckResult("Fly", false);
        }
        final Map<Integer, Double> R = new HashMap<Integer, Double>();
        if (Count == 2) {
            Count = 1;
        }
        R.put(Count, p.getLocation().getY());
        HoverCheck.HoverTicks.put(p, R);
        return new CheckResult("Fly", true);
    }
}
