package net.thomilist.dimensionalinventories.module.builtin.pool;

import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.thomilist.dimensionalinventories.module.version.StorageVersion;
import net.thomilist.dimensionalinventories.DimensionalInventoriesMod;
import net.thomilist.dimensionalinventories.module.base.player.PlayerModule;

import java.util.Optional;

public class DimensionPoolTransitionHandler
{
    public static void loadToPlayer(StorageVersion storageVersion, DimensionPool dimensionPool, ServerPlayerEntity player)
    {
        if (DimensionalInventoriesMod.PLAYER_MODULES.has(storageVersion))
        {
            for (PlayerModule module : DimensionalInventoriesMod.PLAYER_MODULES.get(storageVersion))
            {
                module.loadWithContext(player, dimensionPool);
            }
        }
    }

    public static void saveFromPlayer(StorageVersion storageVersion, DimensionPool dimensionPool, ServerPlayerEntity player)
    {
        if (DimensionalInventoriesMod.PLAYER_MODULES.has(storageVersion))
        {
            for (PlayerModule module : DimensionalInventoriesMod.PLAYER_MODULES.get(storageVersion))
            {
                module.saveWithContext(player, dimensionPool);
            }
        }
    }

    public static void handlePlayerDimensionChange(ServerPlayerEntity player, String originDimensionName, String destinationDimensionName)
    {
        DimensionalInventoriesMod.LOGGER.debug(
            "Player '{}' ({}) travelled from {} to {}.",
            player.getName().getString(),
            player.getUuidAsString(),
            originDimensionName,
            destinationDimensionName
        );

        if (DimensionPoolConfigModule.STATE.dimensionsAreInSamePool(originDimensionName, destinationDimensionName))
        {
            DimensionalInventoriesMod.LOGGER.debug("The origin and destination dimensions are in the same pool. Player unaffected.");
        }
        else
        {
            DimensionalInventoriesMod.LOGGER.debug("The origin and destination dimensions are in different pools. Switching inventories...");

            Optional<DimensionPool> originDimensionPool = DimensionPoolConfigModule.STATE.poolWithDimension(originDimensionName);
            Optional<DimensionPool> destinationDimensionPool = DimensionPoolConfigModule.STATE.poolWithDimension(destinationDimensionName);

            if (originDimensionPool.isEmpty() || destinationDimensionPool.isEmpty())
            {
                DimensionalInventoriesMod.LOGGER.warn(
                    "Not all dimensions are assigned to a dimension pool. Player '{}' unaffected ({} -> {}).",
                    player.getName().getString(),
                    originDimensionName,
                    destinationDimensionName
                );
                return;
            }

            DimensionPoolTransitionHandler.saveFromPlayer(DimensionalInventoriesMod.STORAGE_VERSION, originDimensionPool.get(), player);
            DimensionPoolTransitionHandler.loadToPlayer(DimensionalInventoriesMod.STORAGE_VERSION, destinationDimensionPool.get(), player);
        }
    }

    public static void handleEntityDimensionChange(Entity newEntity, String originDimensionName, String destinationDimensionName)
    {
        if (!DimensionPoolConfigModule.STATE.dimensionsAreInSamePool(originDimensionName, destinationDimensionName))
        {
            DimensionalInventoriesMod.LOGGER.debug(
                "Entity '{}' travelled from {} to {}.",
                newEntity.getName().getString(),
                originDimensionName,
                destinationDimensionName
            );

            DimensionalInventoriesMod.LOGGER.debug("The origin and destination dimensions are in different pools. Deleting entity...");

            Optional<DimensionPool> originDimension = DimensionPoolConfigModule.STATE.poolWithDimension(originDimensionName);
            Optional<DimensionPool> destinationDimension = DimensionPoolConfigModule.STATE.poolWithDimension(destinationDimensionName);

            if (originDimension.isEmpty() || destinationDimension.isEmpty())
            {
                DimensionalInventoriesMod.LOGGER.warn(
                    "Not all dimensions are assigned a dimension pool. Entity '{}' unaffected ({} -> {}).",
                    newEntity.getName().getString(),
                    originDimensionName,
                    destinationDimensionName
                );

                return;
            }

            newEntity.discard();
        }
    }
}
