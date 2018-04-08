package com.mronlinecoder.bedwarsx;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;

public class BWConfig {
	Location<World> lobby;
	
	HashMap<String, Location<World>> teamSpawns;
	HashMap<String, Location<World>> teamResources;
	HashMap<Location<World>, String> teamBeds;
	
	ConfigurationLoader <CommentedConfigurationNode> loader;
	Path path;
	ConfigurationNode rootNode;
	
	boolean ready;
	    
	public void loadConfig(BedwarsX pl, ConfigurationLoader <CommentedConfigurationNode> loader, Path p) throws IOException {
		this.loader = loader;
		this.path = p;
		
	    if (Files.notExists(path)) {
	        Sponge.getAssetManager().getAsset(pl, "default.conf").get().copyToFile(path);
	        ready = false;
	    } else {
	    	ready = true;
	    }
	    
	    rootNode = loader.load();
	}
	
	
	public boolean isReady() {
		return ready;
	}
	
	public BWConfig() {
		lobby = null;
		teamSpawns = new HashMap<>();
		teamResources = new HashMap<>();
		teamBeds = new HashMap<>();
		
	}
	
	private Optional<Location<World>> loadLocation(String section, String arg) {		
		int x = rootNode.getNode(section, arg, "x").getInt();
		int y = rootNode.getNode(section, arg, "y").getInt();
		int z = rootNode.getNode(section, arg, "z").getInt();
		String world = rootNode.getNode(section, arg, "world").getString();
		
		Optional<World> w_ = Sponge.getServer().getWorld(world);
		
		if (!w_.isPresent()) {
			return Optional.empty();
		}
		
		Location<World> loc = new Location<World>(w_.get(), x, y, z);
		
		return Optional.of(loc);
	}
	
	private void saveLocation(String section, String id, Location<World> loc) {
		rootNode.getNode(section, id, "x").setValue(loc.getBlockX());
		rootNode.getNode(section, id, "y").setValue(loc.getBlockY());
		rootNode.getNode(section, id, "z").setValue(loc.getBlockZ());
		rootNode.getNode(section, id, "world").setValue(loc.getExtent().getName());
	}
	
	private void markDirty() {
		ready = false;
	}
	
	public String verify() {
		
		
		return null;
	}
	
	public void saveConfig() throws IOException {
		loader.save(rootNode);
	}
	
	public int getLobbyTime() {
		return rootNode.getNode("lobbyTime").getInt(5);
	}
	
	public int getPlayers() {
		return rootNode.getNode("players").getInt(1);
	}
	
	public Location<World> getLobby() {
		return loadLocation("lobby", "pos").get();
	}
	
	public Optional<Location<World>> getTeamSpawn(String id) {
		return loadLocation("spawns", id);
	}
	
	public Location<World> getTeamResource(String id) {
		return loadLocation("resources", id).get();
	}
	
	public int getPlayersPerTeam() {
		return rootNode.getNode("playersPerTeam").getInt(1);
	}
	
	public int getRespawnTime() {
		return rootNode.getNode("respawnTime").getInt(5);
	}
	
	public int getGoldTime() {
		return rootNode.getNode("goldTime").getInt(10);
	}
	
	public void setInteger(String key, int data) {
		rootNode.getNode(key).setValue(data);
	}
	
	public Optional<String> getTeamForBed(Location<World> bedLoc) {		
		for (TextColor c : Utils.getTeamColors().values()) {
			Optional<Location<World>> _loc = loadLocation("beds", c.getName());
			
			if (_loc.isPresent()) {
				if (_loc.get().equals(bedLoc)) return Optional.of(c.getName());
			}
		}
		
		return Optional.empty();
	}
	
	public void setLobby(Location<World> lobby) {
		saveLocation("lobby", "pos", lobby);
	}
	
	public void setTeamSpawn(String t, Location<World> pos) {
		markDirty();
		
		saveLocation("spawns", t, pos);
	}
	
	public void setGenerator(String name, ItemType type, Location<World> pos) {
		rootNode.getNode("generators", name, "type").setValue(type.getId());
		rootNode.getNode("generators", name, "name").setValue(name);
		saveLocation("generators", name, pos);
	}
	
	public List<Generator> getGenerators() {
		ArrayList<Generator> list = new ArrayList<>();
		
		int sz = rootNode.getNode("generators").getChildrenMap().size();
				
		for (ConfigurationNode node : rootNode.getNode("generators").getChildrenMap().values()) {
			Optional<Location<World>> loc_ = loadLocation("generators", node.getNode("name").getString());
			
			if (loc_.isPresent()) {
				list.add(new Generator(node.getNode("name").getString(), node.getNode("type").getString(), loc_.get()));
			}
		}
		
		return list;
	}
	
	public void setTeamResource(String t, Location<World> pos) {
		markDirty();
		
		saveLocation("resources", t, pos);
	}
	
	public void setTeamBed(String t, Location<World> pos) {
		markDirty();
		
		saveLocation("beds", t, pos);
	}
	
}
