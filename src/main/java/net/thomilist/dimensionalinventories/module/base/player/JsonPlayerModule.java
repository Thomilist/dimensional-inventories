package net.thomilist.dimensionalinventories.module.base.player;

import net.minecraft.server.network.ServerPlayerEntity;
import net.thomilist.dimensionalinventories.module.base.JsonModule;
import net.thomilist.dimensionalinventories.module.builtin.pool.DimensionPool;

import java.nio.file.Path;

public interface JsonPlayerModule<T extends PlayerModuleState>
    extends StatefulPlayerModule<T>, JsonModule<T>
{
    default Path saveFile(ServerPlayerEntity player, DimensionPool dimensionPool)
    {
        return saveDirectory(player, dimensionPool)
            .resolve(saveFileName());
    }

    @Override
    default String noSuchFileWarning()
    {
        return "No data found (default data loaded instead; new data will be saved when leaving the dimension pool)";
    }

    @Override
    default void load(ServerPlayerEntity player, DimensionPool dimensionPool)
    {
        final Path saveFile = saveFile(player, dimensionPool);
        final T data = load(saveFile);
        data.applyToPlayer(player);
    }

    @Override
    default void save(ServerPlayerEntity player, DimensionPool dimensionPool)
    {
        final T data = newInstance(player);
        final Path saveFile = saveFile(player, dimensionPool);
        save(saveFile, data);
    }
}
