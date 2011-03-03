package com.binaryelysium.NPCMerchant;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.Material;
import org.bukkit.event.block.BlockCanBuildEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPhysicsEvent;

/**
 * NPCMerchant block listener
 * @author CaseyLink
 */
public class NPCMerchantBlockListener extends BlockListener {
    private final NPCMerchant plugin;

    public NPCMerchantBlockListener(final NPCMerchant plugin) {
        this.plugin = plugin;
    }

    //put all Block related code here
}
