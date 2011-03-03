package com.binaryelysium.NPCMerchant;

import java.util.logging.Logger;

import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.event.entity.EntityTargetEvent;

import redecouverte.npcspawner.BasicHumanNpc;
import redecouverte.npcspawner.NpcEntityTargetEvent;
import redecouverte.npcspawner.NpcEntityTargetEvent.NpcTargetReason;

public class EEntityListener extends EntityListener {

    private static final Logger logger = Logger.getLogger("Minecraft");
    private final NPCMerchant   parent;

    public EEntityListener( NPCMerchant parent ) {
        this.parent = parent;
    }

    public void onEntityDamage( EntityDamageEvent event ) {

        if ( event instanceof EntityDamageByEntityEvent ) {
            EntityDamageByEntityEvent sub = (EntityDamageByEntityEvent) event;
            if ( event.getEntity() instanceof HumanEntity ) {
                BasicHumanNpc npc = parent.HumanNPCList.getBasicHumanNpc(event
                        .getEntity());

                if ( npc != null && sub.getDamager() instanceof Player ) {

                    Player player = (Player) sub.getDamager();
                    HumanTrader trader = parent.TraderList.get(npc.getUniqueId());
                    if ( trader != null ) {
                        trader.leftClicked(player);
                    }
                    event.setCancelled(true);
                }
            }
        }
    }

    @Override
    public void onEntityTarget( EntityTargetEvent event ) {

        if ( event instanceof NpcEntityTargetEvent ) {
            NpcEntityTargetEvent nevent = (NpcEntityTargetEvent) event;

            BasicHumanNpc npc = parent.HumanNPCList.getBasicHumanNpc(event.getEntity());

            if ( npc != null && event.getTarget() instanceof Player ) {
                if ( nevent.getNpcReason() == NpcTargetReason.NPC_RIGHTCLICKED ) {
                    Player p = (Player) event.getTarget();

                    HumanTrader trader = parent.TraderList.get(npc.getUniqueId());
                    if ( trader != null ) {
                        trader.rightClicked(p);
                    }

                }
            }
        }

    }

}
