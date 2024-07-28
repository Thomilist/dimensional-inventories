package net.thomilist.dimensionalinventories.exception;

import net.thomilist.dimensionalinventories.module.version.StorageVersion;
import net.thomilist.dimensionalinventories.DimensionalInventories;

public class StorageVersionMigrationException
    extends RuntimeException
{
    private static final String MESSAGE_PREFIX = "Failed to migrate Dimensional Inventories data";

    public StorageVersionMigrationException(StorageVersion from, StorageVersion to)
    {
        super(StorageVersionMigrationException.MESSAGE_PREFIX);
        logMigrationStep(from, to, null);
    }

    public StorageVersionMigrationException(StorageVersion from, StorageVersion to, String message)
    {
        super(StorageVersionMigrationException.formatMessage(message));
        StorageVersionMigrationException.logMigrationStep(from, to, null);
    }

    public StorageVersionMigrationException(StorageVersion from, StorageVersion to, String message, Throwable cause)
    {
        super(StorageVersionMigrationException.formatMessage(message), cause);
        StorageVersionMigrationException.logMigrationStep(from, to, cause);
    }

    private static String formatMessage(String message)
    {
        return StorageVersionMigrationException.MESSAGE_PREFIX + ": " + message;
    }

    private static void logMigrationStep(StorageVersion from, StorageVersion to, Throwable cause)
    {
        DimensionalInventories.LOGGER.error("Failed to migrate storage from version {} to {}", from.version, to.version);

        if (cause != null)
        {
            DimensionalInventories.LOGGER.error("Caused by:", cause);
        }
    }
}