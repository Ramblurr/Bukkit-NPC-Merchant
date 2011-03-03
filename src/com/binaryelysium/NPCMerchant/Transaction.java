package com.binaryelysium.NPCMerchant;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class Transaction {
    private Player                                           mPlayer;
    private HumanTrader                                      mTrader;
    private String                                           mLastItem;
    private Iterator<Map.Entry<String, List<ItemValuePair>>> mCurrIterator = null;

    Transaction( Player player, HumanTrader trader ) {
        mPlayer = player;
        mTrader = trader;
    }

    public boolean next() {
        if ( mCurrIterator == null || !mCurrIterator.hasNext() ) {
            mCurrIterator = mTrader.getPricesIterator();
            say("Welcome! I have all sorts of rare and fantastic items for trade. (left click: cycle through items, right click: confirm trade)");
        }
        if ( mCurrIterator.hasNext() ) {
            Map.Entry<String, List<ItemValuePair>> pairs = mCurrIterator.next();
            mLastItem = pairs.getKey();
            List<ItemValuePair> list = pairs.getValue();

            String items = "";
            final String delim = ", or ";
            for ( ItemValuePair itemval : list ) {
                items += itemval.getValue() + " " + itemval.getItem() + delim;
            }
            items = items.substring(0, items.length() - delim.length());
            say("I'll trade " + mLastItem + " for: " + items);
            return true;
        } else {
            say("Sorry, that's all I have. Try again later perhaps.");
            return false;
        }
    }

    public void complete() {
        int to_give_id = ItemDB.nameOrIDToID(mLastItem);
        if ( to_give_id == -1 ) {
            Logger.getLogger("Minecraft").log(Level.SEVERE,
                    "Transaction::complete() Getting mLastItem ID Failed: " + mLastItem);
            return;
        }
        List<ItemValuePair> items = mTrader.getItemValue(mLastItem);

        boolean success = false;
        boolean inventory_full = false;
        for ( ItemValuePair pair : items ) {
            String item_to_take = pair.getItem();
            int id_to_take = ItemDB.nameOrIDToID(item_to_take);
            if ( to_give_id == -1 ) {
                Logger.getLogger("Minecraft").log(
                        Level.SEVERE,
                        "Transaction::complete() Getting id_to_take ID Failed: "
                                + id_to_take);
                return;
            }
            int amt_to_take = pair.getValue();

            ItemStack inHand = mPlayer.getItemInHand();
            int amtInHand = inHand.getAmount();
            if ( inHand.getTypeId() == id_to_take && amtInHand >= amt_to_take ) {
                ItemStack newItem = new ItemStack(to_give_id, 1);
                if ( amtInHand == amt_to_take ) {
                    mPlayer.setItemInHand(newItem);
                    success = true;
                } else {
                    PlayerInventory inv = mPlayer.getInventory();
                    ItemStack change = new ItemStack(id_to_take, amtInHand - amt_to_take);

                    HashMap<Integer, ItemStack> leftover = inv.addItem(newItem);
                    if ( leftover.size() > 0 ) { // player inventory is full
                        inventory_full = true;
                    } else {
                        mPlayer.setItemInHand(change);
                        success = true;
                        mPlayer.updateInventory();
                    }
                }
            }
        }
        if ( inventory_full )
            say("Looks like your inventory is full...");
        else if ( success )
            say("Sounds good, pleasure doing business with you!");
        else
            say("You don't seem to have enough to trade. Please hold it in your hand.");
    }

    private void say( String msg ) {
        mPlayer.sendMessage(mTrader.getName() + "> " + msg);
    }
}
