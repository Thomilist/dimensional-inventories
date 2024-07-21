package net.thomilist.dimensionalinventories.module.builtin.legacy;

import net.minecraft.server.network.ServerPlayerEntity;
import net.thomilist.dimensionalinventories.module.builtin.pool.DimensionPool;
import net.thomilist.dimensionalinventories.module.version.StorageVersion;
import net.thomilist.dimensionalinventories.util.SavePaths;

import java.nio.file.Path;

public class ModuleHelper_SV1
{
    public static Path saveFile(DimensionPool dimensionPool, ServerPlayerEntity player)
    {
        return SavePaths.saveDirectory(StorageVersion.V1, dimensionPool)
            .resolve(player.getUuidAsString() + ".txt");
    }
}
