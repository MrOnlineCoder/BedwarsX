package com.mronlinecoder.bedwarsx;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.manipulator.mutable.item.EnchantmentData;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.effect.sound.SoundCategories;
import org.spongepowered.api.effect.sound.SoundType;
import org.spongepowered.api.effect.sound.SoundTypes;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.Item;
import org.spongepowered.api.entity.explosive.PrimedTNT;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.entity.projectile.explosive.fireball.LargeFireball;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.entity.DamageEntityEvent;
import org.spongepowered.api.event.entity.InteractEntityEvent;
import org.spongepowered.api.event.item.inventory.ChangeInventoryEvent;
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent;
import org.spongepowered.api.event.item.inventory.InteractItemEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.item.FireworkEffect;
import org.spongepowered.api.item.FireworkShapes;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.InventoryArchetypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.item.inventory.property.InventoryDimension;
import org.spongepowered.api.item.inventory.property.InventoryTitle;
import org.spongepowered.api.item.inventory.query.QueryOperationTypes;
import org.spongepowered.api.item.inventory.transaction.SlotTransaction;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;
import org.spongepowered.api.text.title.Title;
import org.spongepowered.api.util.Color;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.google.common.collect.Lists;

public class Game {
	BedwarsX bedwars;
	
	GameState state;
	
	//LOBBY STUFF
	ArrayList<Player> lobby;
	Task lobbyTickTask;
	int lobbyTime;
	//LOBBY STUFF END
	
	//GAME STUFF
	Task gameTickTask;
	ArrayList<Team> teams;
	HashSet<Location<World>> placed;
	HashMap<Player, Integer> players;
	List<Generator> generators;
	
	//Permanent items
	HashMap<Player, ItemType> armor;
	HashMap<Player, Boolean> shears;
	//GAME STUFF END
	
	Inventory shopInv;
	
	//MISC STUFF
	public HashMap<Player, TextColor> bedSet;
	
	HashMap<ItemType, ItemPrice> prices;
	
	public Game(BedwarsX plugin) {		
		bedwars = plugin;
		
		reset();
		
		Sponge.getEventManager().registerListeners(bedwars, this);
	}
	
	public void lobbyTick() {
		String timeStrEnd = " seconds.";
		if (lobbyTime == 1) timeStrEnd = " second.";
		
		if (lobbyTime > 0) broadcastToLobby(Text.of(TextColors.YELLOW, "Game starting in ", TextColors.GREEN, lobbyTime, TextColors.YELLOW, timeStrEnd));
		
		lobbyTime--;
		
		if (lobbyTime == -1) {
			start();
			
			lobbyTickTask.cancel();
		}
	}
	
	public void spawnResource(Location<World> loc, ItemType res) {
		 Entity optItem = loc.getExtent().createEntity(EntityTypes.ITEM, loc.getPosition());
		 if (optItem != null) {
		    Item item = (Item) optItem;
		    ItemStack stack = ItemStack.builder().itemType(res).quantity(1).build();
		    item.offer(Keys.REPRESENTED_ITEM, stack.createSnapshot());
		    loc.getExtent().spawnEntity(item);
		 }
	}
	
	public void spawnTNT(Location<World> loc) {
		 PrimedTNT tnt = (PrimedTNT) loc.getExtent().createEntity(EntityTypes.PRIMED_TNT, loc.getPosition());
		 loc.getExtent().spawnEntity(tnt);
	}
	
	public void spawnFireball(Player pl) {
		 LargeFireball f = (LargeFireball) pl.getLocation().getExtent().createEntity(EntityTypes.FIREBALL, pl.getLocation().getPosition());
		 pl.getWorld().spawnEntity(f);
	}
	
	public void gameTick() {
		Team temp = teams.get(0);
		temp.getResource().getExtent().getProperties().setWorldTime(6000);
		
		for (Team t : teams) {
			t.changeGoldTime(-1);
			
			if (t.getGoldTime() == 0) {
				spawnResource(t.getResource(), ItemTypes.GOLD_INGOT);
				t.changeGoldTime(bedwars.config.getGoldTime());
			}
			
			spawnResource(t.getResource(), ItemTypes.IRON_INGOT);
		}
		
		for (Generator gen : generators) {
			gen.update();
		}
	}
	
