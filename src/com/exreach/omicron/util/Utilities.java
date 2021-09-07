package com.exreach.omicron.util;

import org.bukkit.block.*;
import org.bukkit.*;
import java.util.*;
import org.bukkit.inventory.*;
import org.bukkit.entity.*;
import org.bukkit.util.*;
import org.bukkit.util.Vector;

public final class Utilities
{
    private static final List<Material> INSTANT_BREAK;
    private static final List<Material> FOOD;
    private static final List<Material> INTERACTABLE;
    private static List<Material> unsolidMaterials;
    private static List<Material> stepableMaterials;
    
    static {
        INSTANT_BREAK = new ArrayList<Material>();
        FOOD = new ArrayList<Material>();
        INTERACTABLE = new ArrayList<Material>();
        Utilities.unsolidMaterials = Arrays.asList(Material.AIR, Material.LEGACY_SIGN, Material.LEGACY_SIGN_POST, Material.TRIPWIRE, Material.TRIPWIRE_HOOK, Material.LEGACY_SUGAR_CANE_BLOCK, Material.LEGACY_LONG_GRASS, Material.FLOWER_POT, Material.LEGACY_YELLOW_FLOWER);
        Utilities.stepableMaterials = Arrays.asList(Material.LEGACY_STEP, Material.ACACIA_STAIRS, Material.BIRCH_STAIRS, Material.BIRCH_STAIRS, Material.BRICK_STAIRS, Material.COBBLESTONE_STAIRS, Material.DARK_OAK_STAIRS, Material.JUNGLE_STAIRS, Material.NETHER_BRICK_STAIRS, Material.QUARTZ_STAIRS, Material.RED_SANDSTONE_STAIRS, Material.SANDSTONE_STAIRS, Material.LEGACY_SMOOTH_STAIRS, Material.SPRUCE_STAIRS, Material.OAK_STAIRS, Material.STONE_SLAB);
    }
    
    public static boolean cantStandAtBetter(final Block block) {
        final Block otherBlock = block.getRelative(BlockFace.DOWN);
        final boolean center1 = otherBlock.getType() == Material.AIR;
        final boolean north1 = otherBlock.getRelative(BlockFace.NORTH).getType() == Material.AIR;
        final boolean east1 = otherBlock.getRelative(BlockFace.EAST).getType() == Material.AIR;
        final boolean south1 = otherBlock.getRelative(BlockFace.SOUTH).getType() == Material.AIR;
        final boolean west1 = otherBlock.getRelative(BlockFace.WEST).getType() == Material.AIR;
        final boolean northeast1 = otherBlock.getRelative(BlockFace.NORTH_EAST).getType() == Material.AIR;
        final boolean northwest1 = otherBlock.getRelative(BlockFace.NORTH_WEST).getType() == Material.AIR;
        final boolean southeast1 = otherBlock.getRelative(BlockFace.SOUTH_EAST).getType() == Material.AIR;
        final boolean southwest1 = otherBlock.getRelative(BlockFace.SOUTH_WEST).getType() == Material.AIR;
        final boolean overAir1 = otherBlock.getRelative(BlockFace.DOWN).getType() == Material.AIR || otherBlock.getRelative(BlockFace.DOWN).getType() == Material.WATER || otherBlock.getRelative(BlockFace.DOWN).getType() == Material.LAVA;
        return center1 && north1 && east1 && south1 && west1 && northeast1 && southeast1 && northwest1 && southwest1 && overAir1;
    }
    
    public static boolean cantStandAtSingle(final Block block) {
        final Block otherBlock = block.getRelative(BlockFace.DOWN);
        return otherBlock.getType() == Material.AIR;
    }
    
    public static boolean cantStandAtWater(final Block block) {
        final Block otherBlock = block.getRelative(BlockFace.DOWN);
        final boolean isHover = block.getType() == Material.AIR;
        final boolean n = otherBlock.getRelative(BlockFace.NORTH).getType() == Material.WATER;
        final boolean s = otherBlock.getRelative(BlockFace.SOUTH).getType() == Material.WATER;
        final boolean e = otherBlock.getRelative(BlockFace.EAST).getType() == Material.WATER;
        final boolean w = otherBlock.getRelative(BlockFace.WEST).getType() == Material.WATER;
        final boolean ne = otherBlock.getRelative(BlockFace.NORTH_EAST).getType() == Material.WATER;
        final boolean nw = otherBlock.getRelative(BlockFace.NORTH_WEST).getType() == Material.WATER;
        final boolean se = otherBlock.getRelative(BlockFace.SOUTH_EAST).getType() == Material.WATER;
        final boolean sw = otherBlock.getRelative(BlockFace.SOUTH_WEST).getType() == Material.WATER;
        return n && s && e && w && ne && nw && se && sw && isHover;
    }
    
