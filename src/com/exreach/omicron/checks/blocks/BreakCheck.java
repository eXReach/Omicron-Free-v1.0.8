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
import org.bukkit.block.*;

public class BreakCheck extends Check
{
    private static Map<Player, Map<Long, Integer>> BreakCount;
    
    static {
        BreakCheck.BreakCount = new HashMap<Player, Map<Long, Integer>>();
    }
    
    @Override
    public String getEventCall() {
        return "BlockBreakEvent";
    }
    
    @Override
    public CheckResult performCheck(final User u, final Event e) {
        if (!e.getEventName().equalsIgnoreCase(this.getEventCall())) {
        	Omicron.getOmicron().console("§4Произошла ошибка с OMICRON");
        	Omicron.getOmicron().console("§4BreakCheck executeCheck был вызван для неприменимого события!");
        	Omicron.getOmicron().console("§fОбязательное событие:" + this.getEventCall());
        	Omicron.getOmicron().console("§fСобытие произошло на:" + e.getEventName());
            return new CheckResult("BreakCheck err.", true);
        }
        final BlockBreakEvent event = (BlockBreakEvent)e;
        final Player p = u.getPlayer();
        Boolean instant = false;
        final Material m = event.getBlock().getType();
        try {
            if (m.getHardness() <= 0.1) {
                instant = true;
            }
        }
        catch (Exception ex) {}
        if (p.getGameMode() == GameMode.CREATIVE) {
            instant = true;
        }
        if (instant) {
            return new CheckResult("Impossible Break / Fast Break", true);
        }
        Integer Count = 1;
        if (BreakCheck.BreakCount.containsKey(p)) {
            if (BreakCheck.BreakCount.get(p).keySet().iterator().next() > System.currentTimeMillis()) {
                Count += BreakCheck.BreakCount.get(p).values().iterator().next();
            }
            else {
                BreakCheck.BreakCount.get(p).clear();
            }
        }
        if (!BreakCheck.BreakCount.containsKey(p)) {
            BreakCheck.BreakCount.put(p, new HashMap<Long, Integer>());
        }
        final Map<Long, Integer> R = new HashMap<Long, Integer>();
        if (BreakCheck.BreakCount.get(p).size() == 0) {
            R.put(System.currentTimeMillis() + 1000L, Count);
        }
        else {
            R.put(BreakCheck.BreakCount.get(p).keySet().iterator().next(), Count);
        }
        if (!BreakCheck.BreakCount.containsKey(p)) {
            BreakCheck.BreakCount.put(p, new HashMap<Long, Integer>());
        }
        BreakCheck.BreakCount.put(p, R);
        if (Count > 9 && !VersionUtil.hasEfficiency(p)) {
            event.setCancelled(true);
            return new CheckResult("Fast Break (" + Count + "bps)", false);
        }
        if (VersionUtil.hasEfficiency(p)) {
            return new CheckResult("Impossible Break / Fast Break", true);
        }
        final Location placed = event.getBlock().getLocation();
        final Block target = p.getTargetBlockExact(15);
        Boolean call = false;
        if (placed.distance(target.getLocation()) > 4.7) {
            call = true;
        }
        if (event.getBlock().getType() == Material.NETHERRACK) {
            call = false;
        }
        if (Bukkit.getPluginManager().getPlugin("GraviTree") != null && event.getBlock().getType().toString().contains("LOG")) {
            call = false;
        }
        if (call) {
            return new CheckResult("Impossible Break (Not in LoS)", false);
        }
        return new CheckResult("Impossible Break / Fast Break", true);
    }
}
