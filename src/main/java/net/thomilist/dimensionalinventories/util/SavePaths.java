package net.thomilist.dimensionalinventories.util;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.storage.LevelResource;
import net.thomilist.dimensionalinventories.module.base.Module;
import net.thomilist.dimensionalinventories.module.builtin.pool.DimensionPool;
import net.thomilist.dimensionalinventories.module.version.StorageVersion;

import java.nio.file.Path;

public final class SavePaths
{
    private static final String BASE_SAVE_DIRECTORY_NAME = "dimensional-inventories";
    private static final String DATA_DIRECTORY_NAME = "data";
    private static final String CONFIG_DIRECTORY_NAME = "config";
    private static final String LOST_AND_FOUND_DIRECTORY_NAME = "lost+found";
    private static final String DELETED_DIRECTORY_NAME = "deleted";
    private static Path baseSaveDirectory;

    private SavePaths()
    { }

    public static void onServerStarted( final MinecraftServer server )
    {
        SavePaths.baseSaveDirectory = server
            .getWorldPath( LevelResource.ROOT )
            .resolve( SavePaths.BASE_SAVE_DIRECTORY_NAME );
    }

    public static Path saveDirectory()
    {
        return SavePaths.baseSaveDirectory;
    }

    public static Path saveDirectory( final StorageVersion storageVersion )
    {
        return SavePaths.saveDirectory().resolve( storageVersion.toString() );
    }

    public static Path saveDirectory( final StorageVersion storageVersion, final DimensionPool dimensionPool )
    {
        return switch ( storageVersion )
        {
            case V1 -> SavePaths.saveDirectory( storageVersion ).resolve( dimensionPool.getId() );
            case V2 -> SavePaths
                .saveDirectory( storageVersion )
                .resolve( SavePaths.DATA_DIRECTORY_NAME )
                .resolve( dimensionPool.getId() );
        };
    }

    public static Path saveDirectory( final StorageVersion storageVersion,
                                      final DimensionPool dimensionPool,
                                      final ServerPlayer player )
    {
        return switch ( storageVersion )
        {
            case V1 -> SavePaths.saveDirectory( storageVersion, dimensionPool );
            case V2 -> SavePaths.saveDirectory( storageVersion, dimensionPool ).resolve( player.getStringUUID() );
        };
    }

    public static Path saveDirectory( final StorageVersion storageVersion,
                                      final DimensionPool dimensionPool,
                                      final ServerPlayer player,
                                      final String namespace )
    {
        return switch ( storageVersion )
        {
            case V1 -> SavePaths.saveDirectory( storageVersion, dimensionPool );
            case V2 -> SavePaths.saveDirectory( storageVersion, dimensionPool, player ).resolve( namespace );
        };
    }

    public static Path configDirectory( final StorageVersion storageVersion )
    {
        return switch ( storageVersion )
        {
            case V1 -> SavePaths.saveDirectory( storageVersion );
            case V2 -> SavePaths.saveDirectory( storageVersion ).resolve( SavePaths.CONFIG_DIRECTORY_NAME );
        };
    }

    public static Path configDirectory( final StorageVersion storageVersion, final String namespace )
    {
        return switch ( storageVersion )
        {
            case V1 -> SavePaths.saveDirectory( storageVersion );
            case V2 -> SavePaths.configDirectory( storageVersion ).resolve( namespace );
        };
    }

    public static Path lostAndFoundDirectory( final StorageVersion storageVersion )
    {
        return SavePaths.saveDirectory( storageVersion ).resolve( SavePaths.LOST_AND_FOUND_DIRECTORY_NAME );
    }

    public static Path lostAndFoundDirectory( final StorageVersion storageVersion, final DimensionPool dimensionPool )
    {
        return SavePaths
            .saveDirectory( storageVersion, dimensionPool )
            .resolve( SavePaths.LOST_AND_FOUND_DIRECTORY_NAME );
    }

    public static Path lostAndFoundDirectory( final StorageVersion storageVersion,
                                              final DimensionPool dimensionPool,
                                              final ServerPlayer player )
    {
        return SavePaths
            .saveDirectory( storageVersion, dimensionPool, player )
            .resolve( SavePaths.LOST_AND_FOUND_DIRECTORY_NAME );
    }

    public static Path lostAndFoundDirectory( final StorageVersion storageVersion,
                                              final DimensionPool dimensionPool,
                                              final ServerPlayer player,
                                              final Module module )
    {
        return SavePaths
            .saveDirectory( storageVersion, dimensionPool, player, module.groupId() )
            .resolve( SavePaths.LOST_AND_FOUND_DIRECTORY_NAME );
    }
}