    public static boolean canStandWithin(final Block block) {
        final boolean isSand = block.getType() == Material.SAND;
        final boolean isGravel = block.getType() == Material.GRAVEL;
        final boolean solid = block.getType().isSolid() && !block.getType().name().toLowerCase().contains("door") && !block.getType().name().toLowerCase().contains("fence") && !block.getType().name().toLowerCase().contains("bars") && !block.getType().name().toLowerCase().contains("sign");
        return !isSand && !isGravel && !solid;
    }
    
    public static Vector getRotation(final Location one, final Location two) {
        final double dx = two.getX() - one.getX();
        final double dy = two.getY() - one.getY();
        final double dz = two.getZ() - one.getZ();
        final double distanceXZ = Math.sqrt(dx * dx + dz * dz);
        final float yaw = (float)(Math.atan2(dz, dx) * 180.0 / 3.141592653589793) - 90.0f;
        final float pitch = (float)(-(Math.atan2(dy, distanceXZ) * 180.0 / 3.141592653589793));
        return new Vector(yaw, pitch, 0.0f);
    }
    
    public static double clamp180(double theta) {
        theta %= 360.0;
        if (theta >= 180.0) {
            theta -= 360.0;
        }
        if (theta < -180.0) {
            theta += 360.0;
        }
        return theta;
    }
    
    public static boolean cantStandAt(final Block block) {
        return !canStand(block) && cantStandClose(block) && cantStandFar(block);
    }
    
    public static boolean cantStandAtExp(final Location location) {
        return cantStandAt(new Location(location.getWorld(), fixXAxis(location.getX()), location.getY() - 0.01, (double)location.getBlockZ()).getBlock());
    }
    
    public static boolean cantStandClose(final Block block) {
        return !canStand(block.getRelative(BlockFace.NORTH)) && !canStand(block.getRelative(BlockFace.EAST)) && !canStand(block.getRelative(BlockFace.SOUTH)) && !canStand(block.getRelative(BlockFace.WEST));
    }
    
    public static boolean cantStandFar(final Block block) {
        return !canStand(block.getRelative(BlockFace.NORTH_WEST)) && !canStand(block.getRelative(BlockFace.NORTH_EAST)) && !canStand(block.getRelative(BlockFace.SOUTH_WEST)) && !canStand(block.getRelative(BlockFace.SOUTH_EAST));
    }
    
    public static boolean canStand(final Block block) {
        return !block.isLiquid() && block.getType() != Material.AIR;
    }
    
    public static boolean isFullyInWater(final Location player) {
        final double touchedX = fixXAxis(player.getX());
        return (!new Location(player.getWorld(), touchedX, player.getY(), (double)player.getBlockZ()).getBlock().isLiquid() && !new Location(player.getWorld(), touchedX, (double)Math.round(player.getY()), (double)player.getBlockZ()).getBlock().isLiquid()) || (new Location(player.getWorld(), touchedX, player.getY(), (double)player.getBlockZ()).getBlock().isLiquid() && new Location(player.getWorld(), touchedX, (double)Math.round(player.getY()), (double)player.getBlockZ()).getBlock().isLiquid());
    }
    
    public static double fixXAxis(final double x) {
        double touchedX = x;
        final double rem = touchedX - Math.round(touchedX) + 0.01;
        if (rem < 0.3) {
            touchedX = NumberConversions.floor(x) - 1;
        }
        return touchedX;
    }
    
    public static boolean isHoveringOverWater(final Location player, final int blocks) {
        for (int i = player.getBlockY(); i > player.getBlockY() - blocks; --i) {
            final Block newloc = new Location(player.getWorld(), (double)player.getBlockX(), (double)i, (double)player.getBlockZ()).getBlock();
            if (newloc.getType() != Material.AIR) {
                return newloc.isLiquid();
            }
        }
        return false;
    }
    
