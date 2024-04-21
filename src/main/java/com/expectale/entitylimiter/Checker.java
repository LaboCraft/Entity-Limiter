package com.expectale.entitylimiter;

import com.expectale.entitylimiter.configuration.EntityLimiterConfiguration;
import com.expectale.entitylimiter.utilis.DiscordWebhook;
import org.bukkit.*;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.awt.Color;
import java.io.IOException;
import java.util.HashSet;


public class Checker {

    private static BukkitRunnable task = null;
    private static HashSet<Chunk> chunksCheched = new HashSet<>();

    public static void start() {
        final EntityLimiterConfiguration configuration = EntityLimiter.getINSTANCE().getConfiguration();

        task = new BukkitRunnable() {
            long lastChunkCheck = System.currentTimeMillis();
            @Override
            public void run() {
                if (configuration.isTPSMeter() && Bukkit.getTPS()[0] < configuration.getTPSMeterTrigger()) {
                    removeEntities(EntityType.BOAT);
                    removeEntities(EntityType.MINECART);
                }
                if (configuration.isChunkTask() && System.currentTimeMillis() > lastChunkCheck + (long) configuration.getChunkTaskRefresh() * 60 * 1000) {
                    removeEntities(EntityType.BOAT);
                    removeEntities(EntityType.MINECART);
                    lastChunkCheck = System.currentTimeMillis();
                }
                chunksCheched.clear();
            }
        };
        task.runTaskTimer(EntityLimiter.getINSTANCE(), 20, 20);
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

    public static int countEntityInChunk(Chunk chunk, EntityType type) {
        int count = 0;

        for (Entity entity : chunk.getEntities()) {
            if (entity.getType().equals(type) && (!entity.getType().equals(EntityType.ARMOR_STAND) || ((ArmorStand) entity).isVisible())) {
                count++;
            }
        }
        return count;
    }

    public static void removeEntitiesInChunk(Chunk chunk, EntityType type) {
        final EntityLimiterConfiguration configuration = EntityLimiter.getINSTANCE().getConfiguration();

        if (configuration.isDiscord() && !configuration.getDiscordWebhook().isEmpty()) {
            sendDiscordAlert(chunk, type);
        }
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.hasPermission("entitieslimiter.use")) {
                sendInGameAlert(player, chunk, type);
            }
        }
        for (Entity entity : chunk.getEntities()) {
            if (entity.getType().equals(type) && (!entity.getType().equals(EntityType.ARMOR_STAND) || ((ArmorStand) entity).isVisible())) {
                entity.remove();
            }
        }
    }

    public static void removeEntities(EntityType type) {
        final EntityLimiterConfiguration configuration = EntityLimiter.getINSTANCE().getConfiguration();
        for (World world : Bukkit.getWorlds()) {
            if (!configuration.getDisableIfNameContains().contains(world.getName()) && !configuration.getDisabledWorlds().contains(world.getName())) {
                for (Chunk chunk : world.getLoadedChunks()) {
                    if (countEntityInChunk(chunk, type) >= configuration.getChunkLimit()) {
                        removeEntitiesInChunk(chunk, type);
                    }
                }
            }
        }
    }

    public static void sendInGameCheck(Player player, Chunk chunk, EntityType type) {
        final EntityLimiterConfiguration configuration = EntityLimiter.getINSTANCE().getConfiguration();
        StringBuilder players = new StringBuilder();

        for (Entity entity : chunk.getWorld().getNearbyLivingEntities(new Location(chunk.getWorld(), chunk.getX() * 16, 150, chunk.getZ() * 16), 150)) {
            if (entity instanceof Player) {
                players.append(" ").append(entity.getName());
            }
        }
        player.sendMessage(ChatColor.YELLOW + "Check Entity Limiter :");
        player.sendMessage(ChatColor.GRAY + "- World " + chunk.getWorld().getName());
        player.sendMessage(ChatColor.GRAY + "- X " + chunk.getX() * 16);
        player.sendMessage(ChatColor.GRAY + "- Z " + chunk.getZ() * 16);
        player.sendMessage(ChatColor.GRAY + "- Nearby players (150 blocks) " + ((players.length() == 0) ? "NONE" : players.toString()));
        player.sendMessage(ChatColor.GRAY + "- Counter " + countEntityInChunk(chunk, type) +  "/" + configuration.getChunkLimit());
    }

    public static void sendInGameAlert(Player player, Chunk chunk, EntityType type) {
        final EntityLimiterConfiguration configuration = EntityLimiter.getINSTANCE().getConfiguration();
        StringBuilder players = new StringBuilder();

        for (Entity entity : chunk.getWorld().getNearbyLivingEntities(new Location(chunk.getWorld(), chunk.getX() * 16, 150, chunk.getZ() * 16), 150)) {
            if (entity instanceof Player) {
                players.append(" ").append(entity.getName());
            }
        }
        player.sendMessage(ChatColor.DARK_RED + "A significant number of entites ("+ type.toString() +")  have been removed :");
        player.sendMessage(ChatColor.RED + "- World " + chunk.getWorld().getName());
        player.sendMessage(ChatColor.RED + "- X " + chunk.getX() * 16);
        player.sendMessage(ChatColor.RED + "- Z " + chunk.getZ() * 16);
        player.sendMessage(ChatColor.RED + "- Nearby players (150 blocks) " + ((players.length() == 0) ? "NONE" : players.toString()));
        player.sendMessage(ChatColor.RED + "- Counter " + countEntityInChunk(chunk, type) +  "/" + configuration.getChunkLimit());
    }

    public static void sendDiscordAlert(Chunk chunk, EntityType type) {
        final EntityLimiterConfiguration configuration = EntityLimiter.getINSTANCE().getConfiguration();
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
                        .setTitle("A significant number of entites ("+ type.toString() +")  have been removed")
                        .setDescription("")
                        .setColor(Color.RED)
                        .addField("World", chunk.getWorld().getName(), false)
                        .addField("X", chunk.getX() * 16 + "", false)
                        .addField("Z", chunk.getZ() * 16 + "", false)
                        .addField("Nearby players (150 blocks)", (players.length() == 0) ? "NONE" : players.toString(), false)
                        .addField("Counter", countEntityInChunk(chunk, type) +  "/" + configuration.getChunkLimit(), false)
                        .setAuthor("Entity-Limiter", "https://labocraft.fr", "https://labocraft.fr/storage/img/logo64x64.png")
        );
        try {
            webhook.execute();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
