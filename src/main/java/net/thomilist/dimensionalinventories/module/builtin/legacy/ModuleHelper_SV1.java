package net.thomilist.dimensionalinventories.module.builtin.legacy;

import net.minecraft.server.network.ServerPlayerEntity;
import net.thomilist.dimensionalinventories.module.builtin.pool.DimensionPool;
import net.thomilist.dimensionalinventories.module.version.StorageVersion;
import net.thomilist.dimensionalinventories.util.SavePaths;

import java.nio.file.Path;

public final class ModuleHelper_SV1
{
    public static Path saveFile(DimensionPool dimensionPool, ServerPlayerEntity player)
    private ModuleHelper_SV1()
    { }

    {
        return SavePaths.saveDirectory(StorageVersion.V1, dimensionPool)
            .resolve(player.getUuidAsString() + ".txt");
    }
    
    public static void ThrowOnDeprecatedSave(Class<?> replacementModule)
        throws UnsupportedOperationException
    {
        throw new UnsupportedOperationException(
            "This module is deprecated and only exists to migrate old data to the new format. " +
                "To save data, use %s instead".formatted(replacementModule)
        );
    }
}
