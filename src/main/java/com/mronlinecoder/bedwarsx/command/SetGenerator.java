package com.mronlinecoder.bedwarsx.command;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import com.mronlinecoder.bedwarsx.BedwarsX;

public class SetGenerator implements CommandExecutor {
	
	BedwarsX bedwars;
	
	public SetGenerator(BedwarsX plugin) {
		bedwars = plugin;
	}
	
	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		if (!(src instanceof Player)) {
			throw new CommandException(Text.of(TextColors.RED, "Only for in-game players!"));
		}
		
		Player pl = (Player) src;
		
		String name = args.<String>getOne("name").get();
		ItemType type = args.<ItemType>getOne("type").get();
		
		bedwars.sendMessage(pl, Text.of("Created new generator ", TextColors.GREEN, name, TextColors.GOLD, " at your current location."));
		
		bedwars.config.setGenerator(name, type, pl.getLocation());
		
		return CommandResult.success();
	}
}