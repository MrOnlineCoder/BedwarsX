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

public class SetInteger implements CommandExecutor {
	
	BedwarsX bedwars;
	
	public SetInteger(BedwarsX plugin) {
		bedwars = plugin;
	}
	
	
	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		if (!(src instanceof Player)) {
			throw new CommandException(Text.of(TextColors.RED, "Only for in-game players!"));
		}
		
		Player pl = (Player) src;
		
		String key = args.<String>getOne("key").get();
		int data = args.<Integer>getOne("data").get();
		
		bedwars.sendMessage(pl, Text.of("Set config option ", TextColors.GREEN, key, TextColors.GOLD, " to ", TextColors.GREEN, data));
		
		bedwars.config.setInteger(key, data);
		
		return CommandResult.success();
	}
}