package net.thomilist.dimensionalinventories.module.base.player;

import net.minecraft.server.level.ServerPlayer;
import net.thomilist.dimensionalinventories.module.base.StatefulModule;
import net.thomilist.dimensionalinventories.module.builtin.pool.DimensionPool;
import net.thomilist.dimensionalinventories.util.SavePaths;

import java.nio.file.Path;

public interface StatefulPlayerModule<T extends PlayerModuleState>
    extends PlayerModule, StatefulModule<T>
{
    T newInstance( ServerPlayer player );

    default Path saveDirectory( final ServerPlayer player, final DimensionPool dimensionPool )
    {
        return SavePaths.saveDirectory( this.latestStorageVersion(), dimensionPool, player, this.groupId() );
    }
}
