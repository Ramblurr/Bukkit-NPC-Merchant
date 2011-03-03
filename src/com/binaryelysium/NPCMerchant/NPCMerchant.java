package com.binaryelysium.NPCMerchant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.world.WorldListener;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;
import org.bukkit.util.config.ConfigurationNode;

import redecouverte.npcspawner.BasicHumanNpc;
import redecouverte.npcspawner.BasicHumanNpcList;
import redecouverte.npcspawner.NpcSpawner;

/**
 * NPCMerchant for Bukkit
 * 
 * @author CaseyLink
 */
public class NPCMerchant extends JavaPlugin {
    private final NPCMerchantPlayerListener playerListener = new NPCMerchantPlayerListener(
                                                                   this);
    private final NPCMerchantBlockListener  blockListener  = new NPCMerchantBlockListener(
                                                                   this);
    private final HashMap<Player, Boolean>  debugees       = new HashMap<Player, Boolean>();
    private WorldListener                   mWorldListener = new NPCMerchantWorldListener(
                                                                   this);
    private EEntityListener                 mEntityListener;
    public BasicHumanNpcList                HumanNPCList;
    public HashMap<String, HumanTrader>     TraderList;

    private static Logger                   log            = Logger.getLogger("Minecraft");

    private final static String             logPrefix      = "[NPC Merchant] ";

    public static void info( String msg ) {
        log.log(Level.INFO, logPrefix + msg);
    }

    public static void error( String msg ) {
        log.log(Level.SEVERE, logPrefix + msg);
    }

    public NPCMerchant() {
    }

    public void onEnable() {
        this.HumanNPCList = new BasicHumanNpcList();
        this.TraderList = new HashMap<String, HumanTrader>();
        reloadMerchantConfig();

        // Register our events
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvent(Event.Type.PLAYER_JOIN, playerListener, Priority.Normal, this);

        mEntityListener = new EEntityListener(this);
        pm.registerEvent(Event.Type.ENTITY_TARGET, mEntityListener, Priority.Normal, this);
        pm.registerEvent(Event.Type.ENTITY_DAMAGED, mEntityListener, Priority.Normal,
                this);
        pm.registerEvent(Event.Type.WORLD_LOADED, mWorldListener, Priority.Normal, this);

        spawnAllNPCs(this.getServer().getWorld("world"));
        PluginDescriptionFile pdfFile = this.getDescription();
        NPCMerchant.info(pdfFile.getName() + " version " + pdfFile.getVersion()
                + ": loaded " + TraderList.size() + " npcs.");
    }

    public void onDisable() {

        NPCMerchant.info("Disabling");
        saveNPCs();
    }

    public void saveNPCs() {
        Configuration config = this.getConfiguration();
        for ( BasicHumanNpc npc : HumanNPCList.values() ) {
            NPCMerchant.info("Saving NPC " + npc.getName());
            Location loc = npc.getBukkitEntity().getLocation();

            String base_pos_path = npc.getUniqueId() + ".position.";

            config.setProperty(base_pos_path + "x", loc.getX());
            config.setProperty(base_pos_path + "y", loc.getY());
            config.setProperty(base_pos_path + "z", loc.getZ());
            config.setProperty(base_pos_path + "yaw", loc.getYaw());
            config.setProperty(base_pos_path + "pitch", loc.getPitch());
        }
        if ( !config.save() ) {
            NPCMerchant.error("Couldn't save config");
        }
    }

    public void removeNPC( BasicHumanNpc npc ) {
        NpcSpawner.RemoveBasicHumanNpc(npc);

        Configuration config = this.getConfiguration();
        NPCMerchant.info("Removing NPC " + npc.getName());

        String base_pos_path = npc.getUniqueId() + ".position.";
        config.removeProperty(base_pos_path);

        HumanNPCList.remove(npc.getUniqueId());
        TraderList.remove(npc.getUniqueId());
        if ( !config.save() ) {
            NPCMerchant.error("Couldn't save config");
        }
    }

    public List<ItemValuePair> parseItemValuePairs( Configuration config, String path ) {
        // Ugly ugly hack to workaround broken getNodes()
        Object o = config.getProperty(path);
        if ( o == null ) {
            NPCMerchant.error("Could not find items: " + path);
        } else if ( o instanceof Map ) {
            Map<String, ConfigurationNode> nodes = new HashMap<String, ConfigurationNode>();
            List<ItemValuePair> list = new ArrayList<ItemValuePair>();
            for ( Map.Entry<String, Object> entry : ( (Map<String, Object>) o).entrySet() ) {
                String item = entry.getKey();
                int value = -1;
                try {
                    value = (Integer) entry.getValue();
                } catch ( ClassCastException e ) {
                    NPCMerchant.error("Failed to parse config file. Error with key: "
                            + entry.getKey() + " for " + path);
                }
                if ( value != -1 ) {
                    ItemValuePair pair = new ItemValuePair(item, value);
                    list.add(pair);
                }
            }
            return list;
        }
        return null;
    }

