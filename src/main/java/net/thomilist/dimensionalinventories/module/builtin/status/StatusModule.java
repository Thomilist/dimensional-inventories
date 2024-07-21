package net.thomilist.dimensionalinventories.module.builtin.status;

import com.google.gson.Gson;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.server.network.ServerPlayerEntity;
import net.thomilist.dimensionalinventories.module.base.JsonModule;
import net.thomilist.dimensionalinventories.module.base.ModuleBase;
import net.thomilist.dimensionalinventories.module.version.StorageVersion;
import net.thomilist.dimensionalinventories.module.base.player.JsonPlayerModule;

public class StatusModule
    extends ModuleBase
    implements JsonPlayerModule<StatusModuleState>
{
    static final StatusModuleState STATE = new StatusModuleState();

    private static final Gson GSON = JsonModule.GSON_BUILDER
        .registerTypeAdapter(StatusEffectInstance.class, new StatusEffectSerializerPair())
        .registerTypeAdapter(StatusEffectCollectionSerializerPair.TYPE, new StatusEffectCollectionSerializerPair())
        .create();

    public StatusModule(StorageVersion[] storageVersions, String groupId, String moduleId, String description)
    {
        super(storageVersions, groupId, moduleId, description);
    }

    @Override
    public StatusModuleState newInstance(ServerPlayerEntity player)
    {
        return new StatusModuleState(player);
    }

    @Override
    public StatusModuleState state()
    {
        return StatusModule.STATE;
    }

    @Override
    public StatusModuleState defaultState()
    {
        return new StatusModuleState();
    }

    @Override
    public Gson gson()
    {
        return StatusModule.GSON;
    }
}
