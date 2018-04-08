package com.mronlinecoder.bedwarsx;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.manipulator.mutable.item.EnchantmentData;
import org.spongepowered.api.data.type.DyeColor;
import org.spongepowered.api.data.type.DyeColors;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.entity.damage.DamageTypes;
import org.spongepowered.api.event.cause.entity.damage.source.DamageSource;
import org.spongepowered.api.event.cause.entity.damage.source.EntityDamageSource;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.enchantment.Enchantment;
import org.spongepowered.api.item.enchantment.EnchantmentTypes;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.property.SlotPos;
import org.spongepowered.api.item.inventory.query.QueryOperationTypes;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;

public class Utils {

	public static HashMap<String, TextColor> getTeamColors() {
		HashMap<String, TextColor> colors = new HashMap<>();
		colors.put(TextColors.RED.getName(), TextColors.RED);
		colors.put(TextColors.BLUE.getName(), TextColors.BLUE);

		colors.put(TextColors.GREEN.getName(), TextColors.GREEN);
		colors.put(TextColors.YELLOW.getName(), TextColors.YELLOW);

		colors.put(TextColors.GRAY.getName(), TextColors.GRAY);
		colors.put(TextColors.WHITE.getName(), TextColors.WHITE);

		colors.put(TextColors.AQUA.getName(), TextColors.AQUA);
		colors.put(TextColors.LIGHT_PURPLE.getName(), TextColors.LIGHT_PURPLE);

		return colors;
	}

	public static String capitalize(String input) {
		return input.substring(0, 1).toUpperCase() + input.substring(1);
	}

	public static Text getDeathMessage(Player pl, Object source) {
		String killer = "";
		DamageSource dmg = (DamageSource) source;
		EntityDamageSource edmg = null;

		if (source instanceof EntityDamageSource) {
			edmg = (EntityDamageSource) source;

			if (edmg.getSource().getType() == EntityTypes.PLAYER) {
				killer = ((Player) edmg.getSource()).getName();
			}
		}

		if (dmg.getType() == DamageTypes.VOID) {
			if (!killer.isEmpty()) {
				return Text.of(" was knocked into the void by ", TextColors.GOLD, killer);
			} else {
				return Text.of(" fell into the void.");
			}

		}

		if (dmg.getType() == DamageTypes.EXPLOSIVE) {
			if (!killer.isEmpty()) {
				return Text.of(" was exploded by ", TextColors.GOLD, killer);
			} else {
				return Text.of(" exploded.");
			}

		}

		if (dmg.getType() == DamageTypes.PROJECTILE) {
			if (!killer.isEmpty()) {
				return Text.of(" was sniped by ", TextColors.GOLD, killer);
			} else {
				return Text.of(" was sniped.");
			}
		}

		if (dmg.getType() == DamageTypes.PROJECTILE) {
			if (!killer.isEmpty()) {
				return Text.of(" was burnt by ", TextColors.GOLD, killer);
			} else {
				return Text.of(" burned away.");
			}
		}

		if (dmg.getType() == DamageTypes.FALL) {
			return Text.of(" fell from a high place.");
		}
		
		if (dmg.getType() == DamageTypes.ATTACK) {
			if (!killer.isEmpty()) {
				return Text.of(" was killed by ", TextColors.GOLD, killer);
			} else {
				return Text.of(" was killed.");
			}
		}

		return Text.of(" died.");
	}

	public static ItemStack createShopItem(Text name, ItemType type, Text lore, int amount) {
		ItemStack stack = ItemStack.builder().itemType(type).quantity(amount).build();

		stack.offer(Keys.DISPLAY_NAME, name);

		List<Text> itemLore = new ArrayList<Text>();
		itemLore.add(lore);
		itemLore.add(Text.of(TextColors.GREEN, "Click to buy"));

		if (!lore.isEmpty())
			stack.offer(Keys.ITEM_LORE, itemLore);

		return stack;
	}
	/*
	 * 
	 * 
	 * ================================================================
	 * 								WARNING!
	 * 			A LOT OF HARD CODE BELOW! YOU HAVE BEEN WARNED!
	 * POST SUGGESTIONS FOR CODE IMPROVEMENT TO ISSUES / PULL REQUESTS!
	 * ================================================================
	 * 
	 *
	 */

