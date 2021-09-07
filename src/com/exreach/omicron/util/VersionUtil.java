package com.exreach.omicron.util;

import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.potion.*;
import org.bukkit.enchantments.*;

public class VersionUtil
{
    public static String getVersion() {
        return Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
    }
    
    public static boolean isFlying(final Player p) {
        return p.isFlying() || p.isGliding() || p.hasPotionEffect(PotionEffectType.LEVITATION);
    }
    
    public static boolean hasEfficiency(final Player player) {
        return (player.hasPotionEffect(PotionEffectType.FAST_DIGGING) && player.getPotionEffect(PotionEffectType.FAST_DIGGING).getAmplifier() > 3) || (player.getItemInHand() != null && player.getItemInHand().containsEnchantment(Enchantment.DIG_SPEED));
    }
}
