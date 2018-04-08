package com.mronlinecoder.bedwarsx;

import org.spongepowered.api.item.ItemType;

public class ItemPrice {
	public ItemType type;
	public int amount;
	
	public ItemPrice(ItemType type, int am) {
		this.type = type;
		this.amount = am;
	}
}