	public static void fillShopInventory(Inventory shopInv) {
		ItemStack filler = createShopItem(Text.of(""), ItemTypes.STAINED_GLASS_PANE, Text.EMPTY, 1);

		// Blocks
		ItemStack blocks_wool = createShopItem(Text.of(TextColors.GOLD, "4x Wool"), ItemTypes.WOOL,
				Text.of(TextColors.WHITE, "4x Iron"), 4);

		ItemStack blocks_endstone = createShopItem(Text.of(TextColors.GOLD, "12x Endstone"), ItemTypes.END_STONE,
				Text.of(TextColors.WHITE, "24x Iron"), 12);

		ItemStack blocks_wood = createShopItem(Text.of(TextColors.GOLD, "16x Wooden Planks"), ItemTypes.PLANKS,
				Text.of(TextColors.WHITE, "4x ", TextColors.GOLD, "Gold"), 16);

		ItemStack blocks_ladder = createShopItem(Text.of(TextColors.GOLD, "4x Ladder"), ItemTypes.LADDER,
				Text.of(TextColors.WHITE, "4x Iron"), 4);

		ItemStack blocks_obsidian = createShopItem(Text.of(TextColors.GOLD, "4x Obsidian"), ItemTypes.OBSIDIAN,
				Text.of(TextColors.WHITE, "4x ", TextColors.GREEN, "Emerald"), 4);

		shopInv.query(QueryOperationTypes.INVENTORY_PROPERTY.of(SlotPos.of(1, 0))).offer(blocks_wool);
		shopInv.query(QueryOperationTypes.INVENTORY_PROPERTY.of(SlotPos.of(2, 0))).offer(blocks_endstone);
		shopInv.query(QueryOperationTypes.INVENTORY_PROPERTY.of(SlotPos.of(3, 0))).offer(blocks_wood);
		shopInv.query(QueryOperationTypes.INVENTORY_PROPERTY.of(SlotPos.of(4, 0))).offer(blocks_ladder);
		shopInv.query(QueryOperationTypes.INVENTORY_PROPERTY.of(SlotPos.of(5, 0))).offer(blocks_obsidian);

		// Weapons
		ItemStack wep_stone = createShopItem(Text.of(TextColors.GOLD, "Stone Sword"), ItemTypes.STONE_SWORD,
				Text.of(TextColors.WHITE, "10x Iron"), 1);

		ItemStack wep_iron = createShopItem(Text.of(TextColors.GOLD, "Iron Sword"), ItemTypes.IRON_SWORD,
				Text.of(TextColors.WHITE, "7x ", TextColors.GOLD, "Gold"), 1);

		ItemStack wep_diamond = createShopItem(Text.of(TextColors.GOLD, "Diamond Sword"), ItemTypes.DIAMOND_SWORD,
				Text.of(TextColors.WHITE, "4x ", TextColors.GREEN, "Emerald"), 1);

		ItemStack wep_stick = createShopItem(Text.of(TextColors.GOLD, "Knockback Stick"), ItemTypes.STICK,
				Text.of(TextColors.WHITE, "10x ", TextColors.GOLD, "Gold"), 1);
		
		EnchantmentData ed = wep_stick.getOrCreate(EnchantmentData.class).get();
		
		ed.addElement(Enchantment.of(EnchantmentTypes.KNOCKBACK, 1));
		
		wep_stick.offer(ed);
		
		shopInv.query(QueryOperationTypes.INVENTORY_PROPERTY.of(SlotPos.of(1, 1))).offer(wep_stone);
		shopInv.query(QueryOperationTypes.INVENTORY_PROPERTY.of(SlotPos.of(2, 1))).offer(wep_iron);
		shopInv.query(QueryOperationTypes.INVENTORY_PROPERTY.of(SlotPos.of(3, 1))).offer(wep_diamond);
		shopInv.query(QueryOperationTypes.INVENTORY_PROPERTY.of(SlotPos.of(4, 1))).offer(wep_stick);

		// Tools
		ItemStack tools_shears = createShopItem(Text.of(TextColors.GOLD, "Permanent Shears"), ItemTypes.SHEARS,
				Text.of(TextColors.WHITE, "40x Iron"), 1);

		ItemStack tools_wood = createShopItem(Text.of(TextColors.GOLD, "Wooden Pickaxe"), ItemTypes.WOODEN_PICKAXE,
				Text.of(TextColors.WHITE, "15x Iron"), 1);

		ItemStack tools_stone = createShopItem(Text.of(TextColors.GOLD, "Stone Pickaxe"), ItemTypes.STONE_PICKAXE,
				Text.of(TextColors.WHITE, "30x Iron"), 1);

		ItemStack tools_iron = createShopItem(Text.of(TextColors.GOLD, "Iron Pickaxe"), ItemTypes.IRON_PICKAXE,
				Text.of(TextColors.WHITE, "4x ", TextColors.GOLD, "Gold"), 1);

		ItemStack tools_diamond = createShopItem(Text.of(TextColors.GOLD, "Diamond Pickaxe"), ItemTypes.DIAMOND_PICKAXE,
				Text.of(TextColors.WHITE, "16x ", TextColors.GOLD, "Gold"), 1);

		shopInv.query(QueryOperationTypes.INVENTORY_PROPERTY.of(SlotPos.of(1, 2))).offer(tools_shears);
		shopInv.query(QueryOperationTypes.INVENTORY_PROPERTY.of(SlotPos.of(2, 2))).offer(tools_wood);
		shopInv.query(QueryOperationTypes.INVENTORY_PROPERTY.of(SlotPos.of(3, 2))).offer(tools_stone);
		shopInv.query(QueryOperationTypes.INVENTORY_PROPERTY.of(SlotPos.of(4, 2))).offer(tools_iron);
		shopInv.query(QueryOperationTypes.INVENTORY_PROPERTY.of(SlotPos.of(5, 2))).offer(tools_diamond);
		
		// Armor
		ItemStack armor_chain = createShopItem(Text.of(TextColors.GOLD, "Permanent Chainmail Armor"), ItemTypes.CHAINMAIL_BOOTS,
				Text.of(TextColors.WHITE, "40x Iron"), 1);

		ItemStack armor_iron = createShopItem(Text.of(TextColors.GOLD, "Permanent Iron Armor"), ItemTypes.IRON_BOOTS,
				Text.of(TextColors.WHITE, "12x ", TextColors.GOLD, "Gold"), 1);

		ItemStack armor_diamond = createShopItem(Text.of(TextColors.GOLD, "Permanent Diamond Armor"), ItemTypes.DIAMOND_BOOTS,
				Text.of(TextColors.WHITE, "7x ", TextColors.GREEN, "Emerald"), 1);

		shopInv.query(QueryOperationTypes.INVENTORY_PROPERTY.of(SlotPos.of(1, 3))).offer(armor_chain);
		shopInv.query(QueryOperationTypes.INVENTORY_PROPERTY.of(SlotPos.of(2, 3))).offer(armor_iron);
		shopInv.query(QueryOperationTypes.INVENTORY_PROPERTY.of(SlotPos.of(3, 3))).offer(armor_diamond);
		
		// Ranged
		ItemStack ranged_bow = createShopItem(Text.of(TextColors.GOLD, "Bow"), ItemTypes.BOW,
				Text.of(TextColors.WHITE, "12x ", TextColors.GOLD, "Gold"), 1);

		ItemStack ranged_bow2 = createShopItem(Text.of(TextColors.GOLD, "Bow (Power I)"), ItemTypes.BOW,
				Text.of(TextColors.WHITE, "24x ", TextColors.GOLD, "Gold"), 1);
		
		EnchantmentData rb_data = ranged_bow2.getOrCreate(EnchantmentData.class).get();
		
		rb_data.addElement(Enchantment.of(EnchantmentTypes.POWER, 1));
		
		ranged_bow2.offer(rb_data);

		ItemStack ranged_arrow = createShopItem(Text.of(TextColors.GOLD, "8x Arrow"), ItemTypes.ARROW,
				Text.of(TextColors.WHITE, "2x ", TextColors.GOLD, "Gold"), 8);

		shopInv.query(QueryOperationTypes.INVENTORY_PROPERTY.of(SlotPos.of(1, 4))).offer(ranged_bow);
		shopInv.query(QueryOperationTypes.INVENTORY_PROPERTY.of(SlotPos.of(2, 4))).offer(ranged_bow2);
		shopInv.query(QueryOperationTypes.INVENTORY_PROPERTY.of(SlotPos.of(3, 4))).offer(ranged_arrow);
		
		// Utility
		ItemStack util_fireball = createShopItem(Text.of(TextColors.GOLD, "Fireball"), ItemTypes.FIRE_CHARGE,
				Text.of(TextColors.WHITE, "40x Iron"), 1);

		ItemStack util_tnt = createShopItem(Text.of(TextColors.GOLD, "TNT"), ItemTypes.TNT,
				Text.of(TextColors.WHITE, "4x ", TextColors.GOLD, "Gold"), 1);

		ItemStack util_pearl = createShopItem(Text.of(TextColors.GOLD, "Ender Pearl"), ItemTypes.ENDER_PEARL,
				Text.of(TextColors.WHITE, "4x ", TextColors.GREEN, "Emerald"), 1);
		
		ItemStack util_water = createShopItem(Text.of(TextColors.GOLD, "Water Bucket"), ItemTypes.WATER_BUCKET,
				Text.of(TextColors.WHITE, "4x ", TextColors.GOLD, "Gold"), 1);


		shopInv.query(QueryOperationTypes.INVENTORY_PROPERTY.of(SlotPos.of(1, 5))).offer(util_fireball);
		shopInv.query(QueryOperationTypes.INVENTORY_PROPERTY.of(SlotPos.of(2, 5))).offer(util_tnt);
		shopInv.query(QueryOperationTypes.INVENTORY_PROPERTY.of(SlotPos.of(3, 5))).offer(util_pearl);
		shopInv.query(QueryOperationTypes.INVENTORY_PROPERTY.of(SlotPos.of(4, 5))).offer(util_water);
		
		//fillers
		for (int i = 0; i < 6; i++) {
			shopInv.query(QueryOperationTypes.INVENTORY_PROPERTY.of(SlotPos.of(0, i))).set(filler);
			shopInv.query(QueryOperationTypes.INVENTORY_PROPERTY.of(SlotPos.of(8, i))).set(filler);
		}

	}
	
