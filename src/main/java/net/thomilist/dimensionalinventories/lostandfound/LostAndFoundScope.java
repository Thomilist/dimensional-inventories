package net.thomilist.dimensionalinventories.lostandfound;

import net.minecraft.server.network.ServerPlayerEntity;
import net.thomilist.dimensionalinventories.module.base.Module;
import net.thomilist.dimensionalinventories.module.base.config.ConfigModule;
import net.thomilist.dimensionalinventories.module.base.player.PlayerModule;
import net.thomilist.dimensionalinventories.module.builtin.pool.DimensionPool;
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
        if (layer instanceof DimensionPool)
        {
            return ((DimensionPool) layer).getDisplayName() + " (" + ((DimensionPool) layer).getId() + ")";
        }
        else if (layer instanceof ServerPlayerEntity)
        {
            return ((ServerPlayerEntity) layer).getName().getString() + " (" + ((ServerPlayerEntity) layer).getUuidAsString() + ")";
        }
        else if (layer instanceof ConfigModule)
        {
            return LogHelper.joinScopes(((ConfigModule) layer).groupId(), ((ConfigModule) layer).moduleId() + " (config module)");
        }
        else if (layer instanceof PlayerModule)
        {
            return LogHelper.joinScopes(((PlayerModule) layer).groupId(), ((PlayerModule) layer).moduleId() + " (player module)");
        }
        else if (layer instanceof Module)
        {
            return LogHelper.joinScopes(((Module) layer).groupId(), ((Module) layer).moduleId());
        }
        else
        {
            return layer.toString();
        }
    }
}
