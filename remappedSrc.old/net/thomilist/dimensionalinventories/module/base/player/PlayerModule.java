package net.thomilist.dimensionalinventories.module.base.player;

import net.minecraft.server.network.ServerPlayerEntity;
import net.thomilist.dimensionalinventories.lostandfound.LostAndFound;
import net.thomilist.dimensionalinventories.lostandfound.LostAndFoundScope;
import net.thomilist.dimensionalinventories.module.base.Module;
import net.thomilist.dimensionalinventories.module.builtin.pool.DimensionPool;

public interface PlayerModule
    extends Module
{
    void load( ServerPlayerEntity player, DimensionPool dimensionPool );

    void save( ServerPlayerEntity player, DimensionPool dimensionPool );

    @Override
    default String category()
    {
        return "player";
    }

    @Override
    default String toLostAndFoundScopeString()
    {
        return Module.super.toLostAndFoundScopeString() + " (player module)";
    }

    default void loadWithContext( final ServerPlayerEntity player, final DimensionPool dimensionPool )
    {
        try ( final LostAndFoundScope LAF = LostAndFound.push( dimensionPool, player, this, "load" ) )
        {
            this.load( player, dimensionPool );
        }
    }

    default void saveWithContext( final ServerPlayerEntity player, final DimensionPool dimensionPool )
    {
        try ( final LostAndFoundScope LAF = LostAndFound.push( dimensionPool, player, this, "save" ) )
        {
            this.save( player, dimensionPool );
        }
    }
}