	public static void fillPrices(HashMap<ItemType, ItemPrice> map) {
		//blocks
		map.put(ItemTypes.WOOL, new ItemPrice(ItemTypes.IRON_INGOT, 4));
		map.put(ItemTypes.END_STONE, new ItemPrice(ItemTypes.IRON_INGOT, 24));
		map.put(ItemTypes.PLANKS, new ItemPrice(ItemTypes.GOLD_INGOT, 4));
		map.put(ItemTypes.LADDER, new ItemPrice(ItemTypes.IRON_INGOT, 4));
		map.put(ItemTypes.OBSIDIAN, new ItemPrice(ItemTypes.EMERALD, 4));
		
		//weapons
		map.put(ItemTypes.STONE_SWORD, new ItemPrice(ItemTypes.IRON_INGOT, 10));
		map.put(ItemTypes.IRON_SWORD, new ItemPrice(ItemTypes.GOLD_INGOT, 7));
		map.put(ItemTypes.DIAMOND_SWORD, new ItemPrice(ItemTypes.EMERALD, 4));
		map.put(ItemTypes.STICK, new ItemPrice(ItemTypes.GOLD_INGOT, 10));
		
		//tools
		map.put(ItemTypes.SHEARS, new ItemPrice(ItemTypes.IRON_INGOT, 40));
		map.put(ItemTypes.WOODEN_PICKAXE, new ItemPrice(ItemTypes.IRON_INGOT, 15));
		map.put(ItemTypes.STONE_PICKAXE, new ItemPrice(ItemTypes.IRON_INGOT, 30));
		map.put(ItemTypes.IRON_PICKAXE, new ItemPrice(ItemTypes.GOLD_INGOT, 4));
		map.put(ItemTypes.DIAMOND_PICKAXE, new ItemPrice(ItemTypes.GOLD_INGOT, 16));
		
		//armor
		map.put(ItemTypes.CHAINMAIL_BOOTS, new ItemPrice(ItemTypes.IRON_INGOT, 40));
		map.put(ItemTypes.IRON_BOOTS, new ItemPrice(ItemTypes.GOLD_INGOT, 12));
		map.put(ItemTypes.DIAMOND_BOOTS, new ItemPrice(ItemTypes.EMERALD, 7));
		
		//ranged
		map.put(ItemTypes.BOW, new ItemPrice(ItemTypes.GOLD_INGOT, 12));
		map.put(ItemTypes.ARROW, new ItemPrice(ItemTypes.GOLD_INGOT, 2));
		//map.put(ItemTypes.WOOL, new ItemPrice(ItemTypes.IRON_INGOT, 4));
		
		//utils
		map.put(ItemTypes.TNT, new ItemPrice(ItemTypes.GOLD_INGOT, 4));
		map.put(ItemTypes.FIRE_CHARGE, new ItemPrice(ItemTypes.IRON_INGOT, 40));
		map.put(ItemTypes.ENDER_PEARL, new ItemPrice(ItemTypes.EMERALD, 4));
		map.put(ItemTypes.WATER_BUCKET, new ItemPrice(ItemTypes.GOLD_INGOT, 4));
	}
	
	public static DyeColor toDye(TextColor c) {
		if (c == TextColors.RED) {
			return DyeColors.RED;
		}
		
		if (c == TextColors.BLUE) {
			return DyeColors.BLUE;
		}
		
		if (c == TextColors.GREEN) {
			return DyeColors.GREEN;
		}
		
		if (c == TextColors.YELLOW) {
			return DyeColors.YELLOW;
		}
		
		if (c == TextColors.AQUA) {
			return DyeColors.CYAN;
		}
		
		if (c == TextColors.LIGHT_PURPLE) {
			return DyeColors.PINK;
		}
		
		if (c == TextColors.WHITE) {
			return DyeColors.WHITE;
		}
		
		if (c == TextColors.GRAY) {
			return DyeColors.GRAY;
		}
		
		return DyeColors.BLACK;
	}
}
