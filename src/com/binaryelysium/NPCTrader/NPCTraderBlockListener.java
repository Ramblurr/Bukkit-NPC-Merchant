package com.binaryelysium.NPCTrader;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.Material;
import org.bukkit.event.block.BlockCanBuildEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPhysicsEvent;

/**
 * NPCTrader block listener
 * @author CaseyLink
 */
public class NPCTraderBlockListener extends BlockListener {
    private final NPCTrader plugin;

    public NPCTraderBlockListener(final NPCTrader plugin) {
        this.plugin = plugin;
    }

    //put all Block related code here
}
