package com.exreach.omicron.fwk;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.AbstractMap;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.help.GenericCommandHelpTopic;
import org.bukkit.help.HelpTopic;
import org.bukkit.help.HelpTopicComparator;
import org.bukkit.help.IndexHelpTopic;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.SimplePluginManager;
import org.apache.commons.lang.Validate;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.*;
import java.util.Map.Entry;
import org.bukkit.plugin.java.*;
import com.exreach.omicron.fwk.*;
import org.bukkit.entity.*;
import org.bukkit.plugin.*;
import com.exreach.omicron.command.*;
import com.exreach.omicron.checks.blocks.*;
import com.exreach.omicron.checks.movement.*;
import com.exreach.omicron.checks.flight.*;
import com.exreach.omicron.checks.combat.*;
import com.exreach.omicron.logger.*;
import org.bukkit.event.*;
import com.exreach.omicron.checks.*;
import org.bukkit.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.block.*;
import java.text.*;
import com.exreach.omicron.util.*;
import org.bukkit.potion.*;
import java.io.*;
import java.lang.reflect.*;
import org.bukkit.event.player.*;
import org.apache.commons.lang.*;
import java.io.File;
import java.util.ArrayList;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.BlockIterator;

public class CommandFramework
{
    private Map<String, Map.Entry<Method, Object>> commandMap;
    private CommandMap map;
    private Plugin plugin;
    
    public CommandFramework(final Plugin plugin) {
        this.commandMap = new HashMap<String, Map.Entry<Method, Object>>();
        this.plugin = plugin;
        if (plugin.getServer().getPluginManager() instanceof SimplePluginManager) {
            final SimplePluginManager manager = (SimplePluginManager)plugin.getServer().getPluginManager();
            try {
                final Field field = SimplePluginManager.class.getDeclaredField("commandMap");
                field.setAccessible(true);
                this.map = (CommandMap)field.get(manager);
            }
            catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
            catch (SecurityException e2) {
                e2.printStackTrace();
            }
            catch (IllegalAccessException e3) {
                e3.printStackTrace();
            }
            catch (NoSuchFieldException e4) {
                e4.printStackTrace();
            }
        }
    }
    
    public boolean handleCommand(final CommandSender sender, final String label, final Command cmd, final String[] args) {
        for (int i = args.length; i >= 0; --i) {
            final StringBuffer buffer = new StringBuffer();
            buffer.append(label.toLowerCase());
            for (int x = 0; x < i; ++x) {
                buffer.append("." + args[x].toLowerCase());
            }
            final String cmdLabel = buffer.toString();
            if (this.commandMap.containsKey(cmdLabel)) {
                final Map.Entry<Method, Object> entry = this.commandMap.get(cmdLabel);
                final com.exreach.omicron.fwk.Command command = entry.getKey().getAnnotation(com.exreach.omicron.fwk.Command.class);
                try {
                    if (!sender.hasPermission(command.permission()) && !command.permission().equalsIgnoreCase("")) {
                        sender.sendMessage(command.noPerm());
                    }
                    else {
                        entry.getKey().invoke(entry.getValue(), new CommandArgs(sender, (com.exreach.omicron.fwk.Command) cmd, label, args, cmdLabel.split("\\.").length - 1));
                    }
                }
                catch (IllegalArgumentException e) {
                    e.printStackTrace();
                }
                catch (IllegalAccessException e2) {
                    e2.printStackTrace();
                }
                catch (InvocationTargetException e3) {
                    e3.printStackTrace();
                }
                return true;
            }
        }
        this.defaultCommand(new CommandArgs(sender, (com.exreach.omicron.fwk.Command) cmd, label, args, 0));
        return true;
    }
    
