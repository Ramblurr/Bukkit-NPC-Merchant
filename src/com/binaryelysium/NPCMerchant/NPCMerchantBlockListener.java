package com.binaryelysium.NPCMerchant;

import org.bukkit.event.block.BlockListener;

/**
 * NPCMerchant block listener
 * 
 * @author CaseyLink
 */
public class NPCMerchantBlockListener extends BlockListener {
    private final NPCMerchant plugin;

    public NPCMerchantBlockListener( final NPCMerchant plugin ) {
        this.plugin = plugin;
    }

    // put all Block related code here
}