	public void start() {
		state = GameState.PLAYING;
		
		Title startTitle = Title.builder().title(Text.of(TextColors.GREEN, "The game has started!")).build();
		
		int tidx = 0;
		
		resetTeams();
		
		for (Player pl : lobby) {
			pl.sendTitle(startTitle);
			pl.offer(Keys.GAME_MODE, GameModes.SURVIVAL);
			
			teams.get(tidx).addPlayer(pl);
			
			players.put(pl, tidx);
			
			if (teams.get(tidx).getPlayers().size() >= bedwars.config.getPlayersPerTeam()) {
				tidx++;
			}
		}
		
		for (Team t : teams) {
			t.changeGoldTime(bedwars.config.getGoldTime());
			
			for (Player p : t.getPlayers()) {
				p.setLocation(t.getSpawn());
				p.getInventory().clear();
				p.offer(Keys.DISPLAY_NAME, Text.of(t.getColor(), p.getName()));
				equipPlayer(p);
			}
		}
		
		lobby.clear();
		
		broadcastSound(SoundTypes.ENTITY_ENDERDRAGON_GROWL);
		
		Task.Builder task = Task.builder();
		gameTickTask = task.execute(new Runnable() {
			
			@Override
			public void run() {
				gameTick();
			}
		}).intervalTicks(20).delayTicks(20).submit(bedwars);
	}

	public void reset() {
		state = GameState.WAITING;
		
		lobby = new ArrayList<>();
		teams = new ArrayList<>();
		placed = new HashSet<>();
		bedSet = new HashMap<>();
		players = new HashMap<>();
		prices = new HashMap<>();
		armor = new HashMap<>();
		shears = new HashMap<>();
		
		Utils.fillPrices(prices);
		
		bedwars.getLogger().info("Loaded "+prices.size()+" prices.");
		
		generators = bedwars.config.getGenerators();
		
		shopInv = Inventory.builder()
				.of(InventoryArchetypes.CHEST)
				.property(InventoryDimension.PROPERTY_NAME, new InventoryDimension(9, 6))
				.property(InventoryTitle.PROPERTY_NAME, InventoryTitle.of(Text.of(TextColors.DARK_BLUE, "Bedwars Shop")))
				.build(bedwars);
		
		Utils.fillShopInventory(shopInv);
	}
	
	public void giveLeatherArmor(Player pl) {
		ItemStack leatherHelmet = ItemStack.of(ItemTypes.LEATHER_HELMET, 1);
		ItemStack leatherChest = ItemStack.of(ItemTypes.LEATHER_CHESTPLATE, 1);
		ItemStack leatherLeggs = ItemStack.of(ItemTypes.LEATHER_LEGGINGS, 1);
		ItemStack leatherBoots = ItemStack.of(ItemTypes.LEATHER_BOOTS, 1);
		
		Team t = getPlayerTeam(pl);
		Color c = t.getColor().getColor();
		
		leatherHelmet.offer(Keys.COLOR, c);
		leatherChest.offer(Keys.COLOR, c);
		leatherLeggs.offer(Keys.COLOR, c);
		leatherBoots.offer(Keys.COLOR, c);
		
		pl.setHelmet(leatherHelmet);
		pl.setChestplate(leatherChest);
		pl.setLeggings(leatherLeggs);
		pl.setBoots(leatherBoots);
	}
	
	public void equipPlayerArmor(Player pl) {
		giveLeatherArmor(pl);
		
		if (armor.get(pl) != null) {
			ItemType t = armor.get(pl);
			
			ItemStack leggs = null;
			ItemStack boots = null;
			
			if (t == ItemTypes.CHAINMAIL_BOOTS) {
				leggs = ItemStack.of(ItemTypes.CHAINMAIL_LEGGINGS, 1);
				boots = ItemStack.of(ItemTypes.CHAINMAIL_BOOTS, 1);
			}
			
			if (t == ItemTypes.IRON_BOOTS) {
				leggs = ItemStack.of(ItemTypes.IRON_LEGGINGS, 1);
				boots = ItemStack.of(ItemTypes.IRON_BOOTS, 1);
			}
			
			if (t == ItemTypes.DIAMOND_BOOTS) {
				leggs = ItemStack.of(ItemTypes.DIAMOND_LEGGINGS, 1);
				boots = ItemStack.of(ItemTypes.DIAMOND_BOOTS, 1);
			}
			
			if (leggs != null && boots != null) {
				pl.setLeggings(leggs);
				pl.setBoots(boots);
			}
		}
	}
	
