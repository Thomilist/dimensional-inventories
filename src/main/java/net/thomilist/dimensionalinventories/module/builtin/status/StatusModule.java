package net.thomilist.dimensionalinventories.module.builtin.status;

import com.google.gson.Gson;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.thomilist.dimensionalinventories.module.base.JsonModule;
import net.thomilist.dimensionalinventories.module.base.ModuleBase;
import net.thomilist.dimensionalinventories.module.base.player.JsonPlayerModule;
import net.thomilist.dimensionalinventories.module.version.StorageVersion;

public final class StatusModule
    extends ModuleBase
    implements JsonPlayerModule<StatusModuleState>
{
    private static final String MODULE_ID = "status";
    private static final String DESCRIPTION = "Health, hunger, experience, score & status effects.";

    private static final StorageVersion[] STORAGE_VERSIONS = {
        StorageVersion.V2
    };

    private static final Gson GSON = JsonModule.GSON_BUILDER
        .registerTypeAdapter( MobEffectInstance.class, new StatusEffectSerializerPair() )
        .registerTypeAdapter( StatusEffectCollectionSerializerPair.TYPE, new StatusEffectCollectionSerializerPair() )
        .create();

    private final StatusModuleState state = new StatusModuleState();

    public StatusModule( final String groupId )
    {
        super( StatusModule.STORAGE_VERSIONS, groupId, StatusModule.MODULE_ID, StatusModule.DESCRIPTION );
    }

    @Override
    public StatusModuleState newInstance( final ServerPlayer player )
    {
        return new StatusModuleState( player );
    }

    @Override
    public StatusModuleState state()
    {
        return this.state;
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
