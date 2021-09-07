package com.exreach.omicron.checks.movement;

import org.bukkit.entity.*;
import org.bukkit.event.*;
import com.exreach.omicron.checks.*;
import com.exreach.omicron.*;
import org.bukkit.event.player.*;
import org.bukkit.*;
import com.exreach.omicron.logger.*;
import org.bukkit.block.*;
import org.bukkit.enchantments.*;
import org.bukkit.potion.*;
import com.exreach.omicron.util.*;
import java.util.*;

public class SpeedCheck extends Check
{
    private HashMap<Player, Map<Integer, Long>> SpeedTicks;
    
    public SpeedCheck() {
        this.SpeedTicks = new HashMap<Player, Map<Integer, Long>>();
    }
    
    @Override
    public String getEventCall() {
        return "PlayerMoveEvent";
    }
    
    @Override
    public CheckResult performCheck(final User u, final Event ex) {
        if (!ex.getEventName().equalsIgnoreCase(this.getEventCall())) {
            Omicron.getOmicron().console("§4There was an error with cenix!");
            Omicron.getOmicron().console("§4BreakCheck performCheck was called on a non-applicable event!");
            Omicron.getOmicron().console("§fRequired Event: " + this.getEventCall());
            Omicron.getOmicron().console("§fEvent fired upon: " + ex.getEventName());
            return new CheckResult("SpeedCheck err.", true);
        }
        final PlayerMoveEvent event = (PlayerMoveEvent)ex;
        Integer Count = 0;
        final Player p = u.getPlayer();
        double Offset = 0.0;
        double Limit = 0.74;
        if (this.SpeedTicks.containsKey(p)) {
            if (event.getFrom().getY() > event.getTo().getY()) {
                Offset = UtilMath.offset2d(event.getFrom(), event.getTo());
            }
            else {
                Offset = UtilMath.offset(event.getFrom(), event.getTo());
            }
            if (VersionUtil.isFlying(p) || u.isBouncing() || u.isFalling() || u.getPlayer().isInsideVehicle()) {
                return new CheckResult("Speed", true);
            }
            if (UtilBlock.onBlock(p)) {
                Limit = 0.56;
            }
            if (UtilBlock.onStairs(p)) {
                Limit = 0.77;
            }
            if (Limit < 0.77 && UtilBlock.getBlockAbove(p).getType() != Material.AIR) {
                Limit = 0.77;
            }
            if (PlayerLogger.getLogger().getLastElytraFly(p) != -1L && PlayerLogger.getLogger().getLastElytraFly(p) < 150L) {
                return new CheckResult("Speed", true);
            }
            Boolean ice = false;
            if (u.getBlockBelow().getType().toString().toLowerCase().contains("ice")) {
                ice = true;
            }
            for (final Block bb : UtilBlock.getSurrounding(u.getBlockBelow(), true)) {
                if (bb.getType().toString().toLowerCase().contains("ice") && bb.getRelative(0, 3, 0).getType() != Material.AIR) {
                    Limit = 1.2;
                    ice = true;
                    break;
                }
            }
            if (ice && (p.isGliding() || u.LastElytraFly() <= 650L)) {
                Limit += 0.23;
            }
            if (p.isSwimming()) {
                Limit += 0.15;
            }
            if (p.getInventory().getBoots() != null && p.getInventory().getBoots().containsEnchantment(Enchantment.DEPTH_STRIDER)) {
                Limit += 0.08 * (p.getInventory().getBoots().getEnchantmentLevel(Enchantment.DEPTH_STRIDER) + 1);
            }
            for (final PotionEffect e : p.getActivePotionEffects()) {
                if (e.getType().equals((Object)PotionEffectType.SPEED)) {
                    if (UtilBlock.onStairs(p)) {
                        Limit += 0.14 * (e.getAmplifier() + 1);
                    }
                    else if (p.isOnGround()) {
                        Limit += 0.08 * (e.getAmplifier() + 1);
                    }
                    else {
                        Limit += 0.04 * (e.getAmplifier() + 1);
                    }
                }
                else {
                    if (!e.getType().equals((Object)PotionEffectType.JUMP)) {
                        continue;
                    }
                    Limit += 0.18 * (e.getAmplifier() + 1);
                }
            }
            if (Offset > Limit && !UtilTime.elapsed(this.SpeedTicks.get(p).entrySet().iterator().next().getValue(), 200L)) {
                Count = this.SpeedTicks.get(p).entrySet().iterator().next().getKey() + 1;
            }
            else {
                Count = 0;
            }
        }
        Boolean call = false;
        if (Count > 3) {
            final Map<Integer, Long> R = new HashMap<Integer, Long>();
            R.put(3, System.currentTimeMillis());
            this.SpeedTicks.put(p, R);
            if (!UtilBlock.onBlock(p)) {
                if (event.getTo().getY() == event.getFrom().getY()) {
                    call = true;
                }
                else if (event.getTo().getY() > event.getFrom().getY()) {
                    call = true;
                }
                else {
                    call = true;
                }
            }
            call = true;
            if (call) {
                if (!UtilBlock.onBlock(p)) {
                    return new CheckResult("Fly", false);
                }
                return new CheckResult("Speed", false);
            }
        }
        else {
            final Map<Integer, Long> R = new HashMap<Integer, Long>();
            R.put(Count, System.currentTimeMillis());
            this.SpeedTicks.put(p, R);
        }
        return new CheckResult("Speed", true);
    }
}
