package com.expectale.minecartlimiter.command;

import com.expectale.minecartlimiter.Checker;
import com.expectale.minecartlimiter.MineCartLimiter;
import com.expectale.minecartlimiter.utilis.DiscordWebhook;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.awt.*;
import java.io.IOException;

public class MinecartLimiterCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args) {

        if (sender instanceof Player) {
            if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
                MineCartLimiter.getINSTANCE().reloadConfig();
                sender.sendMessage(ChatColor.YELLOW + "The configuration file has been successfully reloaded");
            } else if (args.length == 1 && args[0].equalsIgnoreCase("check")) {
                sender.sendMessage(ChatColor.YELLOW + "SOON");
                Checker.sendInGameCheck((Player)sender, ((Player)sender).getChunk());
            } else {
                sender.sendMessage(ChatColor.YELLOW + "Usage Minecart Limiter : ");
                sender.sendMessage(ChatColor.GRAY + "- /ml reload ");
                sender.sendMessage(ChatColor.GRAY + "- /ml check");
            }
        }
        return true;
    }
}