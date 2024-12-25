package net.thomilist.dimensionalinventories.module.builtin.status;

import com.google.gson.Gson;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.server.network.ServerPlayerEntity;
import net.thomilist.dimensionalinventories.module.base.JsonModule;
import net.thomilist.dimensionalinventories.module.base.ModuleBase;
import net.thomilist.dimensionalinventories.module.version.StorageVersion;
import net.thomilist.dimensionalinventories.module.base.player.JsonPlayerModule;

public final class StatusModule
    extends ModuleBase
    implements JsonPlayerModule<StatusModuleState>
{
    private static final String MODULE_ID = "status";
    private static final String DESCRIPTION =
        "Health, hunger, experience, score & status effects.";

    private static final StorageVersion[] STORAGE_VERSIONS =
    {
        StorageVersion.V2
    };

    private static final Gson GSON = JsonModule.GSON_BUILDER
        .registerTypeAdapter(StatusEffectInstance.class, new StatusEffectSerializerPair())
        .registerTypeAdapter(StatusEffectCollectionSerializerPair.TYPE, new StatusEffectCollectionSerializerPair())
        .create();

    private final StatusModuleState state = new StatusModuleState();

    public StatusModule(String groupId)
    {
        super(
            StatusModule.STORAGE_VERSIONS,
            groupId,
            StatusModule.MODULE_ID,
            StatusModule.DESCRIPTION
        );
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