    public static boolean isHoveringOverWater(final Location player) {
        return isHoveringOverWater(player, 25);
    }
    
    public static boolean isInstantBreak(final Material m) {
        return Utilities.INSTANT_BREAK.contains(m);
    }
    
    public static boolean isFood(final Material m) {
        return Utilities.FOOD.contains(m);
    }
    
    public static boolean isInteractable(final Material m) {
        return m != null && Utilities.INTERACTABLE.contains(m);
    }
    
    public static boolean sprintFly(final Player player) {
        return player.isSprinting() || player.isFlying();
    }
    
    public static boolean isOnLilyPad(final Player player) {
        final Block block = player.getLocation().getBlock();
        final Material lily = Material.LILY_PAD;
        return block.getType() == lily || block.getRelative(BlockFace.NORTH).getType() == lily || block.getRelative(BlockFace.SOUTH).getType() == lily || block.getRelative(BlockFace.EAST).getType() == lily || block.getRelative(BlockFace.WEST).getType() == lily;
    }
    
    public static boolean isSubmersed(final Player player) {
        return player.getLocation().getBlock().isLiquid() && player.getLocation().getBlock().getRelative(BlockFace.UP).isLiquid();
    }
    
    public static boolean isInWater(final Player player) {
        return player.getLocation().getBlock().isLiquid() || player.getLocation().getBlock().getRelative(BlockFace.DOWN).isLiquid() || player.getLocation().getBlock().getRelative(BlockFace.UP).isLiquid();
    }
    
    public static boolean isInWeb(final Player player) {
        return player.getLocation().getBlock().getType() == Material.COBWEB || player.getLocation().getBlock().getRelative(BlockFace.DOWN).getType() == Material.COBWEB || player.getLocation().getBlock().getRelative(BlockFace.UP).getType() == Material.COBWEB;
    }
    
    public static boolean isClimbableBlock(final Block block) {
        return block.getType() == Material.VINE || block.getType() == Material.LADDER || block.getType() == Material.WATER || block.getType() == Material.WATER;
    }
    
    public static boolean isOnVine(final Player player) {
        return player.getLocation().getBlock().getType() == Material.VINE;
    }
    
    public static boolean isPlayerOnGround(final Player p) {
        return isPlayerLocationOnGround(p);
    }
    
    public static boolean isLocationOnGround(final Location loc) {
        final List<Material> materials = getMaterialsAround(loc.clone().add(0.0, -0.001, 0.0));
        for (final Material m : materials) {
            if (!isUnsolid(m) && m != Material.WATER && m != Material.WATER && m != Material.LAVA && m != Material.LAVA) {
                return true;
            }
        }
        return false;
    }
    
    public static boolean isPlayerLocationOnGround(final Player p) {
        return isLocationOnGround(p.getLocation());
    }
    
    public static boolean isUnderBlock(final Player p) {
        final Block blockAbove = p.getEyeLocation().getBlock().getRelative(BlockFace.UP);
        return blockAbove != null && !isUnsolid(blockAbove);
    }
    
    public static boolean isOnIce(final Player p, final boolean strict) {
        if (isPlayerOnGround(p) || strict) {
            final List<Material> materials = getMaterialsAround(p.getLocation().clone().add(0.0, -0.001, 0.0));
            return materials.contains(Material.ICE) || materials.contains(Material.PACKED_ICE);
        }
        final List<Material> m1 = getMaterialsAround(p.getLocation().clone().add(0.0, -1.0, 0.0));
        final List<Material> m2 = getMaterialsAround(p.getLocation().clone().add(0.0, -2.0, 0.0));
        return m1.contains(Material.ICE) || m1.contains(Material.PACKED_ICE) || m2.contains(Material.ICE) || m2.contains(Material.PACKED_ICE);
    }
    
    public static boolean isOnSteps(final Player p) {
        final List<Material> materials = getMaterialsAround(p.getLocation().clone().add(0.0, -0.001, 0.0));
        for (final Material m : materials) {
            if (isStepable(m)) {
                return true;
            }
        }
        return false;
    }
    
