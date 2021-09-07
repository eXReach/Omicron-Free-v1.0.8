package com.exreach.omicron.checks.blocks;

import org.bukkit.entity.*;
import java.util.*;
import com.exreach.omicron.logger.*;
import org.bukkit.event.*;
import com.exreach.omicron.checks.*;
import com.exreach.omicron.*;
import org.bukkit.event.block.*;
import com.exreach.omicron.util.*;
import org.bukkit.*;

public class PlaceCheck extends Check
{
    private static Map<Player, Map<Long, Integer>> PlaceCount;
    
    static {
        PlaceCheck.PlaceCount = new HashMap<Player, Map<Long, Integer>>();
    }
    
    @Override
    public String getEventCall() {
        return "BlockPlaceEvent";
    }
    
    @Override
    public CheckResult performCheck(final User u, final Event e) {
        if (!e.getEventName().equalsIgnoreCase(this.getEventCall())) {
        	Omicron.getOmicron().console("§4FATAL ERROR WITH OMICRON");
        	Omicron.getOmicron().console("§4BreakCheck executeCheck был вызван для неприменимого события!");
        	Omicron.getOmicron().console("§fОбязательное событие: " + this.getEventCall());
        	Omicron.getOmicron().console("§fСобытие произошло: " + e.getEventName());
            return new CheckResult("PlaceCheck err.", true);
        }
        final BlockPlaceEvent event = (BlockPlaceEvent)e;
        final Player p = u.getPlayer();
        int Count = 1;
        if (PlaceCheck.PlaceCount.containsKey(p)) {
            if (PlaceCheck.PlaceCount.get(p).keySet().iterator().next() > System.currentTimeMillis()) {
                Count += PlaceCheck.PlaceCount.get(p).values().iterator().next();
            }
            else {
                PlaceCheck.PlaceCount.get(p).clear();
            }
        }
        if (event.getBlockPlaced().getType() == Material.SCAFFOLDING) {
            return new CheckResult("Impossible Place", true);
        }
        if (!PlaceCheck.PlaceCount.containsKey(p)) {
            PlaceCheck.PlaceCount.put(p, new HashMap<Long, Integer>());
        }
        final Map<Long, Integer> R = new HashMap<Long, Integer>();
        if (PlaceCheck.PlaceCount.get(p).size() == 0) {
            R.put(System.currentTimeMillis() + 1000L, Count);
        }
        else {
            R.put(PlaceCheck.PlaceCount.get(p).keySet().iterator().next(), Count);
        }
        PlaceCheck.PlaceCount.put(p, R);
        if (Count > 10 && !VersionUtil.hasEfficiency(p)) {
            event.setCancelled(true);
            return new CheckResult("Fast Place (" + Count + "bps)", false);
        }
        final Location placed = event.getBlockPlaced().getLocation();
        Boolean call = false;
        if (placed.distance(p.getTargetBlockExact(10).getLocation()) > 2.5) {
            call = true;
        }
        if (call) {
            return new CheckResult("Impossible Place (Not in LoS)", false);
        }
        return new CheckResult("Impossible Place / Fast Place", true);
    }
}
