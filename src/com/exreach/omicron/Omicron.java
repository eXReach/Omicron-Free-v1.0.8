package com.exreach.omicron;

import org.bukkit.plugin.java.*;
import com.exreach.omicron.fwk.*;
import com.exreach.omicron.fwk.Command;

import org.bukkit.entity.*;
import org.bukkit.plugin.*;
import com.exreach.omicron.command.*;
import com.exreach.omicron.checks.blocks.*;
import com.exreach.omicron.checks.movement.*;
import com.exreach.omicron.checks.flight.*;
import com.exreach.omicron.checks.combat.*;
import org.bukkit.command.*;
import com.exreach.omicron.logger.*;
import org.bukkit.event.*;
import com.exreach.omicron.checks.*;
import org.bukkit.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.block.*;
import java.text.*;
import java.util.*;
import com.exreach.omicron.util.*;
import org.bukkit.potion.*;
import java.io.*;
import java.lang.reflect.*;
import org.bukkit.event.player.*;

public class Omicron extends JavaPlugin implements Listener
{
    private static Object antiLock;
    public static Omicron core;
    public static CommandFramework _fw;
    public List<Player> nonotify;
    private static Map<Player, HashMap<Long, String>> reports;
    private static Map<Player, Long> exempt;
    private static Map<Player, Long> exemptblock;
    private static List<Check> All_Checks;
    
    static {
        Omicron.antiLock = new Object();
        Omicron.core = null;
        Omicron._fw = null;
        Omicron.reports = new HashMap<Player, HashMap<Long, String>>();
        Omicron.exempt = new HashMap<Player, Long>();
        Omicron.exemptblock = new HashMap<Player, Long>();
        Omicron.All_Checks = new ArrayList<Check>();
    }
    
    public Omicron() {
        this.nonotify = new ArrayList<Player>();
    }
    
    public void onEnable() {
        (Omicron.core = this).saveDefaultConfig();
        (Omicron._fw = new CommandFramework((Plugin)this)).registerCommands(new CenixCmd());
        Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask((Plugin)this, (Runnable)new Lag(), 100L, 1L);
        this.getServer().getPluginManager().registerEvents((Listener)this, (Plugin)this);
        this.getServer().getPluginManager().registerEvents((Listener)new PlayerLogger(), (Plugin)this);
        Settings.loadConfig();
        if (Bukkit.getOnlinePlayers().size() > 0) {
            for (final Player p : Bukkit.getOnlinePlayers()) {
                this.addExemption(p, 2000L);
            }
        }
        this.registerCheck(new BreakCheck());
        this.registerCheck(new PlaceCheck());
        this.registerCheck(new KillAuraCheck());
        this.registerCheck(new MultiAuraCheck());
        this.registerCheck(new BoatCheck());
        this.registerCheck(new WaterCheck());
        this.registerCheck(new HoverCheck());
        this.registerCheck(new RiseCheck());
        this.registerCheck(new SpeedCheck());
        this.registerCheck(new FloatCheck());
        this.registerCheck(new ReachCheck());
    }
    
    private void registerCheck(final Check check) {
        if (!Omicron.All_Checks.contains(check)) {
            Omicron.All_Checks.add(check);
        }
    }
    
    public void sendMessage(final Player p, final String msg) {
        p.sendMessage(String.valueOf(Settings.PREFIX) + " " + msg);
    }
    
    public void sendMessageBAR(final Player p, final String msg) {
        p.sendMessage(String.valueOf(Settings.PREFIX) + " " + msg);
    }
    
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onDamage(final EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            final Player p = (Player)event.getEntity();
            this.addExemption(p, 5L);
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onKick(final PlayerKickEvent event) {
        if (event.getReason().equalsIgnoreCase("Flying is not enabled on this server")) {
            this.addSuspicion(event.getPlayer(), "Fly");
            this.addExemption(event.getPlayer(), 50L);
            event.setCancelled(true);
        }
    }
    
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        return sender instanceof Player && Omicron._fw.handleCommand(sender, label, (org.bukkit.command.Command) command, args);
    }
    
