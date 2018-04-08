package com.mronlinecoder.bedwarsx.command;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import com.mronlinecoder.bedwarsx.BedwarsX;

public class PlayBedwars implements CommandExecutor {
	
	BedwarsX bedwars;
	
	public PlayBedwars(BedwarsX plugin) {
		bedwars = plugin;
	}
	
	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		if (!(src instanceof Player)) {
			throw new CommandException(Text.of(TextColors.RED, "Only for in-game players!"));
		}
		
		Player pl = (Player) src;
		
		if (bedwars.game.isPlayerAlreadyPlaying(pl)) {
			throw new CommandException(Text.of(TextColors.RED, "You are already in Bedwars game!"));
		}
		
		if (!bedwars.game.canJoin()) {
			throw new CommandException(Text.of(TextColors.RED, "Cannot join the game!"));
		}
		
		bedwars.game.addToLobby(pl);
		
		return CommandResult.success();
	}

}
