package net.thomilist.dimensionalinventories.lostandfound;

import net.minecraft.server.network.ServerPlayerEntity;
import net.thomilist.dimensionalinventories.util.LogHelper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class LostAndFoundScope
    implements AutoCloseable
{
    private final LostAndFoundContext context;
    private final Collection<Object> layers;

    public LostAndFoundScope(LostAndFoundContext context, Object... layers)
    {
        this.context = context;
        this.layers = List.of(layers);
    }

    public Collection<Object> layers()
    {
        return layers;
    }

    @Override
    public void close()
    {
        context.pop();
    }

    @Override
    public String toString()
    {
        Collection<String> formattedLayers = new ArrayList<>();

        for (var layer : layers)
        {
            formattedLayers.add(LostAndFoundScope.formatLayer(layer));
        }

        return LogHelper.joinScopes(formattedLayers);
    }

    private static String formatLayer(Object layer)
    {
        if (layer instanceof LostAndFoundFormattable lostAndFoundFormattable)
        {
            return lostAndFoundFormattable.toLostAndFoundScopeString();
        }
        else if (layer instanceof ServerPlayerEntity serverPlayerEntity)
        {
            return serverPlayerEntity.getName().getString() + " (" + serverPlayerEntity.getUuidAsString() + ")";
        }
        else
        {
            return layer.toString();
        }
    }
}
