package net.thomilist.dimensionalinventories.module.builtin.legacy;

import net.minecraft.server.level.ServerPlayer;
import net.thomilist.dimensionalinventories.module.builtin.pool.DimensionPool;
import net.thomilist.dimensionalinventories.module.version.StorageVersion;
import net.thomilist.dimensionalinventories.util.SavePaths;
import org.jetbrains.annotations.ApiStatus;

import java.nio.file.Path;

/**
 * @deprecated This is a utility class intended only for use during migration of old storage version 1 data.
 */
@Deprecated
@ApiStatus.Internal
public final class ModuleHelper_SV1
{
    private ModuleHelper_SV1()
    { }

    public static Path saveFile( final DimensionPool dimensionPool, final ServerPlayer player )
    {
        return SavePaths.saveDirectory( StorageVersion.V1, dimensionPool ).resolve( player.getStringUUID() + ".txt" );
    }

    public static void ThrowOnDeprecatedSave( final Class<?> replacementModule )
        throws UnsupportedOperationException
    {
        throw new UnsupportedOperationException(
            "This module is deprecated and only exists to migrate old data to the new format. " +
            "To save data, use %s instead".formatted( replacementModule ) );
    }
}
