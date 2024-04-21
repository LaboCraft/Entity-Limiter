package com.expectale.entitylimiter.command;

import com.expectale.entitylimiter.Checker;
import com.expectale.entitylimiter.EntityLimiter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.List;

public class EntityLimiterCommand implements CommandExecutor {

    private void sendHelpMessage(CommandSender sender) {
        sender.sendMessage(ChatColor.YELLOW + "Usage Entity Limiter : ");
        sender.sendMessage(ChatColor.GRAY + "- /ml reload ");
        sender.sendMessage(ChatColor.GRAY + "- /ml check [<player>] [<radius>]");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args) {

        if (sender instanceof Player) {
            if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
                EntityLimiter.getINSTANCE().reloadConfig();
                sender.sendMessage(ChatColor.YELLOW + "The configuration file has been successfully reloaded");
            } else if (args.length >= 1 && args[0].equalsIgnoreCase("check")) {
                Chunk chunk = ((Player)sender).getChunk();
                int radius = 1;
                if (args.length >= 2) {
                    Player player = Bukkit.getPlayer(args[1]);
                    if (player != null) {
                        chunk = player.getChunk();
                    } else {
                        sender.sendMessage(ChatColor.RED + "Can't find player " + args[1]);
                        sendHelpMessage(sender);
                        return true;
                    }
                }
                if (args.length >= 3) {
                    try {
                        radius = Integer.parseInt(args[2]);
                    } catch (Exception exception) {
                        sender.sendMessage(ChatColor.RED + "Invalid radius " + args[2]);
                        sendHelpMessage(sender);
                        return true;
                    }
                }
                List<Chunk> chunks = Checker.getChunksAround(chunk, radius);
                for (EntityType type : EntityLimiter.getINSTANCE().getConfiguration().getEntityType()) {
                    Checker.sendInGameCheck((Player)sender, chunks, type);
                }
            } else {
                sendHelpMessage(sender);
            }
        }
        return true;
    }
}