    public void registerCommands(final Object obj) {
        Method[] methods;
        for (int length = (methods = obj.getClass().getMethods()).length, i = 0; i < length; ++i) {
            final Method m = methods[i];
            if (m.getAnnotation(com.exreach.omicron.fwk.Command.class) != null) {
                final com.exreach.omicron.fwk.Command command = m.getAnnotation(com.exreach.omicron.fwk.Command.class);
                if (m.getParameterTypes().length > 1 || m.getParameterTypes()[0] != CommandArgs.class) {
                    System.out.println("Unable to register command " + m.getName() + ". Unexpected method arguments");
                }
                else {
                    this.registerCommand(command, command.name(), m, obj);
                    String[] aliases;
                    for (int length2 = (aliases = command.aliases()).length, j = 0; j < length2; ++j) {
                        final String alias = aliases[j];
                        this.registerCommand(command, alias, m, obj);
                    }
                }
            }
            else if (m.getAnnotation(Completer.class) != null) {
                final Completer comp = m.getAnnotation(Completer.class);
                if (m.getParameterTypes().length > 1 || m.getParameterTypes().length == 0 || m.getParameterTypes()[0] != CommandArgs.class) {
                    System.out.println("Unable to register tab completer " + m.getName() + ". Unexpected method arguments");
                }
                else if (m.getReturnType() != List.class) {
                    System.out.println("Unable to register tab completer " + m.getName() + ". Unexpected return type");
                }
                else {
                    this.registerCompleter(comp.name(), m, obj);
                    String[] aliases2;
                    for (int length3 = (aliases2 = comp.aliases()).length, k = 0; k < length3; ++k) {
                        final String alias = aliases2[k];
                        this.registerCompleter(alias, m, obj);
                    }
                }
            }
        }
    }
    
    public void registerHelp() {
        final Set<HelpTopic> help = new TreeSet<HelpTopic>((Comparator<? super HelpTopic>)HelpTopicComparator.helpTopicComparatorInstance());
        for (final String s : this.commandMap.keySet()) {
            if (!s.contains(".")) {
                final Command cmd = this.map.getCommand(s);
                final HelpTopic topic = (HelpTopic)new GenericCommandHelpTopic(cmd);
                help.add(topic);
            }
        }
        final IndexHelpTopic topic2 = new IndexHelpTopic(this.plugin.getName(), "All commands for " + this.plugin.getName(), (String)null, (Collection)help, "Below is a list of all " + this.plugin.getName() + " commands:");
        Bukkit.getServer().getHelpMap().addTopic((HelpTopic)topic2);
    }
    
    private void registerCommand(final com.exreach.omicron.fwk.Command command, final String label, final Method m, final Object obj) {
        final Map.Entry<Method, Object> entry = new AbstractMap.SimpleEntry<Method, Object>(m, obj);
        this.commandMap.put(label.toLowerCase(), entry);
        final String cmdLabel = label.replace(".", ",").split(",")[0].toLowerCase();
        if (this.map.getCommand(cmdLabel) == null) {
            final Command cmd = new BukkitCommand(cmdLabel, this.plugin);
            this.map.register(this.plugin.getName(), cmd);
        }
        if (!command.description().equalsIgnoreCase("") && cmdLabel == label) {
            this.map.getCommand(cmdLabel).setDescription(command.description());
        }
        if (!command.usage().equalsIgnoreCase("") && cmdLabel == label) {
            this.map.getCommand(cmdLabel).setUsage(command.usage());
        }
    }
    
    private void registerCompleter(final String label, final Method m, final Object obj) {
        final String cmdLabel = label.replace(".", ",").split(",")[0].toLowerCase();
        if (this.map.getCommand(cmdLabel) == null) {
            final Command command = new BukkitCommand(cmdLabel, this.plugin);
            this.map.register(this.plugin.getName(), command);
        }
        if (this.map.getCommand(cmdLabel) instanceof BukkitCommand) {
            final BukkitCommand command2 = (BukkitCommand)this.map.getCommand(cmdLabel);
            if (command2.completer == null) {
            }
            command2.completer.addCompleter(label, m, obj);
        }
        else if (this.map.getCommand(cmdLabel) instanceof PluginCommand) {
            try {
                final Object command3 = this.map.getCommand(cmdLabel);
                final Field field = command3.getClass().getDeclaredField("completer");
                field.setAccessible(true);
                if (field.get(command3) == null) {
                }
                else if (field.get(command3) instanceof BukkitCompleter) {
                    final BukkitCompleter completer = (BukkitCompleter)field.get(command3);
                    completer.addCompleter(label, m, obj);
                }
                else {
                    System.out.println("Unable to register tab completer " + m.getName() + ". A tab completer is already registered for that command!");
                }
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
    
    private void defaultCommand(final CommandArgs args) {
        args.getSender().sendMessage(String.valueOf(args.getLabel()) + " is not handled! Oh noes!");
    }
}