    public static Omicron getOmicron() {
        return Omicron.core;
    }
    
    public User getUser(final Player p) {
        return new User(p);
    }
    
    public JavaPlugin getPlugin() {
        return this;
    }
    
    public boolean isExempt(final Player p) {
        return Omicron.exempt.containsKey(p) && System.currentTimeMillis() < Omicron.exempt.get(p);
    }
    
    public void addExemptionBlock(final Player p, final long ms) {
        Omicron.exemptblock.put(p, System.currentTimeMillis() + ms);
    }
    
    public void addExemption(final Player p, final long ms) {
        if (Omicron.exemptblock.containsKey(p)) {
            final long a = Omicron.exemptblock.get(p);
            if (System.currentTimeMillis() > a) {
                Omicron.exemptblock.remove(p);
                Omicron.exempt.put(p, System.currentTimeMillis() + ms);
            }
        }
        else {
            Omicron.exempt.put(p, System.currentTimeMillis() + ms);
        }
    }
    
    public void console(final String msg) {
        Bukkit.getConsoleSender().sendMessage(String.valueOf(Settings.PREFIX) + " " + msg);
    }
    
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onMove(final PlayerMoveEvent event) {
        if (!Settings.ENABLED) {
            return;
        }
        if (!this.isExempt(event.getPlayer())) {
            for (final Check c : Omicron.All_Checks) {
                if (c.getEventCall().equals(event.getEventName())) {
                    final CheckResult result = c.performCheck(this.getUser(event.getPlayer()), (Event)event);
                    if (result.passed()) {
                        continue;
                    }
                    this.addSuspicion(event.getPlayer(), result.getCheckName());
                    Location newloc = this.getUser(event.getPlayer()).LastGroundLocation();
                    if (result.getCheckName().contains("speed") && !result.getCheckName().contains("fly")) {
                        newloc = this.getUser(event.getPlayer()).LastRegularLocation();
                    }
                    if (newloc != null) {
                        event.getPlayer().teleport(newloc);
                    }
                    else {
                        if (Lag.getNiceTPS() < Settings.TPS_LAG_THRESHOLD) {
                            continue;
                        }
                        event.setCancelled(true);
                    }
                }
            }
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlace(final BlockPlaceEvent event) {
        if (!Settings.ENABLED) {
            return;
        }
        for (final Check c : Omicron.All_Checks) {
            if (c.getEventCall().equals(event.getEventName())) {
                final CheckResult result = c.performCheck(this.getUser(event.getPlayer()), (Event)event);
                if (result.passed()) {
                    continue;
                }
                if (Lag.getNiceTPS() >= Settings.TPS_LAG_THRESHOLD) {
                    event.setCancelled(true);
                }
                this.addSuspicion(event.getPlayer(), result.getCheckName());
            }
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onDamage(final EntityDamageByEntityEvent event) {
        if (!Settings.ENABLED) {
            return;
        }
        if (event.getDamager() instanceof Player) {
            final Player p = (Player)event.getDamager();
            for (final Check c : Omicron.All_Checks) {
                if (c.getEventCall().equals(event.getEventName())) {
                    final CheckResult result = c.performCheck(this.getUser(p), (Event)event);
                    if (result.passed()) {
                        continue;
                    }
                    if (Lag.getNiceTPS() >= Settings.TPS_LAG_THRESHOLD) {
                        event.setCancelled(true);
                    }
                    this.addSuspicion(p, result.getCheckName());
                }
            }
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBreak(final BlockBreakEvent event) {
        if (!Settings.ENABLED) {
            return;
        }
        for (final Check c : Omicron.All_Checks) {
            if (c.getEventCall().equals(event.getEventName())) {
                final CheckResult result = c.performCheck(this.getUser(event.getPlayer()), (Event)event);
                if (result.passed()) {
                    continue;
                }
                if (Lag.getNiceTPS() >= Settings.TPS_LAG_THRESHOLD) {
                    event.setCancelled(true);
                }
                this.addSuspicion(event.getPlayer(), result.getCheckName());
            }
        }
    }
    
    private boolean updateDatabase(final Player p) {
        synchronized (Omicron.antiLock) {
            final List<Long> remove = new ArrayList<Long>();
            for (final Long l : Omicron.reports.get(p).keySet()) {
                if (System.currentTimeMillis() > l && !remove.contains(l)) {
                    remove.add(l);
                }
            }
            for (final Long r : remove) {
                if (Omicron.reports.get(p).containsKey(r)) {
                    Omicron.reports.get(p).remove(r);
                }
            }
            if (Omicron.reports.get(p).size() >= Settings.PUNISH_OFFENSE_COUNT && Settings.PUNISH) {
                int rid = 0;
                if (Settings.LOG_REPORTS) {
                    String offenses = Settings.VARIABLE_COLOR;
                    final Date d = new Date();
                    final SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss a");
                    sdf.setTimeZone(TimeZone.getTimeZone(Settings.TIMEZONE));
                    final String time = sdf.format(d).toLowerCase().replaceAll(" am", "am").replaceAll(" pm", "pm");
                    int tick = 0;
                    for (final String s : Omicron.reports.get(p).values()) {
                        if (!offenses.contains(s)) {
                            if (tick == 0) {
                                offenses = "§f" + s;
                            }
                            else {
                                offenses = String.valueOf(offenses) + "§7, " + Settings.VARIABLE_COLOR + s;
                            }
                            ++tick;
                        }
                    }
                    final List<String> r2 = new ArrayList<String>();
                    r2.add("§f--- Report for [VC]" + p.getName() + "§f ---");
                    r2.add("Time [EST]: [VC]" + time);
                    r2.add("Offenses: [VC]" + offenses);
                    r2.add(" ");
                    r2.add("The following was fetched before-punishment:");
                    r2.add("In Vehicle: [VC]" + (p.isInsideVehicle() ? "Yes" : "No"));
                    if (p.isInsideVehicle()) {
                        r2.add("Vehicle Type: [VC]" + p.getVehicle().getType().toString().toLowerCase().replaceAll("_", " "));
                    }
                    r2.add("Health: [VC]" + UtilMath.trim(1, p.getHealth()) + " (" + UtilMath.trim(1, p.getHealth() / 2.0) + " hearts)");
                    r2.add("Food Level: [VC]" + p.getFoodLevel());
                    r2.add("Coordinates (X,Y,Z): [VC]" + p.getLocation().getBlockX() + "§f, " + "[VC]" + p.getLocation().getBlockY() + "§f, " + "[VC]" + p.getLocation().getBlockZ());
                    r2.add("On Fire: [VC]" + ((p.getFireTicks() > 0) ? "Yes" : "No"));
                    r2.add("Falling: [VC]" + (PlayerLogger.getLogger().isFalling(p) ? "Yes" : "No"));
                    r2.add("Bouncing: [VC]" + (PlayerLogger.getLogger().isBouncing(p) ? "Yes" : "No"));
                    r2.add("Flying: [VC]" + (p.isFlying() ? "Yes" : "No"));
                    r2.add("GameMode: [VC]" + p.getGameMode().toString().toLowerCase());
                    r2.add("[INACCURATE] Ping: [VC]" + getPing(p));
                    r2.add("Potion Effects: [VC]" + ((p.getActivePotionEffects().size() != 0) ? "Yes" : "No"));
                    for (final PotionEffect eff : p.getActivePotionEffects()) {
                        r2.add("  §f- [VC]" + eff.getType().getName() + " (x" + eff.getAmplifier() + ")");
                    }
                    r2.add("§f--- Конец отчета ---");
                    final int id = rid = Reports.saveReport(r2);
                    if (!Settings.REPORT_SAVED_ALERT.equalsIgnoreCase("")) {
                        String m = Settings.REPORT_SAVED_ALERT;
                        m = m.replaceAll("\\[VARIABLE_COLOR\\]", Settings.VARIABLE_COLOR);
                        m = m.replaceAll("\\[DISPLAYNAME\\]", p.getDisplayName());
                        m = m.replaceAll("\\[USERNAME\\]", p.getName());
                        m = m.replaceAll("\\[NAME\\]", p.getName());
                        m = m.replaceAll("\\[UUID\\]", p.getUniqueId().toString());
                        m = m.replaceAll("\\[REPORT_ID\\]", new StringBuilder(String.valueOf(id)).toString());
                        this.broadcast(m);
                    }
                }
                String j = Settings.PUNISH_COMMAND;
                j = j.replaceAll("\\[VARIABLE_COLOR\\]", Settings.VARIABLE_COLOR);
                j = j.replaceAll("\\[DISPLAYNAME\\]", p.getDisplayName());
                j = j.replaceAll("\\[USERNAME\\]", p.getName());
                j = j.replaceAll("\\[NAME\\]", p.getName());
                j = j.replaceAll("\\[UUID\\]", p.getUniqueId().toString());
                j = j.replaceAll("\\[REPORT_ID\\]", new StringBuilder(String.valueOf(rid)).toString());
                Bukkit.getServer().dispatchCommand((CommandSender)this.getServer().getConsoleSender(), j);
                // monitorexit(Omicron.antiLock)
                return true;
            }
            // monitorexit(Omicron.antiLock)
            return false;
        }
    }
    
    public void logFile(final String line) {
        if (Settings.LOG_OFFENSES) {
            try {
                final FileWriter fw = new FileWriter(new File(this.getDataFolder(), "offenses.txt"), true);
                final PrintWriter pw = new PrintWriter(fw);
                final Date d = new Date();
                final SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss a");
                try {
                    sdf.setTimeZone(TimeZone.getTimeZone(Settings.TIMEZONE));
                }
                catch (Exception e2) {
                    this.console("§c'timezone' не является действительным часовым поясом! По умолчанию America/New_York");
                    Settings.TIMEZONE = "America/New_York";
                }
                pw.println("[" + sdf.format(d).toLowerCase().replaceAll(" am", "am").replaceAll(" pm", "pm") + "] " + line);
                pw.flush();
                pw.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    public static int getPing(final Player p) {
        int ping = 0;
        try {
            final Object entityPlayer = p.getClass().getMethod("getHandle", (Class<?>[])new Class[0]).invoke(p, new Object[0]);
            ping = (int)entityPlayer.getClass().getField("ping").get(entityPlayer);
        }
        catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException | NoSuchFieldException ex2) {
            final Exception ex = ex2;
            final Exception e = ex;
            e.printStackTrace();
        }
        if (ping > 0) {
            ping /= 2;
        }
        return ping;
    }
    
    public void addSuspicion(final Player p, final String detector) {
        if (!Settings.ENABLED) {
            return;
        }
        if (!Omicron.reports.containsKey(p)) {
            Omicron.reports.put(p, new HashMap<Long, String>());
        }
        if (this.isExempt(p)) {
            return;
        }
        int ping = 0;
        try {
            final Object entityPlayer = p.getClass().getMethod("getHandle", (Class<?>[])new Class[0]).invoke(p, new Object[0]);
            ping = (int)entityPlayer.getClass().getField("ping").get(entityPlayer);
        }
        catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException | NoSuchFieldException ex2) {
            final Exception ex = ex2;
            final Exception e = ex;
            e.printStackTrace();
        }
        if (ping > 0) {
            ping /= 2;
        }
        Integer Count = 0;
        if (Settings.OFFENSE_EXPIRE_TIME == 0) {
            Omicron.reports.get(p).put(System.currentTimeMillis() + System.currentTimeMillis() * 10L, detector);
        }
        else {
            Omicron.reports.get(p).put(System.currentTimeMillis() + Settings.OFFENSE_EXPIRE_TIME * 1000, detector);
        }
        for (final String v : Omicron.reports.get(p).values()) {
            if (v.equalsIgnoreCase(detector)) {
                ++Count;
            }
        }
        this.addExemptionBlock(p, 50L);
        if (Count <= 1) {
            return;
        }
        if (ping >= 125) {
            String m = Settings.SUSPICION_ALERT_IGNORE_PING;
            m = m.replaceAll("\\[VARIABLE_COLOR\\]", Settings.VARIABLE_COLOR);
            m = m.replaceAll("\\[DISPLAYNAME\\]", p.getDisplayName());
            m = m.replaceAll("\\[USERNAME\\]", p.getName());
            m = m.replaceAll("\\[NAME\\]", p.getName());
            m = m.replaceAll("\\[UUID\\]", p.getUniqueId().toString());
            m = m.replaceAll("\\[SUSPICION\\]", detector);
            m = m.replaceAll("\\[COUNT\\]", new StringBuilder().append(Count).toString());
            m = m.replaceAll("\\[PING\\]", new StringBuilder(String.valueOf(ping)).toString());
            this.broadcast(m);
            this.logFile(m);
            return;
        }
        if (Lag.getTPS() <= Settings.TPS_LAG_THRESHOLD) {
            String m = Settings.SUSPICION_ALERT_IGNORE_TPS;
            m = m.replaceAll("\\[VARIABLE_COLOR\\]", Settings.VARIABLE_COLOR);
            m = m.replaceAll("\\[DISPLAYNAME\\]", p.getDisplayName());
            m = m.replaceAll("\\[USERNAME\\]", p.getName());
            m = m.replaceAll("\\[NAME\\]", p.getName());
            m = m.replaceAll("\\[UUID\\]", p.getUniqueId().toString());
            m = m.replaceAll("\\[SUSPICION\\]", detector);
            m = m.replaceAll("\\[COUNT\\]", new StringBuilder().append(Count).toString());
            m = m.replaceAll("\\[TPS\\]", new StringBuilder(String.valueOf(Lag.getNiceTPS())).toString());
            this.broadcast(m);
            this.logFile(m);
            return;
        }
        if (this.updateDatabase(p)) {
            return;
        }
        String m = Settings.SUSPICION_ALERT;
        m = m.replaceAll("\\[VARIABLE_COLOR\\]", Settings.VARIABLE_COLOR);
        m = m.replaceAll("\\[DISPLAYNAME\\]", p.getDisplayName());
        m = m.replaceAll("\\[USERNAME\\]", p.getName());
        m = m.replaceAll("\\[NAME\\]", p.getName());
        m = m.replaceAll("\\[UUID\\]", p.getUniqueId().toString());
        m = m.replaceAll("\\[SUSPICION\\]", detector);
        m = m.replaceAll("\\[COUNT\\]", new StringBuilder().append(Count).toString());
        this.broadcast(m);
    }
    
    public void broadcast(final String... msgs) {
        for (final String m : msgs) {
            this.console(m);
        }
        for (final Player s : Bukkit.getOnlinePlayers()) {
            if (s.hasPermission("omicron.notify") && !this.nonotify.contains(s)) {
                for (final String i : msgs) {
                    if (Settings.ABN) {
                        this.sendMessageBAR(s, i);
                    }
                    else {
                        this.sendMessage(s, i);
                    }
                }
            }
        }
    }
    
    @EventHandler
    public void onQuit(final PlayerQuitEvent event) {
        final Player p = event.getPlayer();
        if (Omicron.reports.containsKey(p)) {
            Omicron.reports.remove(p);
        }
        if (Omicron.exempt.containsKey(p)) {
            Omicron.exempt.remove(p);
        }
    }
    
    public void addExemptionBlock(final User u, final int ms) {
        this.addExemptionBlock(u.getPlayer(), ms);
    }
}
