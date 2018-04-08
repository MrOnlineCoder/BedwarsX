package com.mronlinecoder.bedwarsx;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.GameReloadEvent;
import org.spongepowered.api.event.game.state.GamePostInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.game.state.GameStoppingEvent;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import com.mronlinecoder.bedwarsx.command.PlayBedwars;
import com.mronlinecoder.bedwarsx.command.Save;
import com.mronlinecoder.bedwarsx.command.SetBed;
import com.mronlinecoder.bedwarsx.command.SetGenerator;
import com.mronlinecoder.bedwarsx.command.SetInteger;
import com.mronlinecoder.bedwarsx.command.SetLobby;
import com.mronlinecoder.bedwarsx.command.SetTeamResource;
import com.mronlinecoder.bedwarsx.command.SetTeamSpawn;
import com.mronlinecoder.bedwarsx.command.Stop;

import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;

@Plugin( id = "bedwarsx", name = "BedwarsX", version = "1.0")
public class BedwarsX {

	@Inject
	private Logger logger;

	@Inject @DefaultConfig(sharedRoot = true)
	ConfigurationLoader <CommentedConfigurationNode> loader;
	@Inject @DefaultConfig(sharedRoot = true)
	Path path;
	
	public BWConfig config;
	public Game game;

	public void createCommands() {		
		CommandSpec setlobby = CommandSpec.builder()
			    .permission("bedwarsx.setlobby")
			    .description(Text.of("Sets Bedwars Lobby"))
			    .executor(new SetLobby(this))
			    .build();
		
		CommandSpec setspawn = CommandSpec.builder()
			    .permission("bedwarsx.setspawn")
			    .arguments(GenericArguments.choices(Text.of("team"), Utils.getTeamColors()))
			    .description(Text.of("Sets Bedwars team spawnpoint"))
			    .executor(new SetTeamSpawn(this))
			    .build();
		
		CommandSpec setbed = CommandSpec.builder()
			    .permission("bedwarsx.setbed")
			    .arguments(GenericArguments.choices(Text.of("team"), Utils.getTeamColors()))
			    .description(Text.of("Sets Bedwars team bed"))
			    .executor(new SetBed(this))
			    .build();
		
		CommandSpec setres = CommandSpec.builder()
			    .permission("bedwarsx.setresource")
			    .arguments(GenericArguments.choices(Text.of("team"), Utils.getTeamColors()))
			    .description(Text.of("Sets Bedwars team resources spawnpoint"))
			    .executor(new SetTeamResource(this))
			    .build();
		
		CommandSpec save = CommandSpec.builder()
			    .permission("bedwarsx.save")
			    .description(Text.of("Saves new Bedwars config"))
			    .executor(new Save(this))
			    .build();
		
		CommandSpec stop = CommandSpec.builder()
			    .permission("bedwarsx.stop")
			    .description(Text.of("Stop current Bedwars game"))
			    .executor(new Stop(this))
			    .build();
		
		HashMap<String, ItemType> genTypes = new HashMap<>();
		genTypes.put("diamond", ItemTypes.DIAMOND);
		genTypes.put("emerald", ItemTypes.EMERALD);
		
		CommandSpec setgen = CommandSpec.builder()
			    .permission("bedwarsx.setgenerator")
			    .arguments(GenericArguments.string(Text.of("name")),
			    GenericArguments.choices(Text.of("type"), genTypes))
			    		
			    .description(Text.of("Set Bedwars item generator"))
			    .executor(new SetGenerator(this))
			    .build();
		
		HashMap<String, String> intTypes = new HashMap<>();
		intTypes.put("playersPerTeam", "playersPerTeam");
		intTypes.put("goldTime", "goldTime");
		intTypes.put("respawnTime", "respawnTime");
		intTypes.put("players", "players");
		intTypes.put("lobbyTime", "lobbyTime");
		
		CommandSpec setint = CommandSpec.builder()
			    .permission("bedwarsx.setinteger")
			    .arguments(GenericArguments.choices(Text.of("key"), intTypes),
			    GenericArguments.integer(Text.of("data")))
			    .description(Text.of("Set Bedwars general config option"))
			    .executor(new SetInteger(this))
			    .build();
		
		CommandSpec mainCmd = CommandSpec.builder()
			    .child(setlobby, "setlobby", "sl")
			    .child(setspawn, "setspawn", "ss")
			    .child(setres, "setresource", "sr")
			    .child(setbed, "setbed", "sb")
			    .child(setint, "setinteger", "setint", "si")
			    .child(setgen, "setgenerator", "setgen", "sg")
			    .child(save, "save")
			    .child(stop, "stop", "end")
			    .description(Text.of("Bedwars Admin command"))
			    .build();
		
		Sponge.getCommandManager().register(this, mainCmd, "bedwars", "bedwarsx", "bwx");
		
		CommandSpec playCmd = CommandSpec.builder()
			    .description(Text.of("Play Bedwars game!"))
			    .executor(new PlayBedwars(this))
			    .build();
		
		Sponge.getCommandManager().register(this, playCmd, "playbedwars", "playbw", "joinbw");
	}
	
	public void sendMessage(Player pl, Text msg) {
		pl.sendMessage(Text.of(TextColors.GREEN, "[BedwarsX] ", TextColors.GOLD, msg));
	}
	
	@Listener
	public void onServerStart(GameStartedServerEvent event) {
		logger.info("BedwarsX by MrOnlineCoder is starting...");
		
		logger.info("Loading Bedwars configuration...");
		config = new BWConfig();
		
		boolean ok = true;
		
		try {
			config.loadConfig(this, loader, path);
		} catch (IOException e) {
			logger.error("Couldn't load configuration - file exception!");
			e.printStackTrace();
			ok = false;
		}

		if (ok) {
			logger.info("Creating Game instance...");
			game = new Game(this);
			
			logger.info("Creating commands...");
			createCommands();
		} else {
			logger.error("BedwarsX failed to load, plugin disabled.");
		}
		
		logger.info("BedwarsX loaded and ready to use.");
	}

	@Listener
	public void onReload(GameReloadEvent e) {
		logger.info("BedwarsX reloaded");
	}

	@Listener
	public void onStop(GameStoppingEvent event) {
		logger.info("Stopped BedwarsX.");
	}

	public Logger getLogger() {
		return logger;
	}
}