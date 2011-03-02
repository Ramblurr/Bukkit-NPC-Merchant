package com.binaryelysium.NPCTrader;

import redecouverte.npcspawner.BasicHumanNpcList;

import java.util.HashMap;
import java.util.logging.Logger;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.event.entity.*;
import org.bukkit.entity.Player;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.ItemStack;

import redecouverte.npcspawner.BasicHumanNpc;
import redecouverte.npcspawner.NpcEntityTargetEvent;
import redecouverte.npcspawner.NpcEntityTargetEvent.NpcTargetReason;
import redecouverte.npcspawner.NpcSpawner;

public class EEntityListener extends EntityListener {

    private static final Logger logger = Logger.getLogger("Minecraft");
    private final NPCTrader parent;
    private HashMap<String, Integer> mPrices;
    private HashMap<String, Transaction> mTransactions = new HashMap<String, Transaction>();

    public EEntityListener(NPCTrader parent, HashMap<String, Integer> prices) {
        this.parent = parent;
        this.mPrices = prices;
    }

    public void onEntityDamage(EntityDamageEvent event) {
    	
    	if (event instanceof EntityDamageByEntityEvent) {
            EntityDamageByEntityEvent sub = (EntityDamageByEntityEvent)event;
	        if (event.getEntity() instanceof HumanEntity) {
	            BasicHumanNpc npc = parent.HumanNPCList.getBasicHumanNpc(event.getEntity());
	
	            if (npc != null && sub.getDamager() instanceof Player) {
	
	                Player player = (Player) sub.getDamager();
	                
	                
	                event.setCancelled(true);
	            }
	        }
    	}
    }

    @Override
    public void onEntityTarget(EntityTargetEvent event) {

        if (event instanceof NpcEntityTargetEvent) {
            NpcEntityTargetEvent nevent = (NpcEntityTargetEvent)event;

            BasicHumanNpc npc = parent.HumanNPCList.getBasicHumanNpc(event.getEntity());

            if (npc != null && event.getTarget() instanceof Player) {
                if (nevent.getNpcReason() == NpcTargetReason.NPC_RIGHTCLICKED) {
                    Player p = (Player) event.getTarget();
                    
                    
                    
                }
            }
        }

    }

}
