package com.exreach.omicron;

import java.util.*;

public class Reports
{
    public static int saveReport(final List<String> r) {
        final int id = getNextReportID();
        Omicron.getOmicron().getConfig().set("Reports." + id, (Object)r);
        Omicron.getOmicron().saveConfig();
        return id;
    }
    
    private static int getNextReportID() {
        final int id = new Random().nextInt(9000) + 1000;
        if (Omicron.getOmicron().getConfig().contains("Reports." + id)) {
            return getNextReportID();
        }
        return id;
    }
}
