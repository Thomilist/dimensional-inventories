package net.thomilist.dimensionalinventories.module.builtin.pool;

import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Clearable;
import net.thomilist.dimensionalinventories.DimensionalInventories;
import net.thomilist.dimensionalinventories.module.ModuleRegistry;
import net.thomilist.dimensionalinventories.module.base.config.ConfigModule;
import net.thomilist.dimensionalinventories.module.version.StorageVersion;
import net.thomilist.dimensionalinventories.module.base.player.PlayerModule;

import java.util.Optional;

public class DimensionPoolTransitionHandler
{
    private final StorageVersion storageVersion;
    private final ModuleRegistry<ConfigModule> configModules;
    private final ModuleRegistry<PlayerModule> playerModules;

    public DimensionPoolTransitionHandler(StorageVersion storageVersion, ModuleRegistry<ConfigModule> configModules, ModuleRegistry<PlayerModule> playerModules)
    {
        this.storageVersion = storageVersion;
        this.configModules = configModules;
        this.playerModules = playerModules;
    }

    public void loadToPlayer(StorageVersion storageVersion, DimensionPool dimensionPool, ServerPlayerEntity player)
    {
        if (playerModules.has(storageVersion))
        {
            for (PlayerModule module : playerModules.get(storageVersion))
            {
                module.loadWithContext(player, dimensionPool);
            }
        }
    }

    public void saveFromPlayer(StorageVersion storageVersion, DimensionPool dimensionPool, ServerPlayerEntity player)
    {
        if (playerModules.has(storageVersion))
        {
            for (PlayerModule module : playerModules.get(storageVersion))
            {
                module.saveWithContext(player, dimensionPool);
            }
        }
    }

    public void handlePlayerDimensionChange(ServerPlayerEntity player, String originDimensionName, String destinationDimensionName)
    {
        DimensionalInventories.LOGGER.debug(
            "Player '{}' ({}) travelled from {} to {}.",
            player.getName().getString(),
            player.getUuidAsString(),
            originDimensionName,
            destinationDimensionName
        );

        DimensionPoolConfigModule dimensionPoolConfig = configModules.get(DimensionPoolConfigModule.class);

        if (dimensionPoolConfig.state().dimensionsAreInSamePool(originDimensionName, destinationDimensionName))
        {
            DimensionalInventories.LOGGER.debug("The origin and destination dimensions are in the same pool. Player unaffected.");
        }
        else
        {
            DimensionalInventories.LOGGER.debug("The origin and destination dimensions are in different pools. Switching inventories...");

            Optional<DimensionPool> originDimensionPool = dimensionPoolConfig.state().poolWithDimension(originDimensionName);
            Optional<DimensionPool> destinationDimensionPool = dimensionPoolConfig.state().poolWithDimension(destinationDimensionName);

            if (originDimensionPool.isEmpty() || destinationDimensionPool.isEmpty())
            {
                DimensionalInventories.LOGGER.warn(
                    "Not all dimensions are assigned to a dimension pool. Player '{}' unaffected ({} -> {}).",
                    player.getName().getString(),
                    originDimensionName,
                    destinationDimensionName
                );
                return;
            }

            saveFromPlayer(storageVersion, originDimensionPool.get(), player);
            loadToPlayer(storageVersion, destinationDimensionPool.get(), player);
        }
    }

    public void handleEntityDimensionChange(Entity newEntity, String originDimensionName, String destinationDimensionName)
    {
        DimensionPoolConfigModule dimensionPoolConfig = configModules.get(DimensionPoolConfigModule.class);

        if (!dimensionPoolConfig.state().dimensionsAreInSamePool(originDimensionName, destinationDimensionName))
        {
            DimensionalInventories.LOGGER.debug(
                "Entity '{}' travelled from {} to {}.",
                newEntity.getName().getString(),
                originDimensionName,
                destinationDimensionName
            );

            DimensionalInventories.LOGGER.debug("The origin and destination dimensions are in different pools. Deleting entity...");

            Optional<DimensionPool> originDimension = dimensionPoolConfig.state().poolWithDimension(originDimensionName);
            Optional<DimensionPool> destinationDimension = dimensionPoolConfig.state().poolWithDimension(destinationDimensionName);

            if (originDimension.isEmpty() || destinationDimension.isEmpty())
            {
                DimensionalInventories.LOGGER.warn(
                    "Not all dimensions are assigned a dimension pool. Entity '{}' unaffected ({} -> {}).",
                    newEntity.getName().getString(),
                    originDimensionName,
                    destinationDimensionName
                );

                return;
            }

            if (newEntity instanceof Clearable)
            {
                ((Clearable) newEntity).clear();
            }

            newEntity.discard();
        }
    }
}
