package com.mronlinecoder.bedwarsx;

import java.util.ArrayList;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

public class Team {
	TextColor color;
	
	Location<World> spawn;
	Location<World> resource;
	
	ArrayList<Player> players;
	
	boolean hasBed;
	
	int goldTime;
	
	public Team(TextColor color, Location<World> spawn) {
		this.color = color;
		this.spawn = spawn;
		this.players = new ArrayList<>();
		this.hasBed = true;
		this.goldTime = 0;
	}
	
	public int getGoldTime() {
		return goldTime;
	}
	
	public void changeGoldTime(int d) {
		goldTime += d;
	}
	
	public void addPlayer(Player pl) {
		players.add(pl);
	}

	public TextColor getColor() {
		return color;
	}

	public Location<World> getSpawn() {
		return spawn;
	}

	public ArrayList<Player> getPlayers() {
		return players;
	}

	public Location<World> getResource() {
		return resource;
	}

	public void setResource(Location<World> resource) {
		this.resource = resource;
	}

	public boolean hasBed() {
		return hasBed;
	}

	public void setHasBed(boolean hasBed) {
		this.hasBed = hasBed;
	}
}
