package net.thomilist.dimensionalinventories.lostandfound;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.thomilist.dimensionalinventories.compatibility.LimitedCompatibility;
import net.thomilist.dimensionalinventories.util.StringHelper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class LostAndFoundScope
    implements AutoCloseable
{
    private final LostAndFoundContext context;
    private final Collection<Object> layers;

    public LostAndFoundScope( final LostAndFoundContext context, final Object... layers )
    {
        this.context = context;
        this.layers = List.of( layers );
    }

    // >=21: switch ( layer )
    //  <21: if ( layer instanceof <type> )
    @LimitedCompatibility( target = "Java",
                           versions = "<21" )
    private static String formatLayer( final Object layer )
    {
        if ( layer instanceof LostAndFoundFormattable lostAndFoundFormattable )
        {
            return lostAndFoundFormattable.toLostAndFoundScopeString();
        }
        else if ( layer instanceof ServerPlayerEntity serverPlayerEntity )
        {
            return serverPlayerEntity.getName().getString() + " (" + serverPlayerEntity.getUuidAsString() + ')';
        }
        else
        {
            return layer.toString();
        }
    }

    public Collection<Object> layers()
    {
        return this.layers;
    }

    @Override
    public void close()
    {
        this.context.pop();
    }

    @Override
    public String toString()
    {
        final Collection<String> formattedLayers = new ArrayList<>();

        for ( final Object layer : this.layers )
        {
            formattedLayers.add( LostAndFoundScope.formatLayer( layer ) );
        }

        return StringHelper.joinScopes( formattedLayers );
    }
}