	public void equipPlayer(Player pl) {
		equipPlayerArmor(pl);
		
		if (shears.get(pl) != null) {
			pl.getInventory().offer(ItemStack.of(ItemTypes.SHEARS, 1));
		}
		
		pl.setItemInHand(HandTypes.MAIN_HAND, ItemStack.builder().itemType(ItemTypes.WOODEN_SWORD).quantity(1).build());
	}
	
	public void resetTeams() {
		for (TextColor color : Utils.getTeamColors().values()) {
			if (!bedwars.config.getTeamSpawn(color.getName()).isPresent()) continue;
			
			Team t = new Team(color, bedwars.config.getTeamSpawn(color.getName()).get());
			
			t.setResource(bedwars.config.getTeamResource(color.getName()));
			
			teams.add(t);
		}
	}
	
	public boolean isPlayerAlreadyPlaying(Player pl) {
		return lobby.contains(pl);
	}
	
	public void startLobbyTask() {
		lobbyTime = bedwars.config.getLobbyTime();
		
		Task.Builder task = Task.builder();
		lobbyTickTask = task.execute(new Runnable() {
			
			@Override
			public void run() {
				lobbyTick();
			}
		}).intervalTicks(20).delayTicks(20).submit(bedwars);
	}
	
	public void addToLobby(Player pl) {
		pl.setLocation(bedwars.config.getLobby());
		pl.offer(Keys.GAME_MODE, GameModes.ADVENTURE);
		
		lobby.add(pl);
		
		pl.getInventory().clear();
		
		broadcastToLobby(Text.of(TextColors.GREEN, pl.getName(), TextColors.YELLOW, " joined the game (", TextColors.AQUA, lobby.size(), "/", bedwars.config.getPlayers(), TextColors.YELLOW, ")"));
		
		if (lobby.size() == bedwars.config.getPlayers()) {
			startLobbyTask();
		}
	}
	
	public boolean canJoin() {
		return state == GameState.WAITING && lobby.size() < bedwars.config.getPlayers();
	}
	
	public void broadcastToLobby(Text text) {
		for (Player pl : lobby) {
			pl.sendMessage(text);
		}
	}
	
	public void broadcastToGame(Text text) {
		for (Team t : teams) {
			for (Player p : t.getPlayers()) {
				p.sendMessage(text);
			}
		}
	}
	
	public void broadcastSound(SoundType type) {
		for (Team t : teams) {
			for (Player p : t.getPlayers()) {
				p.playSound(type, SoundCategories.MASTER, p.getLocation().getPosition(), 1);
			}
		}
	}
	
	public void resetPlayer(Player pl) {
		pl.getInventory().clear();
		
		Team t = getPlayerTeam(pl);
		pl.setLocation(t.getSpawn());
		
		pl.offer(Keys.HEALTH, pl.getHealthData().maxHealth().get());
		pl.offer(Keys.GAME_MODE, GameModes.SURVIVAL);
		
		equipPlayer(pl);
	}
	
	public Team getPlayerTeam(Player pl) {	
		return teams.get(players.get(pl));
	}
	
	public void handlePlayerDeath(Player pl, Object src, boolean isFinalKill) {
		Text deathMsg = Utils.getDeathMessage(pl, src);
		Text plName = Text.of(teams.get(players.get(pl)).getColor(), pl.getName());
		
		if (isFinalKill) {
			broadcastToGame(Text.of(plName, TextColors.GRAY, deathMsg, TextColors.AQUA, " FINAL KILL!"));
		} else {
			broadcastToGame(Text.of(plName, TextColors.GRAY, deathMsg));
		}
		
		Title deadTitle = Title.builder()
				.title(Text.of(TextColors.RED, "You died!"))
				.subtitle(Text.of(TextColors.GRAY, "You will respawn in ", TextColors.YELLOW, bedwars.config.getRespawnTime(), TextColors.GRAY, " seconds."))
				.build();
		pl.sendTitle(deadTitle);
		
		resetPlayer(pl);
		pl.offer(Keys.GAME_MODE, GameModes.SPECTATOR);
		
		Task respawnTask = Task.builder().execute(new Runnable() {
			
			@Override
			public void run() {
				resetPlayer(pl);
			}
		}).delayTicks(bedwars.config.getRespawnTime() * 20).submit(bedwars);
	}
	
