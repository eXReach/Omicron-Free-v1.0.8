package com.exreach.omicron.util;

import java.util.*;
import java.text.*;
import org.bukkit.entity.*;
import org.bukkit.*;
import org.bukkit.util.*;
import org.bukkit.util.Vector;

public class UtilMath
{
    public static Random random;
    
    static {
        UtilMath.random = new Random();
    }
    
    public static double trim(final int degree, final double d) {
        String format = "#.#";
        for (int i = 1; i < degree; ++i) {
            format = String.valueOf(format) + "#";
        }
        final DecimalFormat twoDForm = new DecimalFormat(format);
        return Double.valueOf(twoDForm.format(d));
    }
    
    public static int r(final int i) {
        return UtilMath.random.nextInt(i);
    }
    
    public static int getRandom(final int low, final int high) {
        return low + (int)(Math.random() * (high - low + 1));
    }
    
    public static double offset2d(final Entity a, final Entity b) {
        return offset2d(a.getLocation().toVector(), b.getLocation().toVector());
    }
    
    public static double offset2d(final Location a, final Location b) {
        return offset2d(a.toVector(), b.toVector());
    }
    
    public static double offset2d(final Vector a, final Vector b) {
        a.setY(0);
        b.setY(0);
        return a.subtract(b).length();
    }
    
    public static double offset(final Entity a, final Entity b) {
        return offset(a.getLocation().toVector(), b.getLocation().toVector());
    }
    
    public static double offset(final Location a, final Location b) {
        return offset(a.toVector(), b.toVector());
    }
    
    public static double offset(final Vector a, final Vector b) {
        return a.subtract(b).length();
    }
}
