package com.binaryelysium.NPCMerchant;

import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerListener;

/**
 * Handle events for all Player related events
 * 
 * @author CaseyLink
 */
public class NPCMerchantPlayerListener extends PlayerListener {
    private final NPCMerchant plugin;

    public NPCMerchantPlayerListener( NPCMerchant instance ) {
        plugin = instance;
    }

    @Override
    public void onPlayerJoin( PlayerEvent event ) {
        System.out.println(event.getPlayer().getName() + " joined the server! :D");
    }
}
