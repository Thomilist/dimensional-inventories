package net.thomilist.dimensionalinventories.module.builtin.pool;

import com.google.gson.Gson;
import net.thomilist.dimensionalinventories.module.base.JsonModule;
import net.thomilist.dimensionalinventories.module.base.ModuleBase;
import net.thomilist.dimensionalinventories.module.version.StorageVersion;
import net.thomilist.dimensionalinventories.module.base.config.JsonConfigModule;

public final class DimensionPoolConfigModule
    extends ModuleBase
    implements JsonConfigModule<DimensionPoolConfigModuleState>
{
    private static final String MODULE_ID = "dimension-pools";
    private static final String DESCRIPTION =
        "Configuration of dimension pools, including assigned dimensions, game modes & more.";

    private static final StorageVersion[] STORAGE_VERSIONS =
    {
        StorageVersion.V2
    };

    private static final Gson GSON = JsonModule.GSON_BUILDER
        .registerTypeAdapter(DimensionPoolMapSerializerPair.TYPE, new DimensionPoolMapSerializerPair())
        .create();

    private final DimensionPoolConfigModuleState state = new DimensionPoolConfigModuleState();

    public DimensionPoolConfigModule(String groupId)
    {
        super(
            DimensionPoolConfigModule.STORAGE_VERSIONS,
            groupId,
            DimensionPoolConfigModule.MODULE_ID,
            DimensionPoolConfigModule.DESCRIPTION
        );
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
