package com.binaryelysium.NPCMerchant;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.WorldEvent;
import org.bukkit.event.world.WorldListener;


public class NPCMerchantWorldListener extends WorldListener {
	private NPCMerchant plugin;
	NPCMerchantWorldListener(NPCMerchant plugin) {
		this.plugin = plugin;
	}
	@Override
	public void onChunkLoaded(ChunkLoadEvent event) {
		plugin.onChunkLoaded(event.getChunk());
	}
	@Override
	public void onWorldLoaded(WorldEvent event) {
	}
}
