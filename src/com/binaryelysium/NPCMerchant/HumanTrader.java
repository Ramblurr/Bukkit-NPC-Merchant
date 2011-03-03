package com.binaryelysium.NPCMerchant;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.bukkit.entity.Player;

import redecouverte.npcspawner.BasicHumanNpc;

import com.binaryelysium.NPCMerchant.util.Util;

public class HumanTrader {
    private String                               mName;
    // map of items for sale and what their value is
    private HashMap<String, List<ItemValuePair>> mPrices;
    // map of ongoing transactions
    private HashMap<String, Transaction>         mTransactions = new HashMap<String, Transaction>();
    private NPCMerchant                          mPlugin;

    public HumanTrader( NPCMerchant plugin, String name,
            HashMap<String, List<ItemValuePair>> prices ) {
        mPlugin = plugin;
        mPrices = prices;
        mName = name;
    }

    public void leftClicked( Player player ) {
        Transaction t = null;
        if ( mTransactions.containsKey( player.getDisplayName() ) ) {
            t = mTransactions.get( player.getDisplayName() );
        } else {
            t = new Transaction( player, this );
            mTransactions.put( player.getDisplayName(), t );
        }
        t.next();
    }

    public void rightClicked( Player player ) {
        Transaction t = null;
        if ( !mTransactions.containsKey( player.getDisplayName() ) ) {
            t = new Transaction( player, this );
            mTransactions.put( player.getDisplayName(), t );
            if ( !t.next() )
                mTransactions.remove( player.getDisplayName() );
        } else {
            t = mTransactions.get( player.getDisplayName() );

            t.complete();

        }
    }

    public Iterator<Map.Entry<String, List<ItemValuePair>>> getPricesIterator() {
        return mPrices.entrySet().iterator();
    }

    public List<ItemValuePair> getItemValue( String item ) {
        return mPrices.get( item );
    }

    public String getName() {
        return mName;
    }

    public void say( Player player, String msg ) {
        player.sendMessage( getChatPrefix() + msg );
    }

    public void yell( String msg, double max_distance ) {
        BasicHumanNpc me = getNPC();
        if ( me == null )
            return;

        Player[] all_players = mPlugin.getServer().getOnlinePlayers();
        for ( Player p : all_players ) {
            double distance = Util.distance( me.getBukkitEntity().getLocation(),
                    p.getLocation(), true );

            if ( distance <= max_distance )
                say( p, msg );
        }
    }

    public void broadcast( String msg ) {
        mPlugin.getServer().broadcastMessage( getChatPrefix() + msg );
    }

    private BasicHumanNpc getNPC() {
        return mPlugin.HumanNPCList.get( mName );
    }

    private String getChatPrefix() {
        return "[NPC] <" + getName() + "> ";
    }
}
