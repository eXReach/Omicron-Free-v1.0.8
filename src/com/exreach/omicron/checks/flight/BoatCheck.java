package com.exreach.omicron.checks.flight;

import com.exreach.omicron.logger.*;
import org.bukkit.event.*;
import com.exreach.omicron.checks.*;
import org.bukkit.event.player.*;
import com.exreach.omicron.util.*;
import com.exreach.omicron.*;
import org.bukkit.entity.*;
import org.bukkit.plugin.*;
import org.bukkit.*;

public class BoatCheck extends Check
{
    @Override
    public String getEventCall() {
        return "PlayerMoveEvent";
    }
    
    @Override
    public CheckResult performCheck(final User u, final Event e) {
        if (u.getBlock().isLiquid() || u.getBlock().getType().toString().toLowerCase().contains("carpet")) {
            return new CheckResult("Boat Fly", true);
        }
        if (u.InVehicle() && u.getVehicle().getType() == EntityType.BOAT) {
            final PlayerMoveEvent ev = (PlayerMoveEvent)e;
            if (u.getVehicleBlock().getType() == Material.AIR && !UtilBlock.onBlock(u.getVehicle().getLocation()) && UtilBlock.getSurroundingIgnoreAir(u.getVehicleBlock(), true).size() == 0) {
                final Location LastSafe = u.LastNormalBoatLoc();
                if (ev.getTo().getY() < ev.getFrom().getY()) {
                    return new CheckResult("Boat Fly", true);
                }
                if (LastSafe != null) {
                	Omicron.getOmicron().addExemptionBlock(u, 5);
                    final Entity v = u.getVehicle();
                    u.eject();
                    u.teleport(LastSafe);
                    v.teleport(LastSafe);
                    Bukkit.getScheduler().runTaskLater((Plugin)Omicron.getOmicron().getPlugin(), (Runnable)new Runnable() {
                        @Override
                        public void run() {
                            v.setPassenger((Entity)u.getPlayer());
                        }
                    }, 1L);
                }
                else {
                    u.eject();
                }
                return new CheckResult("Boat Fly", false);
            }
        }
        return new CheckResult("Boat Fly", true);
    }
}
