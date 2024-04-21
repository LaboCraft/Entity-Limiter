package com.expectale.entitylimiter.listener;

import com.expectale.entitylimiter.Checker;
import com.expectale.entitylimiter.EntityLimiter;
import com.expectale.entitylimiter.configuration.EntityLimiterConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleCreateEvent;
import org.bukkit.event.vehicle.VehicleEntityCollisionEvent;

public class EntityListener implements Listener {

    @EventHandler
    public void onVehicleEntityCollision(VehicleEntityCollisionEvent event) {
        if (Checker.isChechedChunk(event.getVehicle().getChunk())) {
            Checker.addChechedChunk(event.getVehicle().getChunk());
            final EntityLimiterConfiguration configuration = EntityLimiter.getINSTANCE().getConfiguration();
            if (!configuration.getDisableIfNameContains().contains(event.getVehicle().getWorld().getName()) && !configuration.getDisabledWorlds().contains(event.getVehicle().getWorld().getName())) {
                if (configuration.getChunkLimit() <= Checker.countEntityInChunk(event.getVehicle().getChunk(), event.getVehicle().getType())) {
                    Checker.removeEntitiesInChunk(event.getVehicle().getChunk(), event.getVehicle().getType());
                }
            }
        }
    }

    @EventHandler
    public void onVehicleCreate(VehicleCreateEvent event) {
        if (Checker.isChechedChunk(event.getVehicle().getChunk())) {
            Checker.addChechedChunk(event.getVehicle().getChunk());
            final EntityLimiterConfiguration configuration = EntityLimiter.getINSTANCE().getConfiguration();
            if (!configuration.getDisableIfNameContains().contains(event.getVehicle().getWorld().getName()) && !configuration.getDisabledWorlds().contains(event.getVehicle().getWorld().getName())) {
                if (configuration.getChunkLimit() <= Checker.countEntityInChunk(event.getVehicle().getChunk(), event.getVehicle().getType())) {
                    Checker.removeEntitiesInChunk(event.getVehicle().getChunk(), event.getVehicle().getType());
                }
            }
        }
    }
}
