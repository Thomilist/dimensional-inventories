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
        return switch (layer)
        {
            case DimensionPool dimensionPool ->
                dimensionPool.getDisplayName() + " (" + dimensionPool.getId() + ")";
            case ServerPlayerEntity serverPlayerEntity ->
                serverPlayerEntity.getName().getString() + " (" + serverPlayerEntity.getUuidAsString() + ")";
            case ConfigModule configModule ->
                LogHelper.joinScopes(configModule.groupId(), configModule.moduleId() + " (config module)");
            case PlayerModule playerModule ->
                LogHelper.joinScopes(playerModule.groupId(), playerModule.moduleId() + " (player module)");
            case Module module ->
                LogHelper.joinScopes(module.groupId(), module.moduleId());
            default ->
                layer.toString();
        };
    }
}
