package com.expectale.minecartlimiter.configuration;

import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;

public class MinecartLimiterConfiguration {
    private Configuration configuration;
    private int chunkLimit = 0;
    private boolean chunkTask = false;
    private int chunkTaskRefresh = 10;

    private List<String> disabledWorlds = new ArrayList<>();
    private List<String> disableIfNameContains = new ArrayList<>();

    private boolean TPSMeter = false;
    private int TPSMeterTrigger = 17;

    private boolean discord = false;
    private String discordWebhook = "";
    public MinecartLimiterConfiguration(final Configuration configuration) {
        reload(configuration);
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public void reload(final Configuration configuration) {
        this.configuration = configuration;
        if (configuration.isConfigurationSection("Chunk")) {
            ConfigurationSection chunkConfiguration = configuration.getConfigurationSection("Chunk");
            if (chunkConfiguration != null && chunkConfiguration.isInt("Trigger")) {
                chunkLimit = chunkConfiguration.getInt("Trigger");
            }
            if (chunkConfiguration != null && chunkConfiguration.isConfigurationSection("Task")) {
                ConfigurationSection chunkTaskConfiguration = chunkConfiguration.getConfigurationSection("Task");
                if (chunkTaskConfiguration != null && chunkTaskConfiguration.isBoolean("Enabled")) {
                    chunkTask = chunkTaskConfiguration.getBoolean("Enabled");
                }
                if (chunkTaskConfiguration != null && chunkTaskConfiguration.isInt("Refresh")) {
                    chunkTaskRefresh = chunkTaskConfiguration.getInt("Refresh");
                }
            }
        }
        if (configuration.isConfigurationSection("Checks")) {
            ConfigurationSection checksConfiguration = configuration.getConfigurationSection("Checks");
            if (checksConfiguration != null && checksConfiguration.isList("DisabledWorlds")) {
                disabledWorlds = checksConfiguration.getStringList("DisabledWorlds");
            }
            if (checksConfiguration != null && checksConfiguration.isList("DisableIfNameContains")) {
                disableIfNameContains = checksConfiguration.getStringList("DisableIfNameContains");
            }
        }

        if (configuration.isConfigurationSection("TPSMeter")) {
            ConfigurationSection TPSMeterConfiguration = configuration.getConfigurationSection("TPSMeter");
            if (TPSMeterConfiguration != null && TPSMeterConfiguration.isBoolean("Enabled")) {
                TPSMeter = TPSMeterConfiguration.getBoolean("Enabled");
            }
            if (TPSMeterConfiguration != null && TPSMeterConfiguration.isInt("Trigger")) {
                TPSMeterTrigger = TPSMeterConfiguration.getInt("Trigger");
            }
        }

        if (configuration.isConfigurationSection("Discord")) {
            ConfigurationSection discordConfiguration = configuration.getConfigurationSection("Discord");
            if (discordConfiguration != null && discordConfiguration.isBoolean("Enabled")) {
                discord = discordConfiguration.getBoolean("Enabled");
            }
            if (discordConfiguration != null && discordConfiguration.isString("Webhook")) {
                discordWebhook = discordConfiguration.getString("Webhook");
            }
        }
    }

    public int getChunkLimit() {
        return chunkLimit;
    }

    public boolean isChunkTask() {
        return chunkTask;
    }

    public int getChunkTaskRefresh() {
        return chunkTaskRefresh;
    }

    public List<String> getDisabledWorlds() {
        return disabledWorlds;
    }

    public List<String> getDisableIfNameContains() {
        return disableIfNameContains;
    }

    public boolean isTPSMeter() {
        return TPSMeter;
    }

    public int getTPSMeterTrigger() {
        return TPSMeterTrigger;
    }

    public boolean isDiscord() {
        return discord;
    }

    public String getDiscordWebhook() {
        return discordWebhook;
    }
}
