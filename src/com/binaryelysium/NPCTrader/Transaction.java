package com.binaryelysium.NPCTrader;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class Transaction {
	private Player mPlayer;
	private HumanTrader mTrader;
	private String mLastItem;
	private Iterator<Map.Entry<String, List<ItemValuePair>>> mCurrIterator = null;

	Transaction(Player player, HumanTrader trader) {
		mPlayer = player;
		mTrader = trader;
	}

	public boolean next() {
		if (mCurrIterator == null) {
			mCurrIterator = mTrader.getPricesIterator();
			mPlayer.sendMessage("Welcome! I have all sorts of rare and fantastic items for trade.");
		}
		if (mCurrIterator.hasNext()) {
			Map.Entry<String, List<ItemValuePair>> pairs = mCurrIterator.next();
			mLastItem = pairs.getKey();
			List<ItemValuePair> list = pairs.getValue();

			String items = "";
			final String delim = ", or ";
			for (ItemValuePair itemval : list) {
				items += itemval.getValue() + " " + itemval.getItem() + delim;
			}
			items = items.substring(0, items.length() - delim.length());
			mPlayer.sendMessage("I'll trade " + mLastItem + " for: " + items);
			return true;
		} else {
			mPlayer.sendMessage("Sorry, that's all I have. Try again later perhaps.");
			return false;
		}
	}

	public void complete() {
		int to_give_id = ItemDB.nameOrIDToID(mLastItem);
		List<ItemValuePair> items = mTrader.getItemValue(mLastItem);

		boolean success = false;
		boolean inventory_full = false; 
		for (ItemValuePair pair : items) {
			String item_to_take = pair.getItem();
			int id_to_take = ItemDB.nameOrIDToID(item_to_take);
			int amt_to_take = pair.getValue();

			ItemStack inHand = mPlayer.getItemInHand();
			int amtInHand = inHand.getAmount();
			if (inHand.getTypeId() == id_to_take && amtInHand >= amt_to_take) {
				ItemStack newItem = new ItemStack(to_give_id, 1);
				if (amtInHand == amt_to_take) {
					mPlayer.setItemInHand(newItem);
				} else {
					PlayerInventory inv = mPlayer.getInventory();
					ItemStack change = new ItemStack(id_to_take, amtInHand
							- amt_to_take);

					HashMap<Integer, ItemStack> leftover = inv.addItem(newItem);
					if (leftover.size() > 0) { // player inventory is full
						inventory_full = true;
					} else {
						mPlayer.setItemInHand(change);
						success = true;
					}
				}
			}
		}
		if( inventory_full )
			mPlayer.sendMessage("Looks like your inventory is full...");
		else if ( success )
			mPlayer.sendMessage("Sounds good, pleasure doing business with you!");
		else 
			mPlayer.sendMessage("You don't seem to have enough to trade.");
	}

}
