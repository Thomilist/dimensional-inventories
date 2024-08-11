package net.thomilist.dimensionalinventories.module.builtin.pool;

import com.google.gson.Gson;
import net.thomilist.dimensionalinventories.module.base.JsonModule;
import net.thomilist.dimensionalinventories.module.base.ModuleBase;
import net.thomilist.dimensionalinventories.module.version.StorageVersion;
import net.thomilist.dimensionalinventories.module.base.config.JsonConfigModule;

public class DimensionPoolConfigModule
    extends ModuleBase
    implements JsonConfigModule<DimensionPoolConfigModuleState>
{
    private final DimensionPoolConfigModuleState state = new DimensionPoolConfigModuleState();

    private static final Gson GSON = JsonModule.GSON_BUILDER
        .registerTypeAdapter(DimensionPoolMapSerializerPair.TYPE, new DimensionPoolMapSerializerPair())
        .create();

    public DimensionPoolConfigModule(
        StorageVersion[] storageVersions,
        String groupId,
        String moduleId,
        String description)
    {
        super(storageVersions, groupId, moduleId, description);
    }

    @Override
    public DimensionPoolConfigModuleState newInstance()
    {
        return new DimensionPoolConfigModuleState();
    }

    @Override
    public DimensionPoolConfigModuleState state()
    {
        return state;
    }

    @Override
    public DimensionPoolConfigModuleState defaultState()
    {
        return DimensionPoolConfigModuleState.createDefault();
    }

    @Override
    public Gson gson()
    {
        return DimensionPoolConfigModule.GSON;
    }

    @Override
    public void loadFromOther(DimensionPoolConfigModuleState other)
    {
        state().dimensionPools.clear();
        state().dimensionPools.putAll(other.dimensionPools);
    }
}
