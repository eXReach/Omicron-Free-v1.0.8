package com.exreach.omicron.checks.flight;

import org.bukkit.entity.*;
import com.exreach.omicron.logger.*;
import org.bukkit.event.*;
import com.exreach.omicron.checks.*;
import org.bukkit.event.player.*;
import org.bukkit.*;
import org.bukkit.potion.*;
import com.exreach.omicron.util.*;
import org.bukkit.block.*;
import com.exreach.omicron.*;
import java.util.*;

public class FloatCheck extends Check
{
    private static Map<Player, Integer> calls;
    
    static {
        FloatCheck.calls = new HashMap<Player, Integer>();
    }
    
    @Override
    public String getEventCall() {
        return "PlayerMoveEvent";
    }
    
    @Override
    public CheckResult performCheck(final User u, final Event e) {
        final PlayerMoveEvent event = (PlayerMoveEvent)e;
        if (!FloatCheck.calls.containsKey(u.getPlayer())) {
            FloatCheck.calls.put(u.getPlayer(), 0);
        }
        int cc = FloatCheck.calls.get(u.getPlayer());
        final Player p = u.getPlayer();
        if (p.isFlying() || p.isGliding() || p.getGameMode() == GameMode.CREATIVE || p.getGameMode() == GameMode.SPECTATOR || p.isInsideVehicle() || p.isSwimming() || p.getLocation().getBlock().isLiquid() || p.hasPotionEffect(PotionEffectType.LEVITATION)) {
            return new CheckResult("Fly", true);
        }
        final ArrayList<Block> around = UtilBlock.getSurroundingIgnoreAir(u.getBlock(), true);
        Boolean onlyliquid = true;
        for (final Block b : around) {
            if (!b.isLiquid()) {
                onlyliquid = false;
            }
        }
        if (onlyliquid) {
            return new CheckResult("Fly", true);
        }
        final Double mpx = event.getFrom().getY() - event.getTo().getY();
        if (event.getTo().getY() == event.getFrom().getY() && !u.getBlockBelow().isLiquid() && !p.isSwimming() && around.size() == 0) {
            ++cc;
        }
        else if (mpx <= 0.007 && !p.hasPotionEffect(PotionEffectType.SLOW_FALLING) && !u.getBlockBelow().isLiquid() && !p.getLocation().getBlock().getRelative(BlockFace.DOWN).isLiquid() && !p.isSwimming() && around.size() == 0) {
            ++cc;
        }
        else if (Omicron.getOmicron().getUser(p).isFalling() && mpx <= 0.07) {
            ++cc;
        }
        else if (cc > 0) {
            --cc;
        }
        if (cc > 7) {
            FloatCheck.calls.put(u.getPlayer(), 2);
            return new CheckResult("Fly", false);
        }
        FloatCheck.calls.put(u.getPlayer(), cc);
        return new CheckResult("Fly", true);
    }
}
