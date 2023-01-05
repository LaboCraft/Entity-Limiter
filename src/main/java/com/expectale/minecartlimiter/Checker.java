package com.expectale.minecartlimiter;

import com.expectale.minecartlimiter.configuration.MinecartLimiterConfiguration;
import com.expectale.minecartlimiter.utilis.DiscordWebhook;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.awt.*;
import java.awt.Color;
import java.io.IOException;
import java.util.HashSet;


public class Checker {

    private static BukkitRunnable task = null;
    private static HashSet<Chunk> chunksCheched = new HashSet<>();

    public static void start() {
        final MinecartLimiterConfiguration configuration = MineCartLimiter.getINSTANCE().getConfiguration();

        task = new BukkitRunnable() {
            long lastChunkCheck = System.currentTimeMillis();
            @Override
            public void run() {
                if (configuration.isTPSMeter() && Bukkit.getTPS()[0] < configuration.getTPSMeterTrigger()) {
                    removeMinecarts();
                }
                if (configuration.isChunkTask() && System.currentTimeMillis() > lastChunkCheck + (long) configuration.getChunkTaskRefresh() * 60 * 1000) {
                    removeMinecarts();
                    lastChunkCheck = System.currentTimeMillis();
                }
                chunksCheched.clear();
            }
        };
        task.runTaskTimer(MineCartLimiter.getINSTANCE(), 20, 20);
    }

    public static void stop() {
        if (task != null) {
            task.cancel();
            task = null;
        }
    }

    public static void addChechedChunk(Chunk chunk) {
        chunksCheched.add(chunk);
    }

    public static boolean isChechedChunk(Chunk chunk) {
        return !chunksCheched.contains(chunk);
    }

    public static int countMinecartInChunk(Chunk chunk) {
        int count = 0;

        for (Entity entity : chunk.getEntities()) {
            if (entity instanceof Minecart) {
                count++;
            }
        }
        return count;
    }

    public static void removeMinecartInChunk(Chunk chunk) {
        final MinecartLimiterConfiguration configuration = MineCartLimiter.getINSTANCE().getConfiguration();

        if (configuration.isDiscord() && !configuration.getDiscordWebhook().isEmpty()) {
            sendDiscordAlert(chunk);
        }
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.hasPermission("minecartlimiter.use")) {
                sendInGameAlert(player, chunk);
            }
        }
        for (Entity entity : chunk.getEntities()) {
            if (entity instanceof Minecart) {
                entity.remove();
            }
        }
    }

    public static void removeMinecarts() {
        final MinecartLimiterConfiguration configuration = MineCartLimiter.getINSTANCE().getConfiguration();
        for (World world : Bukkit.getWorlds()) {
            if (!configuration.getDisableIfNameContains().contains(world.getName()) && !configuration.getDisabledWorlds().contains(world.getName())) {
                for (Chunk chunk : world.getLoadedChunks()) {
                    if (countMinecartInChunk(chunk) >= configuration.getChunkLimit()) {
                        removeMinecartInChunk(chunk);
                    }
                }
            }
        }
    }

    public static void sendInGameCheck(Player player, Chunk chunk) {
        final MinecartLimiterConfiguration configuration = MineCartLimiter.getINSTANCE().getConfiguration();
        StringBuilder players = new StringBuilder();

        for (Entity entity : chunk.getWorld().getNearbyLivingEntities(new Location(chunk.getWorld(), chunk.getX() * 16, 150, chunk.getZ() * 16), 150)) {
            if (entity instanceof Player) {
                players.append(" ").append(entity.getName());
            }
        }
        player.sendMessage(ChatColor.YELLOW + "Check Minecart Limiter :");
        player.sendMessage(ChatColor.GRAY + "- World " + chunk.getWorld().getName());
        player.sendMessage(ChatColor.GRAY + "- X " + chunk.getX() * 16);
        player.sendMessage(ChatColor.GRAY + "- Z " + chunk.getZ() * 16);
        player.sendMessage(ChatColor.GRAY + "- Nearby players (150 blocks) " + ((players.length() == 0) ? "NONE" : players.toString()));
        player.sendMessage(ChatColor.GRAY + "- Counter " + countMinecartInChunk(chunk) +  "/" + configuration.getChunkLimit());
    }

    public static void sendInGameAlert(Player player, Chunk chunk) {
        final MinecartLimiterConfiguration configuration = MineCartLimiter.getINSTANCE().getConfiguration();
        StringBuilder players = new StringBuilder();

        for (Entity entity : chunk.getWorld().getNearbyLivingEntities(new Location(chunk.getWorld(), chunk.getX() * 16, 150, chunk.getZ() * 16), 150)) {
            if (entity instanceof Player) {
                players.append(" ").append(entity.getName());
            }
        }
        player.sendMessage(ChatColor.DARK_RED + "Minecart Limiter, a significant number of minecrats have been removed :");
        player.sendMessage(ChatColor.RED + "- World " + chunk.getWorld().getName());
        player.sendMessage(ChatColor.RED + "- X " + chunk.getX() * 16);
        player.sendMessage(ChatColor.RED + "- Z " + chunk.getZ() * 16);
        player.sendMessage(ChatColor.RED + "- Nearby players (150 blocks) " + ((players.length() == 0) ? "NONE" : players.toString()));
        player.sendMessage(ChatColor.RED + "- Counter " + countMinecartInChunk(chunk) +  "/" + configuration.getChunkLimit());
    }

    public static void sendDiscordAlert(Chunk chunk) {
        final MinecartLimiterConfiguration configuration = MineCartLimiter.getINSTANCE().getConfiguration();
        StringBuilder players = new StringBuilder();

        for (Entity entity : chunk.getWorld().getNearbyLivingEntities(new Location(chunk.getWorld(), chunk.getX() * 16, 150, chunk.getZ() * 16), 150)) {
            if (entity instanceof Player) {
                players.append(" ").append(entity.getName());
            }
        }

        DiscordWebhook webhook = new DiscordWebhook(configuration.getDiscordWebhook());
        webhook.setAvatarUrl("https://upload.wikimedia.org/wikipedia/commons/thumb/3/3b/OOjs_UI_icon_alert-warning.svg/1024px-OOjs_UI_icon_alert-warning.svg.png");
        webhook.setUsername("Alert !");
        webhook.setTts(false);
        webhook.addEmbed(new DiscordWebhook.EmbedObject()
                        .setTitle("A significant number of minecrats have been removed")
                        .setDescription("")
                        .setColor(Color.RED)
                        .addField("World", chunk.getWorld().getName(), false)
                        .addField("X", chunk.getX() * 16 + "", false)
                        .addField("Z", chunk.getZ() * 16 + "", false)
                        .addField("Nearby players (150 blocks)", (players.length() == 0) ? "NONE" : players.toString(), false)
                        .addField("Counter", countMinecartInChunk(chunk) +  "/" + configuration.getChunkLimit(), false)
                        .setThumbnail("https://static.wikia.nocookie.net/minecraft_gamepedia/images/9/98/Minecart_JE3_BE2.png")
                        .setAuthor("Minecart-Limiter", "https://labocraft.fr", "https://labocraft.fr/storage/img/logo64x64.png")
        );
        try {
            webhook.execute();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
