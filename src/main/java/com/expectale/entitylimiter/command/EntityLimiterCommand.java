package com.expectale.entitylimiter.command;

import com.expectale.entitylimiter.Checker;
import com.expectale.entitylimiter.EntityLimiter;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class EntityLimiterCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args) {

        if (sender instanceof Player) {
            if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
                EntityLimiter.getINSTANCE().reloadConfig();
                sender.sendMessage(ChatColor.YELLOW + "The configuration file has been successfully reloaded");
            } else if (args.length == 1 && args[0].equalsIgnoreCase("check")) {
                sender.sendMessage(ChatColor.YELLOW + "SOON");
                for (EntityType type : EntityLimiter.getINSTANCE().getConfiguration().getEntityType()) {
                    Checker.sendInGameCheck((Player)sender, ((Player)sender).getChunk(), type);
                }
            } else {
                sender.sendMessage(ChatColor.YELLOW + "Usage Entity Limiter : ");
                sender.sendMessage(ChatColor.GRAY + "- /ml reload ");
                sender.sendMessage(ChatColor.GRAY + "- /ml check");
            }
        }
        return true;
    }
}