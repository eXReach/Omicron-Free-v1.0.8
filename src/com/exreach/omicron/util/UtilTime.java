package com.exreach.omicron.util;

import java.text.*;
import java.util.*;
import java.util.regex.*;

public class UtilTime
{
    public static final String DATE_FORMAT_NOW = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_FORMAT_DAY = "yyyy-MM-dd";
    
    public static String now() {
        final Calendar cal = Calendar.getInstance();
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(cal.getTime());
    }
    
    public static long parseDateDiff(final String time, final boolean future) {
        final Matcher m = Pattern.compile("(?:([0-9]+)\\s*y[a-z]*[,\\s]*)?(?:([0-9]+)\\s*mo[a-z]*[,\\s]*)?(?:([0-9]+)\\s*w[a-z]*[,\\s]*)?(?:([0-9]+)\\s*d[a-z]*[,\\s]*)?(?:([0-9]+)\\s*h[a-z]*[,\\s]*)?(?:([0-9]+)\\s*m[a-z]*[,\\s]*)?(?:([0-9]+)\\s*(?:s[a-z]*)?)?", 2).matcher(time);
        int years = 0;
        int months = 0;
        int weeks = 0;
        int days = 0;
        int hours = 0;
        int minutes = 0;
        int seconds = 0;
        boolean found = false;
        while (m.find()) {
            if (m.group() != null) {
                if (m.group().isEmpty()) {
                    continue;
                }
                for (int i = 0; i < m.groupCount(); ++i) {
                    if (m.group(i) != null && !m.group(i).isEmpty()) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    continue;
                }
                if (m.group(1) != null && !m.group(1).isEmpty()) {
                    years = Integer.parseInt(m.group(1));
                }
                if (m.group(2) != null && !m.group(2).isEmpty()) {
                    months = Integer.parseInt(m.group(2));
                }
                if (m.group(3) != null && !m.group(3).isEmpty()) {
                    weeks = Integer.parseInt(m.group(3));
                }
                if (m.group(4) != null && !m.group(4).isEmpty()) {
                    days = Integer.parseInt(m.group(4));
                }
                if (m.group(5) != null && !m.group(5).isEmpty()) {
                    hours = Integer.parseInt(m.group(5));
                }
                if (m.group(6) != null && !m.group(6).isEmpty()) {
                    minutes = Integer.parseInt(m.group(6));
                }
                if (m.group(7) != null && !m.group(7).isEmpty()) {
                    seconds = Integer.parseInt(m.group(7));
                    break;
                }
                break;
            }
        }
        if (!found) {
            return 0L;
        }
        final Calendar c = new GregorianCalendar();
        if (years > 0) {
            c.add(1, years * (future ? 1 : -1));
        }
        if (months > 0) {
            c.add(2, months * (future ? 1 : -1));
        }
        if (weeks > 0) {
            c.add(3, weeks * (future ? 1 : -1));
        }
        if (days > 0) {
            c.add(5, days * (future ? 1 : -1));
        }
        if (hours > 0) {
            c.add(11, hours * (future ? 1 : -1));
        }
        if (minutes > 0) {
            c.add(12, minutes * (future ? 1 : -1));
        }
        if (seconds > 0) {
            c.add(13, seconds * (future ? 1 : -1));
        }
        final Calendar max = new GregorianCalendar();
        max.add(1, 10);
        if (c.after(max)) {
            return max.getTimeInMillis();
        }
        return c.getTimeInMillis();
    }
    
    public static String when(final long time) {
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(time);
    }
    
    public static String date() {
        final Calendar cal = Calendar.getInstance();
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(cal.getTime());
    }
    
    public static int toms(final int seconds) {
        return seconds * 1000;
    }
    
    public static String left(final long epoch) {
        return convertString(epoch - System.currentTimeMillis(), 1, TimeUnit.FIT);
    }
    
    public static String since(final long epoch) {
        return convertString(System.currentTimeMillis() - epoch, 1, TimeUnit.FIT);
    }
    
    public static double convert(final long time, final int trim, TimeUnit type) {
        if (type == TimeUnit.FIT) {
            if (time < 60000L) {
                type = TimeUnit.SECONDS;
            }
            else if (time < 3600000L) {
                type = TimeUnit.MINUTES;
            }
            else if (time < 86400000L) {
                type = TimeUnit.HOURS;
            }
            else {
                type = TimeUnit.DAYS;
            }
        }
        if (type == TimeUnit.DAYS) {
            return UtilMath.trim(trim, time / 8.64E7);
        }
        if (type == TimeUnit.HOURS) {
            return UtilMath.trim(trim, time / 3600000.0);
        }
        if (type == TimeUnit.MINUTES) {
            return UtilMath.trim(trim, time / 60000.0);
        }
        if (type == TimeUnit.SECONDS) {
            return UtilMath.trim(trim, time / 1000.0);
        }
        return UtilMath.trim(trim, (double)time);
    }
    
    public static String MakeStr(final long time) {
        return convertString(time, 1, TimeUnit.FIT);
    }
    
    public static String MakeStr(final long time, final int trim) {
        return convertString(time, trim, TimeUnit.FIT);
    }
    
    public static String convertString(final long time, final int trim, TimeUnit type) {
        if (time == -1L) {
            return "Permanent";
        }
        if (type == TimeUnit.FIT) {
            if (time < 60000L) {
                type = TimeUnit.SECONDS;
            }
            else if (time < 3600000L) {
                type = TimeUnit.MINUTES;
            }
            else if (time < 86400000L) {
                type = TimeUnit.HOURS;
            }
            else {
                type = TimeUnit.DAYS;
            }
        }
        if (type == TimeUnit.DAYS) {
            return String.valueOf(UtilMath.trim(trim, time / 8.64E7)) + " Days";
        }
        if (type == TimeUnit.HOURS) {
            return String.valueOf(UtilMath.trim(trim, time / 3600000.0)) + " Hours";
        }
        if (type == TimeUnit.MINUTES) {
            return String.valueOf(UtilMath.trim(trim, time / 60000.0)) + " Minutes";
        }
        if (type == TimeUnit.SECONDS) {
            return String.valueOf(UtilMath.trim(trim, time / 1000.0)) + " Seconds";
        }
        return String.valueOf(UtilMath.trim(trim, (double)time)) + " Milliseconds";
    }
    
    public static boolean elapsed(final long from, final long required) {
        return System.currentTimeMillis() - from > required;
    }
    
    public enum TimeUnit
    {
        FIT("FIT", 0), 
        DAYS("DAYS", 1), 
        HOURS("HOURS", 2), 
        MINUTES("MINUTES", 3), 
        SECONDS("SECONDS", 4), 
        MILLISECONDS("MILLISECONDS", 5);
        
        private TimeUnit(final String name, final int ordinal) {
        }
    }
}
