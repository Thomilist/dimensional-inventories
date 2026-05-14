package net.thomilist.dimensionalinventories.module.builtin.pool;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Clearable;
import net.minecraft.world.entity.Entity;
import net.thomilist.dimensionalinventories.DimensionalInventories;
import net.thomilist.dimensionalinventories.module.ModuleRegistry;
import net.thomilist.dimensionalinventories.module.base.config.ConfigModule;
import net.thomilist.dimensionalinventories.module.base.player.PlayerModule;
import net.thomilist.dimensionalinventories.module.version.StorageVersion;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class DimensionPoolTransitionHandler
{
    private final StorageVersion storageVersion;
    private final ModuleRegistry<ConfigModule> configModules;
    private final ModuleRegistry<PlayerModule> playerModules;
    private final Map<Entity, TransitionInfo> lastTransitions = new HashMap<>();

    public DimensionPoolTransitionHandler( final StorageVersion storageVersion,
                                           final ModuleRegistry<ConfigModule> configModules,
                                           final ModuleRegistry<PlayerModule> playerModules )
    {
        this.storageVersion = storageVersion;
        this.configModules = configModules;
        this.playerModules = playerModules;
    }

    public void loadToPlayer( final StorageVersion storageVersion,
                              final DimensionPool dimensionPool,
                              final ServerPlayer player )
    {
        if ( this.playerModules.has( storageVersion ) )
        {
            for ( final PlayerModule module : this.playerModules.get( storageVersion ) )
            {
                module.loadWithContext( player, dimensionPool );
            }
        }
    }

    public void saveFromPlayer( final StorageVersion storageVersion,
                                final DimensionPool dimensionPool,
                                final ServerPlayer player )
    {
        if ( this.playerModules.has( storageVersion ) )
        {
            for ( final PlayerModule module : this.playerModules.get( storageVersion ) )
            {
                module.saveWithContext( player, dimensionPool );
            }
        }
    }

    public void handlePlayerDimensionChange( final ServerPlayer player,
                                             final String originDimensionName,
                                             final String destinationDimensionName )
    {
        if ( this.transitionAlreadyHandled( player, originDimensionName, destinationDimensionName ) )
        {
            return;
        }

        DimensionalInventories.LOGGER.debug(
            "Player '{}' ({}) travelled from {} to {}.",
            player.getName().getString(),
            player.getStringUUID(),
            originDimensionName,
            destinationDimensionName
        );

        final DimensionPoolConfigModule dimensionPoolConfig = this.configModules.get( DimensionPoolConfigModule.class );

        if ( dimensionPoolConfig.state().dimensionsAreInSamePool( originDimensionName, destinationDimensionName ) )
        {
            DimensionalInventories.LOGGER.debug(
                "The origin and destination dimensions are in the same pool. Player unaffected." );
        }
        else
        {
            DimensionalInventories.LOGGER.debug(
                "The origin and destination dimensions are in different pools. Switching inventories..." );

            final Optional<DimensionPool> originDimensionPool = dimensionPoolConfig
                .state()
                .poolWithDimension( originDimensionName );

            final Optional<DimensionPool> destinationDimensionPool = dimensionPoolConfig
                .state()
                .poolWithDimension( destinationDimensionName );

            if ( originDimensionPool.isEmpty() || destinationDimensionPool.isEmpty() )
            {
                DimensionalInventories.LOGGER.warn(
                    "Not all dimensions are assigned to a dimension pool. Player '{}' unaffected ({} -> {}).",
                    player.getName().getString(),
                    originDimensionName,
                    destinationDimensionName
                );

                return;
            }

            this.saveFromPlayer( this.storageVersion, originDimensionPool.get(), player );
            this.loadToPlayer( this.storageVersion, destinationDimensionPool.get(), player );
        }
    }

    public void handleEntityDimensionChange( final Entity newEntity,
                                             final String originDimensionName,
                                             final String destinationDimensionName )
    {
        if ( this.transitionAlreadyHandled( newEntity, originDimensionName, destinationDimensionName ) )
        {
            return;
        }

        final DimensionPoolConfigModule dimensionPoolConfig = this.configModules.get( DimensionPoolConfigModule.class );

        if ( !dimensionPoolConfig.state().dimensionsAreInSamePool( originDimensionName, destinationDimensionName ) )
        {
            DimensionalInventories.LOGGER.debug(
                "Entity '{}' travelled from {} to {}.",
                newEntity.getName().getString(),
                originDimensionName,
                destinationDimensionName
            );

            DimensionalInventories.LOGGER.debug(
                "The origin and destination dimensions are in different pools. Deleting entity..." );

            final Optional<DimensionPool> originDimension = dimensionPoolConfig
                .state()
                .poolWithDimension( originDimensionName );

            final Optional<DimensionPool> destinationDimension = dimensionPoolConfig
                .state()
                .poolWithDimension( destinationDimensionName );

            if ( originDimension.isEmpty() || destinationDimension.isEmpty() )
            {
                DimensionalInventories.LOGGER.warn(
                    "Not all dimensions are assigned a dimension pool. Entity '{}' unaffected ({} -> {}).",
                    newEntity.getName().getString(),
                    originDimensionName,
                    destinationDimensionName
                );

                return;
            }

            if ( newEntity instanceof Clearable )
            {
                ((Clearable) newEntity).clearContent();
            }

            newEntity.discard();
        }
    }

    private Boolean transitionAlreadyHandled( final Entity newEntity,
                                              final String originDimensionName,
                                              final String destinationDimensionName )
    {
        final TransitionInfo oldTransitionInfo = this.lastTransitions.get( newEntity );
        final TransitionInfo newTransitionInfo = new TransitionInfo( originDimensionName, destinationDimensionName );

        if ( (oldTransitionInfo != null) && oldTransitionInfo.equals( newTransitionInfo ) )
        {
            DimensionalInventories.LOGGER.debug(
                "Transition already handled. Entity '{}' unaffected ({} -> {}).",
                newEntity.getName().getString(),
                originDimensionName,
                destinationDimensionName
            );
            return true;
        }

        this.lastTransitions.put( newEntity, newTransitionInfo );
        return false;
    }

    record TransitionInfo( String originDimensionName, String destinationDimensionName )
    { }
}
