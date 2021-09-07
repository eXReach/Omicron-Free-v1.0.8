package com.exreach.omicron;

import net.md_5.bungee.api.*;
import java.io.*;
import com.google.common.io.*;
import org.bukkit.configuration.file.*;
import com.exreach.omicron.*;

public class Settings
{
    public static boolean ENABLED;
    public static boolean PUNISH;
    public static int PUNISH_OFFENSE_COUNT;
    public static String PUNISH_COMMAND;
    public static boolean LOG_REPORTS;
    public static boolean LOG_OFFENSES;
    public static boolean ABN;
    public static int OFFENSE_EXPIRE_TIME;
    public static double TPS_LAG_THRESHOLD;
    public static String PREFIX;
    public static String VARIABLE_COLOR;
    public static String SUSPICION_ALERT;
    public static String SUSPICION_ALERT_IGNORE_TPS;
    public static String SUSPICION_ALERT_IGNORE_PING;
    public static String REPORT_SAVED_ALERT;
    public static String TIMEZONE;
    
    static {
        Settings.ENABLED = true;
        Settings.PUNISH = false;
        Settings.PUNISH_OFFENSE_COUNT = 5;
        Settings.PUNISH_COMMAND = "";
        Settings.LOG_REPORTS = false;
        Settings.LOG_OFFENSES = true;
        Settings.ABN = true;
        Settings.OFFENSE_EXPIRE_TIME = 180;
        Settings.TPS_LAG_THRESHOLD = 17.5;
        Settings.PREFIX = "§8[§6OMICRON§8]§r";
        Settings.VARIABLE_COLOR = "§a";
        Settings.SUSPICION_ALERT = "[VARIABLE_COLOR] [DISPLAYNAME] §fполучил подозрение на §6[SUSPICION]§f. ([COUNT])";
        Settings.SUSPICION_ALERT_IGNORE_TPS = "[VARIABLE_COLOR] [DISPLAYNAME] §fполучил подозрение на §6[SUSPICION]§f, но это игнорируется из-за плохого TPS ([TPS])";
        Settings.SUSPICION_ALERT_IGNORE_PING = "[VARIABLE_COLOR] [DISPLAYNAME] §fполучил подозрение на §6[SUSPICION]§f, но это игнорируется из-за плохого TPS ([TPS])";
        Settings.REPORT_SAVED_ALERT = "§fОтчет за [VARIABLE_COLOR][DISPLAYNAME] §fсохранён ([VARIABLE_COLOR][REPORT_ID]§f)";
        Settings.TIMEZONE = "America/New_York";
    }
    
    public static void loadConfig() {
        final Omicron c = Omicron.getOmicron();
        c.reloadConfig();
        c.saveDefaultConfig();
        final FileConfiguration cf = c.getConfig();
        Settings.PREFIX = ChatColor.translateAlternateColorCodes('&', cf.getString("prefix"));
        c.console("§2Загрузка конфигурации...");
        try {
            Settings.ENABLED = Boolean.parseBoolean(cf.getString("enabled"));
        }
        catch (Exception e2) {
            c.console("§cНе удалось загрузить конфигурацию!");
            c.console("§c'enabled' не является допустимым логическим значением! По умолчанию" + Settings.ENABLED + ".");
        }
        try {
            Settings.PUNISH = Boolean.parseBoolean(cf.getString("punish"));
        }
        catch (Exception e2) {
            c.console("§cНе удалось загрузить конфигурацию!");
            c.console("§c'punish' не является действительным логическим значением! По умолчанию" + Settings.PUNISH + ".");
        }
        Settings.VARIABLE_COLOR = ChatColor.translateAlternateColorCodes('&', cf.getString("variable-color"));
        try {
            Settings.TPS_LAG_THRESHOLD = Double.parseDouble(cf.getString("tps-lag-threshold"));
        }
        catch (Exception e2) {
            c.console("§cНе удалось загрузить конфигурацию!");
            c.console("§c'tps-lag-threshold' неверный номер! По умолчанию" + Settings.TPS_LAG_THRESHOLD + ".");
        }
        try {
            if (!cf.contains("action-bar-notifications")) {
                cf.set("action-bar-notifications", (Object)true);
            }
            Settings.ABN = cf.getBoolean("action-bar-notifications");
        }
        catch (Exception e2) {
            c.console("§cНе удалось загрузить конфигурацию!");
            c.console("§c'action-bar-notifications' не является допустимым логическим значением! По умолчанию" + Settings.ABN + ".");
        }
        try {
            Settings.PUNISH_OFFENSE_COUNT = Integer.parseInt(cf.getString("punish-offense-count"));
        }
        catch (Exception e2) {
            c.console("§cНе удалось загрузить конфигурацию!");
            c.console("§c'punish-offense-count' неверный номер! По умолчанию" + Settings.PUNISH_OFFENSE_COUNT + ".");
        }
        Settings.PUNISH_COMMAND = ChatColor.translateAlternateColorCodes('&', cf.getString("punish-command"));
        try {
            Settings.LOG_REPORTS = Boolean.parseBoolean(cf.getString("log-reports"));
        }
        catch (Exception e2) {
            c.console("§cНе удалось загрузить конфигурацию!");
            c.console("§c'log-reports' не является допустимым логическим значением! По умолчанию" + Settings.LOG_REPORTS + ".");
        }
        try {
            Settings.OFFENSE_EXPIRE_TIME = Integer.parseInt(cf.getString("offense-expire-time"));
        }
        catch (Exception e2) {
            c.console("§cНе удалось загрузить конфигурацию!");
            c.console("§c'offense-expire-time' не является действительным целым числом! По умолчанию" + Settings.OFFENSE_EXPIRE_TIME + ".");
        }
        Settings.SUSPICION_ALERT = ChatColor.translateAlternateColorCodes('&', cf.getString("suspicion-alert"));
        Settings.SUSPICION_ALERT_IGNORE_TPS = ChatColor.translateAlternateColorCodes('&', cf.getString("suspicion-alert-ignore-tps"));
        Settings.REPORT_SAVED_ALERT = ChatColor.translateAlternateColorCodes('&', cf.getString("report-saved-alert"));
        Settings.SUSPICION_ALERT_IGNORE_PING = ChatColor.translateAlternateColorCodes('&', cf.getString("suspicion-alert-ignore-ping"));
        if (Settings.SUSPICION_ALERT_IGNORE_PING.toUpperCase().contains("[COUNT]")) {
            c.console("§eПредупреждение: в сообщении 'suspicion-alert-ignore-ping' определена переменная [COUNT], но в этом сообщении счетчик не активен, он будет пустым.");
        }
        Settings.TIMEZONE = cf.getString("timezone");
        final File offenses = new File(c.getDataFolder(), "offenses.txt");
        c.saveConfig();
        if (Settings.LOG_OFFENSES) {
            if (!offenses.exists()) {
                try {
                    offenses.createNewFile();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        else if (offenses.exists()) {
            final File oldoffenses = new File(c.getDataFolder(), "offenses.txt.old");
            if (oldoffenses.exists()) {
                oldoffenses.delete();
            }
            try {
                Files.copy(offenses, oldoffenses);
            }
            catch (IOException e3) {
                c.console("§cВозникла проблема с перемещением старого файла offenses.txt.");
            }
            offenses.delete();
        }
        c.console("§aКонфигурация загружена!");
    }
}
