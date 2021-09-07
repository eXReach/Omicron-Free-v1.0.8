package com.exreach.omicron.util;

import java.util.*;
import org.bukkit.*;
import org.bukkit.potion.*;
import org.bukkit.enchantments.*;

public class NMS_1_9_PLUS
{
    static final EnumSet<Material> MOVE_UP_BLOCKS_1_9;
    static final PotionEffectType LEVITATION;
    static final Enchantment FROST_WALKER;
    
    static {
        MOVE_UP_BLOCKS_1_9 = EnumSet.of(Material.PURPUR_STAIRS, Material.OAK_STAIRS, Material.COBBLESTONE_STAIRS, Material.BRICK_STAIRS, Material.STONE_BRICK_STAIRS, Material.NETHER_BRICK_STAIRS, Material.SANDSTONE_STAIRS, Material.SPRUCE_STAIRS, Material.BIRCH_STAIRS, Material.JUNGLE_STAIRS, Material.QUARTZ_STAIRS, Material.ACACIA_STAIRS, Material.DARK_OAK_STAIRS, Material.PRISMARINE_STAIRS, Material.PRISMARINE_BRICK_STAIRS, Material.DARK_PRISMARINE_STAIRS, Material.RED_SANDSTONE_STAIRS);
        LEVITATION = PotionEffectType.LEVITATION;
        FROST_WALKER = Enchantment.FROST_WALKER;
    }
}
