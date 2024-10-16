package net.thomilist.dimensionalinventories.module.version;

import net.minecraft.server.MinecraftServer;
import net.minecraft.util.WorldSavePath;
import net.minecraft.world.GameMode;
import net.thomilist.dimensionalinventories.DimensionalInventories;
import net.thomilist.dimensionalinventories.exception.ModuleNotRegisteredException;
import net.thomilist.dimensionalinventories.module.ModuleRegistry;
import net.thomilist.dimensionalinventories.module.base.config.ConfigModule;
import net.thomilist.dimensionalinventories.module.builtin.legacy.pool.DimensionPoolConfigModule_SV1;
import net.thomilist.dimensionalinventories.exception.StorageVersionMigrationException;
import net.thomilist.dimensionalinventories.lostandfound.LostAndFound;
import net.thomilist.dimensionalinventories.module.builtin.pool.DimensionPoolConfigModuleState;
import net.thomilist.dimensionalinventories.module.builtin.pool.DimensionPoolConfigModule;
import net.thomilist.dimensionalinventories.module.builtin.pool.DimensionPool;
import net.thomilist.dimensionalinventories.module.builtin.pool.DimensionPoolTransitionHandler;
import net.thomilist.dimensionalinventories.util.DummyServerPlayerEntity;
import net.thomilist.dimensionalinventories.util.SavePaths;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class StorageVersionMigration
{
    private static final String LEGACY_BASE_SAVE_DIRECTORY_NAME = "dimensionalinventories";

    private final StorageVersion targetStorageVersion;
    private final ModuleRegistry<ConfigModule> configModules;
    private final DimensionPoolTransitionHandler transitionHandler;
    private final String legacyBaseSaveDirectoryName;

    private Path legacyBaseSaveDirectory;

    public StorageVersionMigration(
        StorageVersion targetStorageVersion,
        ModuleRegistry<ConfigModule> configModules,
        DimensionPoolTransitionHandler transitionHandler,
        String legacyBaseSaveDirectoryName)
    {
        this.targetStorageVersion = targetStorageVersion;
        this.configModules = configModules;
        this.transitionHandler = transitionHandler;
        this.legacyBaseSaveDirectoryName = legacyBaseSaveDirectoryName;
    }

    public StorageVersionMigration(
        StorageVersion targetStorageVersion,
        ModuleRegistry<ConfigModule> configModules,
        DimensionPoolTransitionHandler transitionHandler)
    {
        this(targetStorageVersion, configModules, transitionHandler, StorageVersionMigration.LEGACY_BASE_SAVE_DIRECTORY_NAME);
    }

    public void tryMigrate(MinecraftServer server)
    {
        try (var LAF = LostAndFound.push("storage version migration"))
        {
            legacyBaseSaveDirectory = server.getSavePath(WorldSavePath.ROOT)
                .resolve(legacyBaseSaveDirectoryName);
            StorageVersion writtenStorageVersion = determineWrittenDataVersion();

            // No data found. Start fresh
            if (writtenStorageVersion == null)
            {
                DimensionalInventories.LOGGER.info("No data found");
                DimensionalInventories.LOGGER.info("Initialising with storage version {}...",
                    targetStorageVersion.version);
            }
            // Outdated data found. Migrate
            else if (writtenStorageVersion != targetStorageVersion)
            {
                DimensionalInventories.LOGGER.info("Data from storage version {} found.",
                    writtenStorageVersion.version);
                DimensionalInventories.LOGGER.info("Migrating to storage version {}...",
                    targetStorageVersion.version);

                migrate(writtenStorageVersion, targetStorageVersion, server);

                DimensionalInventories.LOGGER.info("Migration complete");
            }
            // Up-to-date data found
            else
            {
                DimensionalInventories.LOGGER.info("Data from storage version {} found (up to date)",
                    writtenStorageVersion.version);
            }
        }
    }

    public StorageVersion determineWrittenDataVersion()
    {
        // Reversed to get newest first
        for (StorageVersion storageVersion : StorageVersion.reversed())
        {
            if (Files.exists(SavePaths.saveDirectory(storageVersion)))
            {
                return storageVersion;
            }
        }

        if (Files.exists(legacyBaseSaveDirectory))
        {
            return StorageVersion.V1;
        }

        return null;
    }

    private void migrate(StorageVersion from, StorageVersion to, MinecraftServer server)
        throws StorageVersionMigrationException
    {
        try (var LAF = LostAndFound.push("migrate " + from + ".." + to))
        {
            if (from.version <= StorageVersion.V1.version && to.version >= StorageVersion.V2.version)
            {
                try (var LAF_1_2 = LostAndFound.push(StorageVersion.V1 + ".." + StorageVersion.V2))
                {
                    migrate1to2(server);
                }
            }
        }
    }

    private void migrate1to2(MinecraftServer server)
        throws StorageVersionMigrationException
    {
        DimensionalInventories.LOGGER.info("Preparing migration step from {} to {}...",
            StorageVersion.V1, StorageVersion.V2);

        prepareMigration1to2();
        migrateConfig1to2();
        migratePlayers1to2(server);

        DimensionalInventories.LOGGER.info("Migration step from {} to {} complete",
            StorageVersion.V1, StorageVersion.V2);
    }

    private void prepareMigration1to2()
        throws StorageVersionMigrationException
    {
        try (var LAF = LostAndFound.push("prepare"))
        {
            // Copy old "<world>/dimensionalinventories" directory to new "<world>/dimensional-inventories/v1" directory

            DimensionalInventories.LOGGER.info("Copying {} data...", StorageVersion.V1);

            try
            {
                FileUtils.copyDirectory(
                    legacyBaseSaveDirectory.toFile(),
                    SavePaths.saveDirectory(StorageVersion.V1).toFile()
                );
            }
            catch (IOException e)
            {
                throw new StorageVersionMigrationException(StorageVersion.V1, StorageVersion.V2,
                    "Unable to copy " + StorageVersion.V1 + " data", e);
            }

            // Create directory for v2 data

            DimensionalInventories.LOGGER.info("Creating {} directory...", StorageVersion.V2);

            try
            {
                Files.createDirectories(SavePaths.saveDirectory(StorageVersion.V2));
            }
            catch (IOException e)
            {
                throw new StorageVersionMigrationException(StorageVersion.V1, StorageVersion.V2,
                    "Unable to create" + StorageVersion.V2 + " directory", e);
            }
        }
    }

    @SuppressWarnings("deprecation")
    private void migrateConfig1to2()
    {
        try (var LAF = LostAndFound.push("config"))
        {
            DimensionalInventories.LOGGER.info("Migrating config to {}...", StorageVersion.V2);

            DimensionPoolConfigModule_SV1 legacyConfigModule;
            DimensionPoolConfigModule newConfigModule;

            try
            {
                legacyConfigModule = configModules.get(DimensionPoolConfigModule_SV1.class);
                newConfigModule = configModules.get(DimensionPoolConfigModule.class);
            }
            catch (ModuleNotRegisteredException e)
            {
                throw new StorageVersionMigrationException(StorageVersion.V1, StorageVersion.V2,
                    "Failed to migration dimension pool config", e);
            }

            legacyConfigModule.loadWithContext();
            var newConfigData = DimensionPoolConfigModuleState.fromLegacy(legacyConfigModule.state());
            newConfigModule.loadFromOther(newConfigData);
            newConfigModule.saveWithContext();
        }
    }

    private void migratePlayers1to2(MinecraftServer server)
    {
        try (var LAF = LostAndFound.push("players"))
        {
            // Migrate player data

            DimensionalInventories.LOGGER.info("Migrating player data to {}...", StorageVersion.V2);

            File[] v1DimensionPoolDirectories = SavePaths.saveDirectory(StorageVersion.V1)
                .toFile().listFiles(File::isDirectory);

            if (v1DimensionPoolDirectories == null)
            {
                DimensionalInventories.LOGGER.warn("Migration step from {} to {} finished early: No player data found",
                    StorageVersion.V1, StorageVersion.V2);
                return;
            }

            for (File v1DimensionPoolDirectory : v1DimensionPoolDirectories)
            {
                File[] files = v1DimensionPoolDirectory.listFiles(File::isFile);

                if (files == null)
                {
                    continue;
                }

                String dimensionPoolName = v1DimensionPoolDirectory.getName();
                DimensionalInventories.LOGGER.info("Migrating dimension pool '{}'...", dimensionPoolName);

                // Temporary dimension pool to hold the dimension pool name
                DimensionPool tempDimensionPool = new DimensionPool(dimensionPoolName, GameMode.DEFAULT);

                for (File v1InventoryFile : files)
                {
                    String uuid = v1InventoryFile.getName().replace(".txt", "");
                    DimensionalInventories.LOGGER.debug("Migrating data for player '{}' (UUID)...", uuid);

                    // Dummy player to store data during migration
                    DummyServerPlayerEntity dummyPlayer = new DummyServerPlayerEntity(server, uuid);

                    transitionHandler.loadToPlayer(StorageVersion.V1, tempDimensionPool, dummyPlayer);
                    transitionHandler.saveFromPlayer(StorageVersion.V2, tempDimensionPool, dummyPlayer);
                }
            }
        }
    }
}
