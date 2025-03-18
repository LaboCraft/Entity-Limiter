package com.expectale.entitylimiter;

import com.expectale.entitylimiter.configuration.EntityLimiterConfiguration;
import com.expectale.entitylimiter.utils.DiscordWebhook;
import org.bukkit.*;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;


public class Checker {

    private static BukkitRunnable task = null;
    private static final HashSet<Chunk> chunksChecked = new HashSet<>();

    public static void start() {
        final EntityLimiterConfiguration configuration = EntityLimiter.getInstance().getConfiguration();

        task = new BukkitRunnable() {
            long lastChunkCheck = System.currentTimeMillis();
            @Override
            public void run() {
                if (configuration.isTPSMeter() && Bukkit.getTPS()[0] < configuration.getTPSMeterTrigger()) {
                    for (EntityType type : EntityLimiter.getInstance().getConfiguration().getEntityType()) {
                        removeEntities(type);
                    }
                }
                if (configuration.isChunkTask() && System.currentTimeMillis() > lastChunkCheck + (long) configuration.getChunkTaskRefresh() * 60 * 1000) {
                    for (EntityType type : EntityLimiter.getInstance().getConfiguration().getEntityType()) {
                        removeEntities(type);
                    }
                    lastChunkCheck = System.currentTimeMillis();
                }
                chunksChecked.clear();
            }
        };
        task.runTaskTimer(EntityLimiter.getInstance(), 20, 20);
    }

    public static void stop() {
        if (task != null) {
            task.cancel();
            task = null;
        }
    }

    public static void addCheckedChunk(Chunk chunk) {
        chunksChecked.add(chunk);
    }

    public static boolean isCheckedChunk(Chunk chunk) {
        return !chunksChecked.contains(chunk);
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
        final EntityLimiterConfiguration configuration = EntityLimiter.getInstance().getConfiguration();

        if (configuration.isDiscord() && !configuration.getDiscordWebhook().isEmpty()) {
            sendDiscordAlert(chunk, type);
        }
        
        if (configuration.isVerbose()) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.hasPermission("entitieslimiter.use")) {
                    sendInGameAlert(player, chunk, type);
                }
            }
        }

        for (Entity entity : chunk.getEntities()) {
            if (entity.getType().equals(type) && (!entity.getType().equals(EntityType.ARMOR_STAND) || ((ArmorStand) entity).isVisible())) {
                entity.remove();
            }
        }
    }

    public static void removeEntities(EntityType type) {
        final EntityLimiterConfiguration configuration = EntityLimiter.getInstance().getConfiguration();
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

    public static List<Chunk> getChunksAround(Chunk chunk, int radius) {
        int chunkX = chunk.getX();
        int chunkZ = chunk.getZ();
        int minX = chunkX - radius;
        int minZ = chunkZ - radius;
        int maxX = chunkX + radius;
        int maxZ = chunkZ + radius;

        List<Chunk> chunks = new ArrayList<>();
        for (int x = minX; x <= maxX; x++) {
            for (int z = minZ; z <= maxZ; z++) {
                chunks.add(chunk.getWorld().getChunkAt(x, z));
            }
        }

        return chunks;
    }

    public static void sendInGameCheck(Player player, List<Chunk> chunks, EntityType type) {
        StringBuilder players = new StringBuilder();

        Chunk centerChunk = chunks.get(chunks.size() / 2);
        for (Entity entity : centerChunk.getWorld().getNearbyLivingEntities(new Location(centerChunk.getWorld(), centerChunk.getX() * 16, 150, centerChunk.getZ() * 16), 150)) {
            if (entity instanceof Player) {
                players.append(" ").append(entity.getName());
            }
        }
        int counter = 0;
        for (Chunk chunk : chunks) {
            counter += countEntityInChunk(chunk, type);
        }
        player.sendMessage(ChatColor.YELLOW + "Check Entity Limiter (" + type.toString() + ") :");
        player.sendMessage(ChatColor.GRAY + "- World " + centerChunk.getWorld().getName());
        player.sendMessage(ChatColor.GRAY + "- X " + centerChunk.getX() * 16);
        player.sendMessage(ChatColor.GRAY + "- Z " + centerChunk.getZ() * 16);
        player.sendMessage(ChatColor.GRAY + "- Nearby players (150 blocks) " + ((players.length() == 0) ? (ChatColor.RED + "NONE") : players.toString()));
        player.sendMessage(ChatColor.GRAY + "- Counter " + counter);
    }

    public static void sendInGameAlert(Player player, Chunk chunk, EntityType type) {
        final EntityLimiterConfiguration configuration = EntityLimiter.getInstance().getConfiguration();
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
        final EntityLimiterConfiguration configuration = EntityLimiter.getInstance().getConfiguration();
        StringBuilder players = new StringBuilder();

        for (Entity entity : chunk.getWorld().getNearbyLivingEntities(new Location(chunk.getWorld(), chunk.getX() * 16, 150, chunk.getZ() * 16), 150)) {
            if (entity instanceof Player) {
                players.append(" ").append(entity.getName());
            }
        }

        DiscordWebhook webhook = new DiscordWebhook(configuration.getDiscordWebhook());
        webhook.setAvatarUrl("https://labocraft.fr/storage/img/logo64x64.png");
        webhook.setUsername("Entity-Limiter");
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
                        .setAuthor("Entity-Limiter Alert", "https://labocraft.fr", "https://upload.wikimedia.org/wikipedia/commons/thumb/3/3b/OOjs_UI_icon_alert-warning.svg/1024px-OOjs_UI_icon_alert-warning.svg.png")
        );
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    webhook.execute();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }.runTaskAsynchronously(EntityLimiter.getInstance());
    }

}
