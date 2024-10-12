package net.thomilist.dimensionalinventories.module.builtin.shoulderentity;

import com.google.gson.Gson;
import net.minecraft.server.network.ServerPlayerEntity;
import net.thomilist.dimensionalinventories.module.base.JsonModule;
import net.thomilist.dimensionalinventories.module.base.ModuleBase;
import net.thomilist.dimensionalinventories.module.base.player.JsonPlayerModule;
import net.thomilist.dimensionalinventories.module.version.StorageVersion;

public class ShoulderEntityModule
    extends ModuleBase
    implements JsonPlayerModule<ShoulderEntityModuleState>
{
    static final ShoulderEntityModuleState STATE = new ShoulderEntityModuleState();

    private static final Gson GSON = JsonModule.GSON_BUILDER
        .create();

    public ShoulderEntityModule(StorageVersion[] storageVersions, String groupId, String moduleId, String description)
    {
        super(storageVersions, groupId, moduleId, description);
    }

    @Override
    public Gson gson()
    {
        return ShoulderEntityModule.GSON;
    }

    @Override
    public ShoulderEntityModuleState newInstance(ServerPlayerEntity player)
    {
        return new ShoulderEntityModuleState(player);
    }

    @Override
    public ShoulderEntityModuleState state()
    {
        return ShoulderEntityModule.STATE;
    }

    @Override
    public ShoulderEntityModuleState defaultState()
    {
        return new ShoulderEntityModuleState();
    }
}
