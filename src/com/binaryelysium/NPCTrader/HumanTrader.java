package com.binaryelysium.NPCTrader;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.bukkit.entity.Player;

import redecouverte.npcspawner.BasicHumanNpc;

public class HumanTrader extends BasicHumanNpc{
	// map of items for sale and what their value is
	private HashMap<String, List<ItemValuePair> > mPrices;
	// map of ongoing transactions
	private HashMap<String, Transaction> mTransactions = new HashMap<String, Transaction>();

	public HumanTrader(HashMap<String, List<ItemValuePair> > prices) {
		super() // TODO
		mPrices = prices;
	}
	
	public HumanTrader() {
		 mPrices = new HashMap<String, List<ItemValuePair> >();
	}
	
	public void leftClicked(Player player) {
		Transaction t = null;
        if( mTransactions.containsKey( player.getDisplayName() ) ) {
    		t = mTransactions.get( player.getDisplayName() );
    	} else {
    		t = new Transaction( player, this );
    		mTransactions.put( player.getDisplayName(), t );
    	}
        if( !t.next() ) 
        	mTransactions.remove(player.getDisplayName() );
	}
	
	public void rightClicked(Player player) {
		Transaction t = null;
        if( !mTransactions.containsKey( player.getDisplayName() ) ) {
    		t = new Transaction( player, this );
    		mTransactions.put( player.getDisplayName(), t );
            if( !t.next() ) 
            	mTransactions.remove(player.getDisplayName() );
    	} else {
	        t = mTransactions.get( player.getDisplayName() );
	        
	        t.complete();
	        mTransactions.remove(player.getDisplayName() );

    	}
	}
	
	public Iterator<Map.Entry<String, List<ItemValuePair>>> getPricesIterator() {
		return mPrices.entrySet().iterator();
	}
	
	public List<ItemValuePair> getItemValue( String item ) {
		return mPrices.get( item );
	}
	
}