    public boolean isDebugging( final Player player ) {
        if ( debugees.containsKey(player) ) {
            return debugees.get(player);
        } else {
            return false;
        }
    }

    public void setDebugging( final Player player, final boolean value ) {
        debugees.put(player, value);
    }

    private void reloadMerchantConfig() {
        this.TraderList.clear();
        Configuration config = this.getConfiguration();
        List<String> npc_list = config.getStringList("npcs", null);

        for ( String npc_name : npc_list ) {
            // load items
            Map<String, ConfigurationNode> items = config.getNodes(npc_name
                    + ".items_for_sale");
            HashMap<String, List<ItemValuePair>> items_map = new HashMap<String, List<ItemValuePair>>();
            for ( Map.Entry<String, ConfigurationNode> entry : items.entrySet() ) {
                String item_for_sale = entry.getKey();
                List<ItemValuePair> subitems = this.parseItemValuePairs(config, npc_name
                        + ".items_for_sale." + item_for_sale);
                items_map.put(item_for_sale, subitems);
            }
            HumanTrader trader = new HumanTrader(npc_name, items_map);
            this.TraderList.put(npc_name, trader);
            NPCMerchant.info("Loaded " + npc_name + " with " + items_map
                    + " items for sale.");
        }
    }

    public void spawnAllNPCs( World world ) {
        NPCMerchant.info("Spawning all NPCs");
        this.HumanNPCList.clear();
        Configuration config = this.getConfiguration();
        config.load();

        List<String> npc_list = config.getStringList("npcs", null);

        for ( String npc_name : npc_list ) {
            // load position info
            float yaw, pitch;
            Double x, y, z;
            String base_pos_path = npc_name + ".position.";

            x = (Double) config.getProperty(base_pos_path + "x");
            y = (Double) config.getProperty(base_pos_path + "y");
            z = (Double) config.getProperty(base_pos_path + "z");
            System.out.println("x:" + x + "  y:" + y + "  z:" + z);

            yaw = (float) config.getDouble(base_pos_path + "yaw", 0);
            pitch = (float) config.getDouble(base_pos_path + "pitch", 0);

            if ( x != null && y != null && z != null ) {
                NPCMerchant.info("Spawning " + npc_name);
                BasicHumanNpc hnpc = NpcSpawner.SpawnBasicHumanNpc(npc_name, npc_name,
                        world, x, y, z, yaw, pitch);
                this.HumanNPCList.put(npc_name, hnpc);
            }
        }
    }

    public void onChunkLoaded( Chunk chunk ) {

    }

    static boolean spawnHuman = true;

    @Override
    public boolean onCommand( CommandSender sender, Command command, String commandLabel,
            String[] args ) {

        try {

            if ( !command.getName().toLowerCase().equals("merchant") ) {
                return false;
            }
            if ( ! ( sender instanceof Player) ) {
                return false;
            }

            if ( args.length < 1 ) {
                return false;
            }

            String subCommand = args[0].toLowerCase();

            Player player = (Player) sender;
            Location l = player.getLocation();

            // create npc-id npc-name
            if ( subCommand.equals("create") ) {
                if ( args.length < 2 ) {
                    return false;
                }

                if ( this.HumanNPCList.get(args[1]) != null ) {
                    player.sendMessage("This npc-id is already in use.");
                    return true;
                }

                BasicHumanNpc hnpc = NpcSpawner.SpawnBasicHumanNpc(args[1], args[1],
                        player.getWorld(), l.getX(), l.getY(), l.getZ(), l.getYaw(),
                        l.getPitch());
                this.HumanNPCList.put(args[1], hnpc);
                saveNPCs();
            } else if ( subCommand.equals("remove") ) {
                BasicHumanNpc npc = this.HumanNPCList.get(args[1]);
                if ( npc != null ) {
                    removeNPC(npc);
                }
                // move npc-id
            } else if ( subCommand.equals("move") ) {
                if ( args.length < 2 ) {
                    return false;
                }

                BasicHumanNpc npc = this.HumanNPCList.get(args[1]);
                if ( npc != null ) {
                    npc.moveTo(l.getX(), l.getY(), l.getZ(), l.getYaw(), l.getPitch());
                    return true;
                }

            } /*
               * else if( subCommand.equals("reload") ) {
               * reloadMerchantConfig(); }
               */

        } catch ( Exception e ) {
            sender.sendMessage("An error occured.");
            System.out.println("NPC Merchant: error: " + e.getMessage()
                    + e.getStackTrace().toString());
            e.printStackTrace();
            return true;
        }

        return true;
    }

}
