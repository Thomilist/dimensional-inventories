package net.thomilist.dimensionalinventories.exception;

import net.thomilist.dimensionalinventories.DimensionalInventories;
import net.thomilist.dimensionalinventories.module.version.StorageVersion;

public class StorageVersionMigrationException
    extends RuntimeException
{
    private static final String MESSAGE_PREFIX = "Failed to migrate Dimensional Inventories data";

    public StorageVersionMigrationException( final StorageVersion from, final StorageVersion to )
    {
        super( StorageVersionMigrationException.MESSAGE_PREFIX );
        StorageVersionMigrationException.logMigrationStep( from, to, null );
    }

    public StorageVersionMigrationException( final StorageVersion from, final StorageVersion to, final String message )
    {
        super( StorageVersionMigrationException.formatMessage( message ) );
        StorageVersionMigrationException.logMigrationStep( from, to, null );
    }

    public StorageVersionMigrationException( final StorageVersion from,
                                             final StorageVersion to,
                                             final String message,
                                             final Throwable cause )
    {
        super( StorageVersionMigrationException.formatMessage( message ), cause );
        StorageVersionMigrationException.logMigrationStep( from, to, cause );
    }

    private static void logMigrationStep( final StorageVersion from, final StorageVersion to, final Throwable cause )
    {
        DimensionalInventories.LOGGER.error(
            "Failed to migrate storage from version {} to {}",
            from.version,
            to.version
        );

        if ( cause != null )
        {
            DimensionalInventories.LOGGER.error( "Caused by:", cause );
        }
    }

    private static String formatMessage( final String message )
    {
        return StorageVersionMigrationException.MESSAGE_PREFIX + ": " + message;
    }
}