	public boolean handleBedBreak(Player pl, BlockSnapshot b) {
		Optional<String> tso = bedwars.config.getTeamForBed(b.getLocation().get());
		
		if (!tso.isPresent()) return true;
		
		String ts = tso.get();
		
		String teamName = Utils.capitalize(ts);
		TextColor teamColor = Sponge.getRegistry().getType(TextColor.class, teamName.toUpperCase()).get();
		
		TextColor dcolor = teams.get(players.get(pl)).getColor();
		
		if (dcolor == teamColor) {
			pl.sendMessage(Text.of(TextColors.WHITE, "(!) ", TextColors.RED, "Hey! You can't break your bed!")); 
			return false;
		}
		
		broadcastToGame(Text
				.builder("====== BED DESTRUCTION ======")
				.color(TextColors.WHITE)
				.style(TextStyles.BOLD)
				.append(Text.of('\n', TextColors.WHITE, " > ", teamColor, teamName, TextColors.WHITE, " bed was destroyed by ", dcolor, pl.getName()))
				.build());
		
		Title nobedTitle = Title.builder()
				.title(Text.of(TextColors.RED, "YOUR BED HAS BEEN DESTROYED!"))
				.subtitle(Text.of(TextColors.GRAY, "You can no longer respawn."))
				.build();
		
		for (Team t : teams) {
			if (t.getColor() == teamColor) {
				t.setHasBed(false);
				for (Player p : t.getPlayers()) {
					p.sendTitle(nobedTitle);
				}
				break;
			}
		}
		
		broadcastSound(SoundTypes.ENTITY_WITHER_DEATH);
		return true;
	}
	
	private boolean withdrawItems(Player pl, ItemStack stack) {

		if (!pl.getInventory().contains(stack)) return false;
		
		pl.getInventory().query(QueryOperationTypes.ITEM_STACK_IGNORE_QUANTITY.of(stack)).poll(stack.getQuantity());
		
		return true;
	}
	
	public ItemStack getItemPrice(ItemType it) {
		int q;
		ItemType type;
		
		ItemPrice price = prices.get(it);
		q = price.amount;
		type = price.type;
		
		return ItemStack.of(type, q);
	}
	

	private void handleItemBuy(Player pl, ItemStackSnapshot original) {		
		if (!original.get(Keys.ITEM_LORE).isPresent()) return;
		
		ItemStack price = getItemPrice(original.getType());
		
		Team t = getPlayerTeam(pl);
		
		if (!withdrawItems(pl, price)) {
			pl.sendMessage(Text.of(TextColors.RED, "You do not have enough materials to buy this item!"));
			return;
		}
		
		ItemStack item = ItemStack.of(original.getType(), original.getQuantity());
		
		if (item.getType() == ItemTypes.WOOL) {
			item.offer(Keys.DYE_COLOR, Utils.toDye(t.getColor()));
		}
		
		if (item.getType() == ItemTypes.SHEARS) {
			if (shears.get(pl) != null) {
				pl.sendMessage(Text.of(TextColors.RED, "You have already bought this."));
				return;
			}
			
			shears.put(pl, true);
			pl.getInventory().offer(ItemStack.of(ItemTypes.SHEARS, 1));
			pl.sendMessage(Text.of(TextColors.GREEN, "You bought ", TextColors.YELLOW, "Permanent Shears"));
			return;
		}
		
		if (item.getType() == ItemTypes.CHAINMAIL_BOOTS || item.getType() == ItemTypes.IRON_BOOTS || item.getType() == ItemTypes.DIAMOND_BOOTS)	{
			
			if (armor.get(pl) == item.getType()) {
				pl.sendMessage(Text.of(TextColors.RED, "You have already bought this armor."));
				return;
			}
			
			armor.put(pl, item.getType());
			equipPlayerArmor(pl);
			return;
		}
		
		if (item.getType() == ItemTypes.BOW || item.getType() == ItemTypes.STICK) {
			item.offer(original.createStack().getOrCreate(EnchantmentData.class).get());
		}
		
		pl.getInventory().offer(item);
		
		pl.sendMessage(Text.of(TextColors.GREEN, "You bought ", TextColors.YELLOW, original.getType()));
	}
	
