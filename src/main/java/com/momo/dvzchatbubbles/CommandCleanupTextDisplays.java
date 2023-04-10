package com.momo.dvzchatbubbles;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;

public class CommandCleanupTextDisplays implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player player)) return false;

		int radius = 5;

		if (args.length > 0) {
			radius = Integer.parseInt(args[0]);
		}

		boolean success = false;
		for (Entity entity : player.getNearbyEntities(radius, radius, radius)) {
			if (entity instanceof TextDisplay) {
				entity.remove();
				success = true;
			}
		}
		return success;
	}

}