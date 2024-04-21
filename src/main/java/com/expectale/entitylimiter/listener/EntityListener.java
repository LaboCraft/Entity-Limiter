package com.expectale.entitylimiter.listener;

import com.expectale.entitylimiter.Checker;
import com.expectale.entitylimiter.EntityLimiter;
import com.expectale.entitylimiter.configuration.EntityLimiterConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.vehicle.VehicleCreateEvent;
import org.bukkit.event.vehicle.VehicleEntityCollisionEvent;

public class EntityListener implements Listener {

    @EventHandler
    public void onVehicleEntityCollision(VehicleEntityCollisionEvent event) {
        final EntityLimiterConfiguration configuration = EntityLimiter.getINSTANCE().getConfiguration();
        if (configuration.getEntityType().contains(event.getVehicle().getType()) && Checker.isChechedChunk(event.getVehicle().getChunk())) {
            Checker.addChechedChunk(event.getVehicle().getChunk());
            if (!configuration.getDisableIfNameContains().contains(event.getVehicle().getWorld().getName()) && !configuration.getDisabledWorlds().contains(event.getVehicle().getWorld().getName())) {
                if (configuration.getChunkLimit() <= Checker.countEntityInChunk(event.getVehicle().getChunk(), event.getVehicle().getType())) {
                    Checker.removeEntitiesInChunk(event.getVehicle().getChunk(), event.getVehicle().getType());
                }
            }
        }
    }

    @EventHandler
    public void onVehicleCreate(VehicleCreateEvent event) {
        final EntityLimiterConfiguration configuration = EntityLimiter.getINSTANCE().getConfiguration();
        if (configuration.getEntityType().contains(event.getVehicle().getType()) && Checker.isChechedChunk(event.getVehicle().getChunk())) {
            Checker.addChechedChunk(event.getVehicle().getChunk());
            if (!configuration.getDisableIfNameContains().contains(event.getVehicle().getWorld().getName()) && !configuration.getDisabledWorlds().contains(event.getVehicle().getWorld().getName())) {
                if (configuration.getChunkLimit() <= Checker.countEntityInChunk(event.getVehicle().getChunk(), event.getVehicle().getType())) {
                    Checker.removeEntitiesInChunk(event.getVehicle().getChunk(), event.getVehicle().getType());
                }
            }
        }
    }

    @EventHandler
    public void onEntitySpawnEvent(EntitySpawnEvent event) {
        final EntityLimiterConfiguration configuration = EntityLimiter.getINSTANCE().getConfiguration();
        if (configuration.getEntityType().contains(event.getEntity().getType())) {
            event.getEntity().setGravity(false);
            if (Checker.isChechedChunk(event.getEntity().getChunk())) {
                Checker.addChechedChunk(event.getEntity().getChunk());
                if (!configuration.getDisableIfNameContains().contains(event.getEntity().getWorld().getName()) && !configuration.getDisabledWorlds().contains(event.getEntity().getWorld().getName())) {
                    if (configuration.getChunkLimit() <= Checker.countEntityInChunk(event.getEntity().getChunk(), event.getEntity().getType())) {
                        Checker.removeEntitiesInChunk(event.getEntity().getChunk(), event.getEntity().getType());
                    }
                }
            }
        }
    }
}
