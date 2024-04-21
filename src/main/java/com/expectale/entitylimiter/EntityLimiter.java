package com.expectale.entitylimiter;

import com.expectale.entitylimiter.command.EntityLimiterCommand;
import com.expectale.entitylimiter.configuration.ConfigurationLoader;
import com.expectale.entitylimiter.configuration.EntityLimiterConfiguration;
import com.expectale.entitylimiter.listener.EntityListener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class EntityLimiter extends JavaPlugin {

    private static EntityLimiter INSTANCE;

    public static EntityLimiter getINSTANCE() {
        return INSTANCE;
    }

    private EntityLimiterConfiguration configuration;

    public EntityLimiterConfiguration getConfiguration() {
        return configuration;
    }

    public void reloadConfig() {
        Checker.stop();
        ConfigurationLoader configurationLoader = new ConfigurationLoader(this, "config.yml", new File(getDataFolder(), "config.yml"));
        configuration = new EntityLimiterConfiguration(configurationLoader.getConfiguration());
        Checker.start();
    }

    @Override
    public void onEnable() {
        INSTANCE = this;
        reloadConfig();
        getCommand("ml").setExecutor(new EntityLimiterCommand());
        getServer().getPluginManager().registerEvents(new EntityListener(), this);
        getLogger().info("EntityLimiter is enabled");
    }

    @Override
    public void onDisable() {
        getLogger().info("EntityLimiter is disabled");
        Checker.stop();
    }
}