	public void eliminatePlayer(Player pl) {
		Title doneTitle = Title.builder()
				.subtitle(Text.of(TextColors.YELLOW, "You are out of the game!"))
				.build();
		
		resetPlayer(pl);
		
		pl.offer(Keys.GAME_MODE, GameModes.SPECTATOR);
		pl.sendTitle(doneTitle);
		
		removePlayer(pl);
	}
	
	public boolean isPlayerInGame(Player pl) {
		return players.get(pl) != null;
	}
	
	public void stop() {
		if (state == GameState.WAITING) return;
		
		lobbyTickTask.cancel();
		gameTickTask.cancel();
		
		for (Team t : teams) {
			for (Player p : t.getPlayers()) {
				p.offer(Keys.GAME_MODE, GameModes.SURVIVAL);
				p.setLocation(p.getWorld().getSpawnLocation());
			}
		}
		
		for (Location<World> l : placed) {
			l.setBlockType(BlockTypes.AIR);
		}
		
		reset();
	}
	
	private void removePlayer(Player pl) {
		Team t = teams.get(players.get(pl));
		TextColor c = t.getColor();
		t.players.remove(pl);
		players.remove(pl);
		
		if (t.players.size() == 0) {
			teams.remove(t);
			
			broadcastToGame(Text.of(Text.of(TextColors.WHITE, "TEAM ELIMINATED > ", c, Utils.capitalize(c.getName()), TextColors.WHITE, " team was eliminated!")));
			
			checkWin();
		}
	}
	
	private void checkWin() {
		if (teams.size() == 1) {	
			for (Player p : teams.get(0).getPlayers()) {
				Title victoryTitle = Title.builder()
						.title(Text.of(TextColors.GOLD, "VICTORY!"))
						.subtitle(Text.of(TextColors.GRAY, "Your team won the game!"))
						.build();
				
				broadcastSound(SoundTypes.ENTITY_GENERIC_EXPLODE);
				p.sendTitle(victoryTitle);
				
				FireworkEffect fireworkEffect = FireworkEffect.builder()
		                .colors(TextColors.GREEN.getColor())
		                .shape(FireworkShapes.BALL)
		                .build();

		        Entity firework = p.getWorld().createEntity(EntityTypes.FIREWORK, p.getLocation().getPosition());
		        firework.offer(Keys.FIREWORK_EFFECTS, Lists.newArrayList(fireworkEffect));
		        firework.offer(Keys.FIREWORK_FLIGHT_MODIFIER, 1);

		        p.getWorld().spawnEntity(firework);
			}
			
			Task.builder().execute(new Runnable() {
				@Override
				public void run() {
					stop();
				}
			}).delayTicks(20 * 3).submit(bedwars);
		}
	}
	
	@Listener
	public void onGameLeave(ClientConnectionEvent.Disconnect e) {
		Player pl = e.getTargetEntity();
		
		if (isPlayerInGame(pl)) {
			removePlayer(pl);
			checkWin();
		}
	}

	@Listener
	public void onLobbyLeave(ClientConnectionEvent.Disconnect e) {
		Player pl = e.getTargetEntity();
		
		if (lobby.contains(pl)) {
			lobby.remove(pl);
			broadcastToLobby(Text.of(TextColors.GREEN, pl.getName(), TextColors.YELLOW, " quit the game."));
			
			lobbyTickTask.cancel();
		}
	}
	
	@Listener
	public void onPlayerDeath(DamageEntityEvent e) {
		if (state != GameState.PLAYING) return;
		
		if (e.getTargetEntity().getType() != EntityTypes.PLAYER) return;
		
		Player pl = (Player) e.getTargetEntity();
		
		if (pl.get(Keys.HEALTH).get() - e.getFinalDamage() <= 0) {
			e.setCancelled(true);
			
			Team t = getPlayerTeam(pl);
			
			if (!t.hasBed()) {
				handlePlayerDeath(pl, e.getSource(), true);
				
				eliminatePlayer(pl);
			} else {
				handlePlayerDeath(pl, e.getSource(), false);
			}
		}
	}
	