    public static boolean isGlidingWithElytra(final Player p) {
        final ItemStack chestplate = p.getInventory().getChestplate();
        return p.isGliding() && chestplate != null && chestplate.getType() == Material.ELYTRA;
    }
    
    public static List<Material> getMaterialsAround(final Location loc) {
        final List<Material> result = new ArrayList<Material>();
        result.add(loc.getBlock().getType());
        result.add(loc.clone().add(0.3, 0.0, -0.3).getBlock().getType());
        result.add(loc.clone().add(-0.3, 0.0, -0.3).getBlock().getType());
        result.add(loc.clone().add(0.3, 0.0, 0.3).getBlock().getType());
        result.add(loc.clone().add(-0.3, 0.0, 0.3).getBlock().getType());
        return result;
    }
    
    public static boolean isAround(final Location loc, final Material mat) {
        final Block blockDown = loc.getBlock().getRelative(BlockFace.DOWN);
        final ArrayList<Material> materials = new ArrayList<Material>();
        materials.add(blockDown.getType());
        materials.add(blockDown.getRelative(BlockFace.NORTH).getType());
        materials.add(blockDown.getRelative(BlockFace.NORTH_EAST).getType());
        materials.add(blockDown.getRelative(BlockFace.EAST).getType());
        materials.add(blockDown.getRelative(BlockFace.SOUTH_EAST).getType());
        materials.add(blockDown.getRelative(BlockFace.SOUTH).getType());
        materials.add(blockDown.getRelative(BlockFace.SOUTH_WEST).getType());
        materials.add(blockDown.getRelative(BlockFace.WEST).getType());
        materials.add(blockDown.getRelative(BlockFace.NORTH_WEST).getType());
        final Block blockDown2 = loc.getBlock().getRelative(BlockFace.DOWN, 2);
        materials.add(blockDown2.getType());
        materials.add(blockDown2.getRelative(BlockFace.NORTH).getType());
        materials.add(blockDown2.getRelative(BlockFace.NORTH_EAST).getType());
        materials.add(blockDown2.getRelative(BlockFace.EAST).getType());
        materials.add(blockDown2.getRelative(BlockFace.SOUTH_EAST).getType());
        materials.add(blockDown2.getRelative(BlockFace.SOUTH).getType());
        materials.add(blockDown2.getRelative(BlockFace.SOUTH_WEST).getType());
        materials.add(blockDown2.getRelative(BlockFace.WEST).getType());
        materials.add(blockDown2.getRelative(BlockFace.NORTH_WEST).getType());
        final Block block = loc.getBlock();
        materials.add(block.getType());
        materials.add(block.getRelative(BlockFace.NORTH).getType());
        materials.add(block.getRelative(BlockFace.NORTH_EAST).getType());
        materials.add(block.getRelative(BlockFace.EAST).getType());
        materials.add(block.getRelative(BlockFace.SOUTH_EAST).getType());
        materials.add(block.getRelative(BlockFace.SOUTH).getType());
        materials.add(block.getRelative(BlockFace.SOUTH_WEST).getType());
        materials.add(block.getRelative(BlockFace.WEST).getType());
        materials.add(block.getRelative(BlockFace.NORTH_WEST).getType());
        final Block blockUp = loc.getBlock().getRelative(BlockFace.UP);
        materials.add(blockUp.getType());
        materials.add(blockUp.getRelative(BlockFace.NORTH).getType());
        materials.add(blockUp.getRelative(BlockFace.NORTH_EAST).getType());
        materials.add(blockUp.getRelative(BlockFace.EAST).getType());
        materials.add(blockUp.getRelative(BlockFace.SOUTH_EAST).getType());
        materials.add(blockUp.getRelative(BlockFace.SOUTH).getType());
        materials.add(blockUp.getRelative(BlockFace.SOUTH_WEST).getType());
        materials.add(blockUp.getRelative(BlockFace.WEST).getType());
        materials.add(blockUp.getRelative(BlockFace.NORTH_WEST).getType());
        for (final Material m : materials) {
            if (m != mat) {
                return false;
            }
        }
        return true;
    }
    
