package com.exreach.omicron.command;

import com.exreach.omicron.*;
import org.bukkit.*;
import com.exreach.omicron.util.*;
import org.bukkit.entity.*;
import java.util.*;
import com.exreach.omicron.fwk.*;

public class CenixCmd
{
    @Command(name = "Omicron")
    public void onCmd(final CommandArgs a) {
        final Player p = a.getPlayer();
        final String[] args = a.getArgs();
        final Omicron omicron = Omicron.getOmicron();
        if (args.length == 0) {
            omicron.sendMessage(p, "§rЗапуск версии Omicron " + Settings.VARIABLE_COLOR + omicron.getDescription().getVersion().replaceAll("\\[", "").replaceAll("\\]", "") + "§f Плагин разработан :" + Settings.VARIABLE_COLOR + omicron.getDescription().getAuthors().get(0).replaceAll("\\[", "").replaceAll("\\]", "") + "§f.");
            if (p.hasPermission("omicron.admin")) {
                omicron.sendMessage(p, "§fИспользуйте" + Settings.VARIABLE_COLOR + "/omicron help§f чтобы посмотреть все доступные команды.");
            }
            return;
        }
        if (args[0].equalsIgnoreCase("tac") && p.hasPermission("omicron.tac")) {
            if (Settings.ENABLED) {
                Settings.ENABLED = false;
                omicron.sendMessage(p, "§cФункциональность OMICRON была отключена!");
            }
            else {
                Settings.ENABLED = true;
                omicron.sendMessage(p, "§aФункциональность OMICRON была включена!");
            }
            final Omicron c = Omicron.getOmicron();
            c.reloadConfig();
            c.getConfig().set("enabled", (Object)Settings.ENABLED);
            c.saveConfig();
            return;
        }
        if (args[0].equalsIgnoreCase("help")) {
            boolean hasinfo = false;
            boolean hasnotify = false;
            boolean hasexempt = false;
            boolean hasvr = false;
            boolean hasrlcfg = false;
            boolean hastac = false;
            if (p.hasPermission("omicron.tac")) {
                hastac = true;
            }
            if (p.hasPermission("omicron.reload")) {
                hasrlcfg = true;
            }
            if (p.hasPermission("omicron.info")) {
                hasinfo = true;
            }
            if (p.hasPermission("omicron.notify")) {
                hasnotify = true;
            }
            if (p.hasPermission("omicron.exempt")) {
                hasexempt = true;
            }
            if (p.hasPermission("omicron.viewreport")) {
                hasvr = true;
            }
            if (hasnotify || hasexempt || hasinfo || hasvr || hasrlcfg) {
                omicron.sendMessage(p, "Список всех команд:");
            }
            if (hasinfo) {
                omicron.sendMessage(p, String.valueOf(Settings.VARIABLE_COLOR) + "/omicron info §7-§rПоказывает информацию о системе / сервере");
            }
            if (hasnotify) {
                omicron.sendMessage(p, String.valueOf(Settings.VARIABLE_COLOR) + "/omicron notify §7-§r Включение и выключение уведомлений.");
            }
            if (hasexempt) {
                omicron.sendMessage(p, String.valueOf(Settings.VARIABLE_COLOR) + "/omicron exempt <игрок> <время> §7-§rОсвобождает игрока на указанное время.");
            }
            if (hasvr) {
                omicron.sendMessage(p, String.valueOf(Settings.VARIABLE_COLOR) + "/omicron viewreport <id> §7-§rПросматривает отчет о удалении игрока.");
            }
            if (hasrlcfg) {
                omicron.sendMessage(p, String.valueOf(Settings.VARIABLE_COLOR) + "/omicron reload §7-§r Перезагрузка файла конфигурации.");
            }
            if (hastac) {
                omicron.sendMessage(p, String.valueOf(Settings.VARIABLE_COLOR) + "/omicron tac - §cЭто отключит / включит античит.");
            }
            return;
        }
        if (args[0].equalsIgnoreCase("info") && p.hasPermission("omicron.info")) {
            final double tps = UtilMath.trim(2, Lag.getTPS());
            String tps_real = "§c" + tps;
            if (tps >= 19.0) {
                tps_real = String.valueOf(Settings.VARIABLE_COLOR) + tps;
            }
            else if (tps >= 18.0) {
                tps_real = "§e" + tps;
            }
            omicron.sendMessage(p, "Current TPS: " + tps_real);
            omicron.sendMessage(p, "Maximum Memory: " + Settings.VARIABLE_COLOR + Runtime.getRuntime().maxMemory() / 1024L / 1024L + "MB");
            omicron.sendMessage(p, "Free Memory: " + Settings.VARIABLE_COLOR + Runtime.getRuntime().freeMemory() / 1024L / 1024L + "MB");
            omicron.sendMessage(p, "Available Cores: §6" + Runtime.getRuntime().availableProcessors());
            omicron.sendMessage(p, "Operating System: §6" + System.getProperty("os.name") + " (" + System.getProperty("os.version") + ")");
            omicron.sendMessage(p, "System Architecture: §6" + System.getProperty("os.arch"));
            String bukkitVersion = Bukkit.getVersion();
            bukkitVersion = bukkitVersion.substring(bukkitVersion.indexOf("MC: ") + 4, bukkitVersion.length() - 1);
            omicron.sendMessage(p, "Server Version: §e" + bukkitVersion);
            omicron.sendMessage(p, "Java Runtime Version: §e" + System.getProperty("java.runtime.version"));
            return;
        }
        if (args[0].equalsIgnoreCase("notify") && p.hasPermission("omicron.notify")) {
            if (omicron.nonotify.contains(p)) {
                omicron.nonotify.remove(p);
                omicron.sendMessage(p, "Уведомления сейчас" + Settings.VARIABLE_COLOR + "включены§r.");
            }
            else {
                omicron.nonotify.add(p);
                omicron.sendMessage(p, "Уведомления сейчас §4отключены§r.");
            }
            return;
        }
        if (args[0].equalsIgnoreCase("viewreport") && p.hasPermission("omicron.viewreport")) {
            if (args.length != 2) {
                omicron.sendMessage(p, "Usage: " + Settings.VARIABLE_COLOR + "/omicron viewreport <id>");
                return;
            }
            int rid = -1;
            try {
                rid = Integer.parseInt(args[1]);
            }
            catch (Exception ex) {}
            if (rid == -1) {
                omicron.sendMessage(p, "That's not a valid report ID.");
                return;
            }
            if (!omicron.getConfig().contains("Reports." + rid)) {
                omicron.sendMessage(p, "That's not a valid report ID.");
                return;
            }
            final List<String> rdata = (List<String>)omicron.getConfig().getStringList("Reports." + rid);
            for (final String s : rdata) {
                omicron.sendMessage(p, s.replaceAll("[VC]", Settings.VARIABLE_COLOR));
            }
        }
        else if (args[0].equalsIgnoreCase("exempt") && p.hasPermission("omicron.exempt")) {
            if (args.length != 3) {
                omicron.sendMessage(p, "Usage: " + Settings.VARIABLE_COLOR + "/omicron exempt <player> <time>");
                omicron.sendMessage(p, "§7Пример времени: " + Settings.VARIABLE_COLOR + "1h30m");
                return;
            }
            final Player t = Bukkit.getPlayer(args[1]);
            if (t == null || !t.isOnline()) {
                omicron.sendMessage(p, "That player is not online!");
                return;
            }
            final long expire = UtilTime.parseDateDiff(args[2], true);
            if (expire == 0L) {
                omicron.sendMessage(p, "Это не правильное время! Пример:" + Settings.VARIABLE_COLOR + "1d5h3m");
                return;
            }
            omicron.addExemption(p, expire - System.currentTimeMillis());
            omicron.broadcast(String.valueOf(Settings.VARIABLE_COLOR) + t.getDisplayName() + " §rбыл добавлен в список исключений.", "This will expire in " + Settings.VARIABLE_COLOR + UtilTime.MakeStr(expire - System.currentTimeMillis(), 2) + "§r.");
        }
        else {
            if (args[0].equalsIgnoreCase("reload") && p.hasPermission("omicron.reload")) {
                Settings.loadConfig();
                omicron.sendMessage(p, "Configuration reloaded.");
                return;
            }
            omicron.sendMessage(p, "Running Omicron version " + Settings.VARIABLE_COLOR + omicron.getDescription().getVersion().replaceAll("\\[", "").replaceAll("\\]", "") + "§f Plugin Creator" + Settings.VARIABLE_COLOR + omicron.getDescription().getAuthors().get(0).replaceAll("\\[", "").replaceAll("\\]", "") + "§f.");
            if (p.hasPermission("omicron.admin")) {
                omicron.sendMessage(p, "Используйте" + Settings.VARIABLE_COLOR + "/omicron help§r чтобы посмотреть все доступные команды");
            }
        }
    }
}
