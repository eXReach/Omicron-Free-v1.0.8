package com.exreach.omicron.checks.combat;

import com.exreach.omicron.logger.*;
import org.bukkit.event.*;
import com.exreach.omicron.checks.*;
import org.bukkit.event.entity.*;

public class ReachCheck extends Check
{
    @Override
    public String getEventCall() {
        return "EntityDamageByEntityEvent";
    }
    
    @Override
    public CheckResult performCheck(final User u, final Event e) {
        final EntityDamageByEntityEvent event = (EntityDamageByEntityEvent)e;
        Double range = event.getEntity().getLocation().distance(event.getDamager().getLocation());
        final String rf = new StringBuilder().append(range).toString();
        try {
            range = Double.parseDouble(rf.substring(0, 4));
        }
        catch (Exception ex) {}
        if (range > 5.98) {
            return new CheckResult("Reach (dist: " + range + ")", false);
        }
        return new CheckResult("Reach", true);
    }
}
