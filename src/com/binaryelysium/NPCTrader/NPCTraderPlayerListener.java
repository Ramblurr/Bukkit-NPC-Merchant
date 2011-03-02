package com.binaryelysium.NPCTrader;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerMoveEvent;

/**
 * Handle events for all Player related events
 * @author CaseyLink
 */
public class NPCTraderPlayerListener extends PlayerListener {
    private final NPCTrader plugin;

    public NPCTraderPlayerListener(NPCTrader instance) {
        plugin = instance;
    }
    
    @Override
    public void onPlayerJoin(PlayerEvent event) {
        System.out.println(event.getPlayer().getName() + " joined the server! :D");
    }
}

