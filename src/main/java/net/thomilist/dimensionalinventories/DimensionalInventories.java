package net.thomilist.dimensionalinventories;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityLevelChangeEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.thomilist.dimensionalinventories.compatibility.Compat;
import net.thomilist.dimensionalinventories.lostandfound.LostAndFound;
import net.thomilist.dimensionalinventories.lostandfound.LostAndFoundContext;
import net.thomilist.dimensionalinventories.module.ModuleGroup;
import net.thomilist.dimensionalinventories.module.ModuleRegistry;
import net.thomilist.dimensionalinventories.module.base.config.ConfigModule;
import net.thomilist.dimensionalinventories.module.base.player.PlayerModule;
import net.thomilist.dimensionalinventories.module.builtin.pool.DimensionPoolTransitionHandler;
import net.thomilist.dimensionalinventories.module.version.StorageVersion;
import net.thomilist.dimensionalinventories.module.version.StorageVersionMigration;
import net.thomilist.dimensionalinventories.util.ModProperties;
import net.thomilist.dimensionalinventories.util.SavePaths;
import net.thomilist.dimensionalinventories.util.StringHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DimensionalInventories
    implements ModInitializer
{
    public static final DimensionalInventories INSTANCE = new DimensionalInventories();
    public static final ModProperties PROPERTIES = new ModProperties( "dimensional-inventories" );
    public static final Logger LOGGER = LoggerFactory.getLogger( DimensionalInventories.PROPERTIES.namePascal() );
    public final StorageVersion storageVersion;
    public final StorageVersionMigration storageVersionMigration;
    public final ModuleRegistry<ConfigModule> configModules = new ModuleRegistry<>( ConfigModule.class );
    public final ModuleRegistry<PlayerModule> playerModules = new ModuleRegistry<>( PlayerModule.class );
    public final DimensionPoolTransitionHandler transitionHandler;

    public DimensionalInventories( final StorageVersion storageVersion )
    {
        this.storageVersion = storageVersion;

        this.transitionHandler = new DimensionPoolTransitionHandler(
            this.storageVersion,
            this.configModules,
            this.playerModules
        );

        this.storageVersionMigration = new StorageVersionMigration(
            this.storageVersion,
            this.configModules,
            this.transitionHandler
        );
    }

    public DimensionalInventories()
    {
        this( StorageVersion.V2 );
    }

    @Override
    public void onInitialize()
    {
        try ( final LostAndFoundContext LAF = LostAndFound.init(
            "init",
            "base",
            DimensionalInventories.PROPERTIES.id()
        ) )
        {
            DimensionalInventories.INSTANCE.registerStartupHandlers();
            DimensionalInventories.INSTANCE.registerPlayerTravelHandler();
            DimensionalInventories.INSTANCE.registerPlayerRespawnHandler();
            DimensionalInventories.INSTANCE.registerEntityTravelHandler();
        }
    }

    public void registerModules( final ModuleGroup moduleGroup )
    {
        DimensionalInventories.LOGGER.info(
            "Registering modules from module group {} ...",
            StringHelper.joinAndWrapScopes( moduleGroup.groupId() )
        );

        this.configModules.register( moduleGroup );
        this.playerModules.register( moduleGroup );
    }

    private void registerStartupHandlers()
    {
        ServerLifecycleEvents.SERVER_STARTED.register( server -> {
            try ( final LostAndFoundContext LAF = LostAndFound.init( "server started" ) )
            {
                Compat.onServerStarted( server );
                SavePaths.onServerStarted( server );
                this.storageVersionMigration.tryMigrate( server );

                for ( final ConfigModule config : this.configModules.get( StorageVersion.latest() ) )
                {
                    config.loadWithContext();
                }

                DimensionalInventories.LOGGER.info(
                    "{} {} initialised",
                    DimensionalInventories.PROPERTIES.namePretty(),
                    DimensionalInventories.PROPERTIES.version()
                );
            }
        } );
    }

    private void registerPlayerTravelHandler()
    {
        ServerEntityLevelChangeEvents.AFTER_PLAYER_CHANGE_LEVEL.register( ( player, origin, destination ) -> {
            try ( final LostAndFoundContext LAF = LostAndFound.init( "player changed dimension" ) )
            {
                final String originDimensionName = origin.dimension().identifier().toString();
                final String destinationDimensionName = destination.dimension().identifier().toString();

                this.transitionHandler.handlePlayerDimensionChange(
                    player,
                    originDimensionName,
                    destinationDimensionName
                );
            }
        } );
    }

    private void registerPlayerRespawnHandler()
    {
        ServerPlayerEvents.AFTER_RESPAWN.register( ( oldPlayer, newPlayer, alive ) -> {
            try ( final LostAndFoundContext LAF = LostAndFound.init( "player respawned" ) )
            {
                final String originDimensionName = Compat.ENTITY.getWorld( oldPlayer ).dimension().identifier().toString();
                final String destinationDimensionName = Compat.ENTITY.getWorld( newPlayer ).dimension().identifier().toString();

                this.transitionHandler.handlePlayerDimensionChange(
                    newPlayer,
                    originDimensionName,
                    destinationDimensionName
                );
            }
        } );
    }

    private void registerEntityTravelHandler()
    {
        ServerEntityLevelChangeEvents.AFTER_ENTITY_CHANGE_LEVEL.register( ( originalEntity, newEntity, origin,
                                                                            destination ) -> {
            try ( final LostAndFoundContext LAF = LostAndFound.init( "entity changed dimension" ) )
            {
                final String originDimensionName = origin.dimension().identifier().toString();
                final String destinationDimensionName = destination.dimension().identifier().toString();

                this.transitionHandler.handleEntityDimensionChange(
                    newEntity,
                    originDimensionName,
                    destinationDimensionName
                );
            }
        } );
    }
}
