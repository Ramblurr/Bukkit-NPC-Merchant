package com.binaryelysium.NPCTrader;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.MobType;
import org.bukkit.entity.Player;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginLoader;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.util.config.Configuration;
import org.bukkit.util.config.ConfigurationNode;

import redecouverte.npcspawner.BasicHumanNpc;
import redecouverte.npcspawner.BasicHumanNpcList;
import redecouverte.npcspawner.NpcSpawner;
import sun.util.logging.resources.logging;

/**
 * NPCTrader for Bukkit
 *
 * @author CaseyLink
 */
public class NPCTrader extends JavaPlugin {
    private final NPCTraderPlayerListener playerListener = new NPCTraderPlayerListener(this);
    private final NPCTraderBlockListener blockListener = new NPCTraderBlockListener(this);
    private final HashMap<Player, Boolean> debugees = new HashMap<Player, Boolean>();
    private EEntityListener mEntityListener;
    public BasicHumanNpcList HumanNPCList;
    public HashMap<String, HumanTrader> TraderList; // map of NPC unique ids and the traders
    
    private Logger log;
    
    public NPCTrader() {
    	log = Logger.getLogger("Minecraft");
    }
    
    private void firstRunSetup() {
    	File dataFolder = getDataFolder();
    	if( !dataFolder.mkdirs() ) {
    		log.log(Level.SEVERE, "Failed to create data folder");
    		
    		File config = new File(dataFolder, "config.yml");
    		try { 
    			if(!config.createNewFile())
    				throw new IOException("failed");
    		} catch(IOException e) {
    			log.log(Level.SEVERE, "Failed to create config file");
    		}
    			
    	}
    	
    }
    public void onEnable() {
    	
    	HashMap<String, Integer> prices = new HashMap<String, Integer>();
    	
    	Configuration config = this.getConfiguration();
    	
    	// Ugly ugly hack to workaround broken getNodes() 
    	Object o = config.getProperty("prices");
        if (o == null) {
        	log.log(Level.SEVERE, "Could not find any items+prices");
        } else if (o instanceof Map) {
            Map<String, ConfigurationNode> nodes =
                new HashMap<String, ConfigurationNode>();

            for (Map.Entry<String, Object> entry : ((Map<String, Object>)o).entrySet()) {
            	String item = entry.getKey();
            	int value = -1;
            	try { 
            		value = (Integer) entry.getValue();
            	} catch( ClassCastException e ) {
            		log.log(Level.SEVERE, "Failed to parse config file. Error with key: " + entry.getKey());
            	}
            	prices.put(item, value);
            }
        }

        // Register our events
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvent(Event.Type.PLAYER_JOIN, playerListener, Priority.Normal, this);
       
        mEntityListener = new EEntityListener(this, prices);
        pm.registerEvent(Event.Type.ENTITY_TARGET, mEntityListener, Priority.Normal, this);
        pm.registerEvent(Event.Type.ENTITY_DAMAGED, mEntityListener, Priority.Normal, this);

        this.HumanNPCList = new BasicHumanNpcList();        

        PluginDescriptionFile pdfFile = this.getDescription();
        log.log(Level.INFO, pdfFile.getName() + " version " + pdfFile.getVersion() + ": loaded "+ prices.size() + " items.");
    }
    public void onDisable() {

    }

    public boolean isDebugging(final Player player) {
        if (debugees.containsKey(player)) {
            return debugees.get(player);
        } else {
            return false;
        }
    }

    public void setDebugging(final Player player, final boolean value) {
        debugees.put(player, value);
    }
    
    static boolean spawnHuman = true;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {

        try {

            if (!command.getName().toLowerCase().equals("bnpc")) {
                return false;
            }
            if (!(sender instanceof Player)) {
                return false;
            }

            if (args.length < 1) {
                return false;
            }

            String subCommand = args[0].toLowerCase();

            Player player = (Player) sender;
            Location l = player.getLocation();


            // create npc-id npc-name
            if (subCommand.equals("create")) {
                if (args.length < 3) {
                    return false;
                }

                if (this.HumanNPCList.get(args[1]) != null) {
                    player.sendMessage("This npc-id is already in use.");
                    return true;
                }

                BasicHumanNpc hnpc = NpcSpawner.SpawnBasicHumanNpc(args[1], args[2], player.getWorld(), l.getX(), l.getY(), l.getZ(), l.getYaw(), l.getPitch());
                this.HumanNPCList.put(args[1], hnpc);
                
                ItemStack is = new ItemStack(Material.BOOKSHELF);
                is.setAmount(1);
                hnpc.getBukkitEntity().setItemInHand(is);


            // attackme npc-id
            } else if (subCommand.equals("attackme")) {

                if (args.length < 2) {
                    return false;
                }

                BasicHumanNpc npc = this.HumanNPCList.get(args[1]);
                if (npc != null) {
                    npc.attackLivingEntity(player);
                    return true;
                }

            // move npc-id
            } else if (subCommand.equals("move")) {
                if (args.length < 2) {
                    return false;
                }

                BasicHumanNpc npc = this.HumanNPCList.get(args[1]);
                if (npc != null) {
                    npc.moveTo(l.getX(), l.getY(), l.getZ(), l.getYaw(), l.getPitch());
                    return true;
                }

            // spawnpig
            } else if (subCommand.equals("spawnpig")) {
                NpcSpawner.SpawnMob(MobType.PIG, player.getWorld(), l.getX(), l.getY(), l.getZ());
            }


        } catch (Exception e) {
            sender.sendMessage("An error occured.");
            System.out.println("BasicNPCs: error: " + e.getMessage() + e.getStackTrace().toString());
            e.printStackTrace();
            return true;
        }

        return true;
    }
    
}

