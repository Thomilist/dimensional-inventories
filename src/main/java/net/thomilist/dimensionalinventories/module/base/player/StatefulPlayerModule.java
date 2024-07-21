package net.thomilist.dimensionalinventories.module.base.player;

import net.minecraft.server.network.ServerPlayerEntity;
import net.thomilist.dimensionalinventories.module.base.StatefulModule;
import net.thomilist.dimensionalinventories.module.builtin.pool.DimensionPool;
import net.thomilist.dimensionalinventories.util.SavePaths;

import java.nio.file.Path;

public interface StatefulPlayerModule<T extends PlayerModuleState>
    extends PlayerModule, StatefulModule<T>
{
    T newInstance(ServerPlayerEntity player);

    default Path saveDirectory(ServerPlayerEntity player, DimensionPool dimensionPool)
    {
        return SavePaths.saveDirectory(latestStorageVersion(), dimensionPool, player, groupId());
    }
}
