package com.expectale.minecartlimiter.listener;

import com.expectale.minecartlimiter.Checker;
import com.expectale.minecartlimiter.MineCartLimiter;
import com.expectale.minecartlimiter.configuration.MinecartLimiterConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleCreateEvent;
import org.bukkit.event.vehicle.VehicleEntityCollisionEvent;

public class MineCartListener implements Listener {

    @EventHandler
    public void onVehicleEntityCollision(VehicleEntityCollisionEvent event) {
        if (Checker.isChechedChunk(event.getVehicle().getChunk())) {
            Checker.addChechedChunk(event.getVehicle().getChunk());
            final MinecartLimiterConfiguration configuration = MineCartLimiter.getINSTANCE().getConfiguration();
            if (!configuration.getDisableIfNameContains().contains(event.getVehicle().getWorld().getName()) && !configuration.getDisabledWorlds().contains(event.getVehicle().getWorld().getName())) {
                if (configuration.getChunkLimit() <= Checker.countMinecartInChunk(event.getVehicle().getChunk())) {
                    Checker.removeMinecartInChunk(event.getVehicle().getChunk());
                }
            }
        }
    }

    @EventHandler
    public void onVehicleCreate(VehicleCreateEvent event) {
        if (Checker.isChechedChunk(event.getVehicle().getChunk())) {
            Checker.addChechedChunk(event.getVehicle().getChunk());
            final MinecartLimiterConfiguration configuration = MineCartLimiter.getINSTANCE().getConfiguration();
            if (!configuration.getDisableIfNameContains().contains(event.getVehicle().getWorld().getName()) && !configuration.getDisabledWorlds().contains(event.getVehicle().getWorld().getName())) {
                if (configuration.getChunkLimit() <= Checker.countMinecartInChunk(event.getVehicle().getChunk())) {
                    Checker.removeMinecartInChunk(event.getVehicle().getChunk());
                }
            }
        }
    }
}
