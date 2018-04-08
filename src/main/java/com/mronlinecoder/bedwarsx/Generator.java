package com.mronlinecoder.bedwarsx;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.Item;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

public class Generator {
	
	Location<World> location;
	ItemType type;
	String name;
	
	int spawnTime;
	int time;
	
	public Generator(String name, String type, Location<World> location) {
		this.name = name;
		this.type = Sponge.getRegistry().getType(ItemType.class, type).get();
		this.location = location;
		this.spawnTime = 5;
		this.time = this.spawnTime;
	}
	
	public void update() {
		this.time--;
		
		if (this.time == -1) {
			generateItem();
			this.time = this.spawnTime;
		}
	}
	
	public void generateItem() {
		 Entity optItem = location.getExtent().createEntity(EntityTypes.ITEM, location.getPosition());
		 if (optItem != null) {
		    Item item = (Item) optItem;
		    ItemStack stack = ItemStack.builder().itemType(type).quantity(1).build();
		    item.offer(Keys.REPRESENTED_ITEM, stack.createSnapshot());
		    location.getExtent().spawnEntity(item);
		 }
	}

	public int getSpawnTime() {
		return spawnTime;
	}

	public int getTime() {
		return time;
	}

	public void setSpawnTime(int spawnTime) {
		this.spawnTime = spawnTime;
	}

	public void setTime(int time) {
		this.time = time;
	}

	public String getName() {
		return name;
	}
}
