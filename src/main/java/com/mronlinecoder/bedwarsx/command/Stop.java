package com.mronlinecoder.bedwarsx.command;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;

import com.mronlinecoder.bedwarsx.BedwarsX;

public class Stop implements CommandExecutor {
	
	BedwarsX bedwars;
	
	public Stop(BedwarsX plugin) {
		bedwars = plugin;
	}
	
	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		if (!(src instanceof Player)) {
			throw new CommandException(Text.of(TextColors.RED, "Only for in-game players!"));
		}
		
		Player pl = (Player) src;
		
		bedwars.sendMessage(pl, Text.of("Bedwars game stopped."));
		bedwars.game.broadcastToGame(Text.of(TextColors.GREEN, "The game was stopped by server administrator."));
		bedwars.game.stop();
		
		return CommandResult.success();
	}
}