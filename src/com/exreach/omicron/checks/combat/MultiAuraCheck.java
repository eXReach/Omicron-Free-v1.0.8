package com.exreach.omicron.checks.combat;

import org.bukkit.entity.*;
import org.bukkit.*;
import java.util.*;
import com.exreach.omicron.logger.*;
import org.bukkit.event.*;
import com.exreach.omicron.checks.*;
import com.exreach.omicron.*;
import org.bukkit.event.entity.*;

public class MultiAuraCheck extends Check
{
    private static Map<Player, Map<Long, Location>> LastHit;
    
    static {
        MultiAuraCheck.LastHit = new HashMap<Player, Map<Long, Location>>();
    }
    
    @Override
    public String getEventCall() {
        return "EntityDamageByEntityEvent";
    }
    
    @Override
    public CheckResult performCheck(final User u, final Event ex) {
        if (!ex.getEventName().equalsIgnoreCase(this.getEventCall())) {
        	Omicron.getOmicron().console("§4There was an error with cenix!");
        	Omicron.getOmicron().console("§4BreakCheck performCheck was called on a non-applicable event!");
        	Omicron.getOmicron().console("§fRequired Event: " + this.getEventCall());
        	Omicron.getOmicron().console("§fEvent fired upon: " + ex.getEventName());
            return new CheckResult("MultiAura err.", true);
        }
        final EntityDamageByEntityEvent event = (EntityDamageByEntityEvent)ex;
        final Location hit = event.getEntity().getLocation();
        String ret = null;
        final Player p = u.getPlayer();
        if (event.getCause() == EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK) {
            return new CheckResult("Multi Aura", true);
        }
        if (MultiAuraCheck.LastHit.containsKey(p)) {
            final long time = System.currentTimeMillis() - MultiAuraCheck.LastHit.get(p).keySet().iterator().next();
            final double distance = MultiAuraCheck.LastHit.get(p).values().iterator().next().distance(hit);
            if (distance > 1.5 && time < 8L) {
                ret = "Multi Aura";
            }
        }
        final Map<Long, Location> R = new HashMap<Long, Location>();
        R.put(System.currentTimeMillis(), hit);
        MultiAuraCheck.LastHit.put(p, R);
        if (ret != null) {
            return new CheckResult("Multi Aura", false);
        }
        return new CheckResult("Multi Aura", true);
    }
}
