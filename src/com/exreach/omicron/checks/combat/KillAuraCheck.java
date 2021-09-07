package com.exreach.omicron.checks.combat;

import org.bukkit.entity.*;
import java.util.*;
import com.exreach.omicron.logger.*;
import org.bukkit.event.*;
import com.exreach.omicron.checks.*;
import org.bukkit.event.entity.*;
import org.bukkit.*;

public class KillAuraCheck extends Check
{
    private static Map<Player, Long> lastCheck;
    
    static {
        KillAuraCheck.lastCheck = new HashMap<Player, Long>();
    }
    
    @Override
    public String getEventCall() {
        return "EntityDamageByEntityEvent";
    }
    
    @Override
    public CheckResult performCheck(final User u, final Event ex) {
        final Player p = u.getPlayer();
        if (p.isBlocking()) {
            return new CheckResult("Impossible Fight (Combat while Blocking)", false);
        }
        if (p.isSleeping()) {
            return new CheckResult("Impossible Fight (Combat while Sleeping)", false);
        }
        if (p.isDead()) {
            return new CheckResult("Impossible Fight (Combat while Dead)", false);
        }
        if (!KillAuraCheck.lastCheck.containsKey(p)) {
            KillAuraCheck.lastCheck.put(p, System.currentTimeMillis() - 500L);
        }
        final EntityDamageByEntityEvent event = (EntityDamageByEntityEvent)ex;
        final Location hit = event.getEntity().getLocation();
        final Location possible = event.getDamager().getLocation().getDirection().multiply(-2.2).toLocation(event.getDamager().getWorld());
        if (hit.distance(possible) <= 1.1) {
            return new CheckResult("Kill Aura", false);
        }
        return new CheckResult("Kill Aura", true);
    }
}