	@Listener
	public void onBlockPlace(ChangeBlockEvent.Place e) {
		if (!(e.getSource() instanceof Player)) return;
		
		Player pl = (Player) e.getSource();
		
		if (!isPlayerInGame(pl)) return;
		
		for (Transaction<BlockSnapshot> t : e.getTransactions()) {
			placed.add(t.getFinal().getLocation().get());
		}
	}
	
	@Listener
	public void onBedPlace(ChangeBlockEvent.Place e) {
		if (!(e.getSource() instanceof Player)) return;
		
		Player pl = (Player) e.getSource();
		
		if (bedSet.get(pl) == null) return;
		
		for (Transaction<BlockSnapshot> t : e.getTransactions()) {
			TextColor c = bedSet.get(pl);
			
			bedwars.config.setTeamBed(c.getName(), t.getFinal().getLocation().get());
			bedwars.sendMessage(pl, Text.of("Bed set for team ",c, c.getName()));
			break;
		}
		
		bedSet.remove(pl);
	}
	
	@Listener
	public void onBlockBreak(ChangeBlockEvent.Break e) {
		if (!(e.getSource() instanceof Player)) return;
		
		Player pl = (Player) e.getSource();
		
		if (!isPlayerInGame(pl)) return;
		
		for (Transaction<BlockSnapshot> t : e.getTransactions()) {
			if (t.getOriginal().getState().getType() == BlockTypes.BED) {
				
				boolean isValid = handleBedBreak(pl, t.getOriginal());;
				t.setValid(isValid);
			} else if (!placed.contains(t.getOriginal().getLocation().get())) {
				t.setValid(false);
				pl.sendMessage(Text.of(TextColors.WHITE, "(!) ", TextColors.RED, "You can only break blocks placed by a player!")); 
			}
		}
	}
	
	@Listener
	public void onBedPickup(ChangeInventoryEvent.Pickup e) {
		if (!(e.getSource() instanceof Player)) return;
		
		Player pl = (Player) e.getSource();
		
		if (!isPlayerInGame(pl)) return;
		
		for (SlotTransaction t : e.getTransactions()) {
			if (t.getFinal().getType() == ItemTypes.BED) {
				t.setCustom(ItemStack.empty());
			}
		}
	}
	
	@Listener
	public void onTrade(InteractEntityEvent.Secondary e) {
		if (e.isCancelled()) return;
		
		if (!(e.getSource() instanceof Player)) return;
		
		Player pl = (Player) e.getSource();
		
		if (e.getTargetEntity().getType() != EntityTypes.VILLAGER) {
			return;
		}
		
		if (!isPlayerInGame(pl)) return;
		
		e.setCancelled(true);
		pl.openInventory(shopInv);
	}
	
	@Listener
	public void onFireball(InteractItemEvent.Secondary e) {
		if (e.isCancelled()) return;
		
		if (!(e.getSource() instanceof Player)) return;
		
		Player pl = (Player) e.getSource();
		
		if (e.getItemStack().getType() != ItemTypes.FIRE_CHARGE) {
			return;
		}
		
		if (e.getItemStack().getQuantity() == 1) {
			pl.setItemInHand(HandTypes.MAIN_HAND, ItemStack.empty());
		} else {
			ItemStack i = e.getItemStack().createStack();
			i.setQuantity(e.getItemStack().getQuantity() - 1);
			pl.setItemInHand(HandTypes.MAIN_HAND, i);
		}
		
		spawnFireball(pl);
	}
	
	@Listener
	public void onClick(ClickInventoryEvent e) {
		if (e.isCancelled()) return;
		
		if (!(e.getSource() instanceof Player)) return;
		
		Player pl = (Player) e.getSource();
		
		if (!isPlayerInGame(pl)) return;
		
		if (!e.getTargetInventory().getName().get().equals(shopInv.getName().get())) {
			return;
		}
		
		e.setCancelled(true);
		
		for (SlotTransaction s : e.getTransactions()) {
			s.setValid(false);
		}
		
		handleItemBuy(pl, e.getCursorTransaction().getFinal());
	}
}
