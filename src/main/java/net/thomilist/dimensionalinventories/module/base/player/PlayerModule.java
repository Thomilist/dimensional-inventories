package net.thomilist.dimensionalinventories.module.base.player;

import net.minecraft.server.network.ServerPlayerEntity;
import net.thomilist.dimensionalinventories.lostandfound.LostAndFound;
import net.thomilist.dimensionalinventories.module.base.Module;
import net.thomilist.dimensionalinventories.module.builtin.pool.DimensionPool;

public interface PlayerModule
    extends Module
{
    void load(ServerPlayerEntity player, DimensionPool dimensionPool);
    void save(ServerPlayerEntity player, DimensionPool dimensionPool);

    default void loadWithContext(ServerPlayerEntity player, DimensionPool dimensionPool)
    {
        try (var LAF = LostAndFound.push(dimensionPool, player, this, "load"))
        {
            load(player, dimensionPool);
        }
    }

    default void saveWithContext(ServerPlayerEntity player, DimensionPool dimensionPool)
    {
        try (var LAF = LostAndFound.push(dimensionPool, player, this, "save"))
        {
            save(player, dimensionPool);
        }
    }
}
