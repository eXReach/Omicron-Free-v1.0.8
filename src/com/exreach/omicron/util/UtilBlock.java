package com.exreach.omicron.util;

import org.bukkit.util.*;
import org.bukkit.entity.*;
import java.util.*;
import org.bukkit.block.*;
import org.bukkit.*;

public class UtilBlock
{
    public static boolean canReallySeeEntity(final Player p, final LivingEntity e) {
        BlockIterator bl = new BlockIterator((LivingEntity)p, 7);
        boolean found = false;
        double md = 1.0;
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
    
    public static ArrayList<Block> getSurrounding(final Block block, final boolean diagonals) {
        final ArrayList<Block> blocks = new ArrayList<Block>();
        if (diagonals) {
            for (int x = -1; x <= 1; ++x) {
                for (int y = -1; y <= 1; ++y) {
                    for (int z = -1; z <= 1; ++z) {
                        if (x != 0 || y != 0 || z != 0) {
                            blocks.add(block.getRelative(x, y, z));
                        }
                    }
                }
            }
        }
        else {
            blocks.add(block.getRelative(BlockFace.UP));
            blocks.add(block.getRelative(BlockFace.DOWN));
            blocks.add(block.getRelative(BlockFace.NORTH));
            blocks.add(block.getRelative(BlockFace.SOUTH));
            blocks.add(block.getRelative(BlockFace.EAST));
            blocks.add(block.getRelative(BlockFace.WEST));
        }
        return blocks;
    }
    
    public static ArrayList<Block> getSurroundingIgnoreAir(final Block block, final boolean diagonals) {
        final ArrayList<Block> blocks = new ArrayList<Block>();
        if (diagonals) {
            for (int x = -1; x <= 1; ++x) {
                for (int y = -1; y <= 1; ++y) {
                    for (int z = -1; z <= 1; ++z) {
                        if ((x != 0 || y != 0 || z != 0) && block.getRelative(x, y, z).getType() != Material.AIR) {
                            blocks.add(block.getRelative(x, y, z));
                        }
                    }
                }
            }
        }
        else {
            if (block.getRelative(BlockFace.UP).getType() != Material.AIR) {
                blocks.add(block.getRelative(BlockFace.UP));
            }
            if (block.getRelative(BlockFace.DOWN).getType() != Material.AIR) {
                blocks.add(block.getRelative(BlockFace.DOWN));
            }
            if (block.getRelative(BlockFace.NORTH).getType() != Material.AIR) {
                blocks.add(block.getRelative(BlockFace.NORTH));
            }
            if (block.getRelative(BlockFace.SOUTH).getType() != Material.AIR) {
                blocks.add(block.getRelative(BlockFace.SOUTH));
            }
            if (block.getRelative(BlockFace.EAST).getType() != Material.AIR) {
                blocks.add(block.getRelative(BlockFace.EAST));
            }
            if (block.getRelative(BlockFace.WEST).getType() != Material.AIR) {
                blocks.add(block.getRelative(BlockFace.WEST));
            }
        }
        return blocks;
    }
    
    public static boolean contains(final Block b, final String meta) {
        return b.getType().toString().toLowerCase().contains(meta.toLowerCase());
    }
    
    public static Block getBlockAbove(final Player p) {
        return p.getLocation().getBlock().getRelative(BlockFace.UP).getRelative(BlockFace.UP);
    }
    
    public static boolean onStairs(final Player p) {
        final String m = p.getLocation().getBlock().getType().toString().toLowerCase();
        final String mu = p.getLocation().getBlock().getRelative(BlockFace.DOWN).getType().toString().toLowerCase();
        return m.contains("stair") || mu.contains("stair");
    }
    
    public static boolean onBlock(final Location loc) {
        double xMod = loc.getX() % 1.0;
        if (loc.getX() < 0.0) {
            ++xMod;
        }
        double zMod = loc.getZ() % 1.0;
        if (loc.getZ() < 0.0) {
            ++zMod;
        }
        int xMin = 0;
        int xMax = 0;
        int zMin = 0;
        int zMax = 0;
        if (xMod < 0.3) {
            xMin = -1;
        }
        if (xMod > 0.7) {
            xMax = 1;
        }
        if (zMod < 0.3) {
            zMin = -1;
        }
        if (zMod > 0.7) {
            zMax = 1;
        }
        for (int x = xMin; x <= xMax; ++x) {
            for (int z = zMin; z <= zMax; ++z) {
                if (loc.add((double)x, 0.0, (double)z).getBlock().getType() == Material.LILY_PAD) {
                    return true;
                }
                if (loc.add((double)x, -0.5, (double)z).getBlock().getType() != Material.AIR && !loc.add((double)x, -0.5, (double)z).getBlock().isLiquid()) {
                    return true;
                }
                final Material beneath = loc.add((double)x, -1.5, (double)z).getBlock().getType();
                if (loc.getY() % 0.5 == 0.0 && (beneath.toString().toLowerCase().contains("fence") || beneath.toString().toLowerCase().contains("rod") || beneath.toString().toLowerCase().contains("bamboo") || beneath.toString().toLowerCase().contains("wall"))) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public static boolean onBlock(final Player arg0) {
        return onBlock(arg0.getLocation());
    }
}
