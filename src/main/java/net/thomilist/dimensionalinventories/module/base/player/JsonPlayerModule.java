package net.thomilist.dimensionalinventories.module.base.player;

import net.minecraft.server.level.ServerPlayer;
import net.thomilist.dimensionalinventories.module.base.JsonModule;
import net.thomilist.dimensionalinventories.module.builtin.pool.DimensionPool;

import java.nio.file.Path;

public interface JsonPlayerModule<T extends PlayerModuleState>
    extends StatefulPlayerModule<T>, JsonModule<T>
{
    default Path saveFile( final ServerPlayer player, final DimensionPool dimensionPool )
    {
        return this.saveDirectory( player, dimensionPool ).resolve( this.saveFileName() );
    }

    @Override
    default String noSuchFileWarning()
    {
        return "No data found (default data loaded instead; new data will be saved when leaving the dimension pool)";
    }

    @Override
    default void load( final ServerPlayer player, final DimensionPool dimensionPool )
    {
        final Path saveFile = this.saveFile( player, dimensionPool );
        final T data = this.load( saveFile );
        data.applyToPlayer( player );
    }

    @Override
    default void save( final ServerPlayer player, final DimensionPool dimensionPool )
    {
        final T data = this.newInstance( player );
        final Path saveFile = this.saveFile( player, dimensionPool );
        this.save( saveFile, data );
    }
}