    public static Location getPlayerStandOnBlockLocation(final Location locationUnderPlayer, final Material mat) {
        final Location b11 = locationUnderPlayer.clone().add(0.3, 0.0, -0.3);
        if (b11.getBlock().getType() != mat) {
            return b11;
        }
        final Location b12 = locationUnderPlayer.clone().add(-0.3, 0.0, -0.3);
        if (b12.getBlock().getType() != mat) {
            return b12;
        }
        final Location b13 = locationUnderPlayer.clone().add(0.3, 0.0, 0.3);
        if (b13.getBlock().getType() != mat) {
            return b13;
        }
        final Location b14 = locationUnderPlayer.clone().add(-0.3, 0.0, 0.3);
        if (b14.getBlock().getType() != mat) {
            return b14;
        }
        return locationUnderPlayer;
    }
    
    public static boolean isInBlock(final Player p, final Material block) {
        final Location loc = p.getLocation().add(0.0, 0.0, 0.0);
        return getPlayerStandOnBlockLocation(loc, block).getBlock().getType() == block;
    }
    
    public static boolean isOnWater(final Player p, final double depth) {
        final Location loc = p.getLocation().subtract(0.0, depth, 0.0);
        return getPlayerStandOnBlockLocation(loc, Material.WATER).getBlock().getType() == Material.WATER;
    }
    
    public static boolean isOnEntity(final Player p, final EntityType type) {
        for (final Entity e : p.getWorld().getNearbyEntities(p.getLocation(), 1.0, 1.0, 1.0)) {
            if (e.getType() == type && e.getLocation().getY() < p.getLocation().getY()) {
                return true;
            }
        }
        return false;
    }
    
    public static List<Material> getUnsolidMaterials() {
        return Utilities.unsolidMaterials;
    }
    
    public static boolean isUnsolid(final Material m) {
        return getUnsolidMaterials().contains(m);
    }
    
    public static boolean isUnsolid(final Block b) {
        return isUnsolid(b.getType());
    }
    
    public static List<Material> getStepableMaterials() {
        return Utilities.stepableMaterials;
    }
    
    public static boolean isStepable(final Material m) {
        return getStepableMaterials().contains(m);
    }
    
    public static boolean isStepable(final Block b) {
        return isStepable(b.getType());
    }
    
    public static boolean canReallySeeEntity(final Player p, final LivingEntity e) {
        BlockIterator bl = new BlockIterator((LivingEntity)p, 7);
        boolean found = false;
        double md = 3.25;
        if (e.getType() == EntityType.WITHER) {
            md = 9.0;
        }
        else if (e.getType() == EntityType.ENDERMAN) {
            md = 5.0;
        }
        else {
            md += e.getEyeHeight();
        }
        while (bl.hasNext()) {
            found = true;
            final double d = bl.next().getLocation().distanceSquared(e.getLocation());
            if (d <= md) {
                return true;
            }
        }
        bl = null;
        return !found;
    }
    
    public static LivingEntity getTarget(final Player player) {
        final int range = 8;
        final ArrayList<LivingEntity> livingE = new ArrayList<LivingEntity>();
        for (final Entity e : player.getNearbyEntities((double)range, (double)range, (double)range)) {
            if (e instanceof LivingEntity) {
                livingE.add((LivingEntity)e);
            }
        }
        LivingEntity target = null;
        final BlockIterator bItr = new BlockIterator((LivingEntity)player, range);
        double md = Double.MAX_VALUE;
        while (bItr.hasNext()) {
            final Block block = bItr.next();
            final int bx = block.getX();
            final int by = block.getY();
            final int bz = block.getZ();
            for (final LivingEntity e2 : livingE) {
                final Location loc = e2.getLocation();
                final double ex = loc.getX();
                final double ey = loc.getY();
                final double ez = loc.getZ();
                final double d = loc.distanceSquared(player.getLocation());
                if (e2.getType() == EntityType.HORSE) {
                    if (bx - 1.2 > ex || ex > bx + 2.2 || bz - 1.2 > ez || ez > bz + 2.2 || by - 2.5 > ey || ey > by + 4.5 || d >= md) {
                        continue;
                    }
                    md = d;
                    target = e2;
                }
                else {
                    if (bx - 0.8 > ex || ex > bx + 1.85 || bz - 0.8 > ez || ez > bz + 1.85 || by - 2.5 > ey || ey > by + 4.5 || d >= md) {
                        continue;
                    }
                    md = d;
                    target = e2;
                }
            }
        }
        livingE.clear();
        return target;
    }
}
