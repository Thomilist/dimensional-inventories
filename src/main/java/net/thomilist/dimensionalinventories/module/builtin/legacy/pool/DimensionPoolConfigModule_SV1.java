package net.thomilist.dimensionalinventories.module.builtin.legacy.pool;

import com.google.gson.Gson;
import net.thomilist.dimensionalinventories.module.base.JsonModule;
import net.thomilist.dimensionalinventories.module.base.ModuleBase;
import net.thomilist.dimensionalinventories.module.version.StorageVersion;
import net.thomilist.dimensionalinventories.module.base.config.JsonConfigModule;

@Deprecated
public class DimensionPoolConfigModule_SV1
    extends ModuleBase
    implements JsonConfigModule<DimensionPoolConfigModuleState_SV1>
{
    static final DimensionPoolConfigModuleState_SV1 STATE = new DimensionPoolConfigModuleState_SV1();

    private static final Gson GSON = JsonModule.GSON_BUILDER
        .registerTypeAdapter(DimensionPoolListSerializerPair_SV1.TYPE, new DimensionPoolListSerializerPair_SV1())
        .create();

    public DimensionPoolConfigModule_SV1(
        StorageVersion[] storageVersions,
        String groupId,
        String moduleId,
        String description)
    {
        super(storageVersions, groupId, moduleId, description);
    }

    @Override
    public DimensionPoolConfigModuleState_SV1 newInstance()
    {
        return new DimensionPoolConfigModuleState_SV1();
    }

    @Override
    public DimensionPoolConfigModuleState_SV1 state()
    {
        return DimensionPoolConfigModule_SV1.STATE;
    }

    @Override
    public DimensionPoolConfigModuleState_SV1 defaultState()
    {
        return new DimensionPoolConfigModuleState_SV1();
    }

    @Override
    public Gson gson()
    {
        return DimensionPoolConfigModule_SV1.GSON;
    }

    @Override
    public void loadFromOther(DimensionPoolConfigModuleState_SV1 other)
    {
        DimensionPoolConfigModule_SV1.STATE.dimensionPools.clear();
        DimensionPoolConfigModule_SV1.STATE.dimensionPools.addAll(other.dimensionPools);
    }

    @Override
    public DimensionPoolConfigModuleState_SV1 loadFromJsonString(String json)
    {
        final var data = new DimensionPoolConfigModuleState_SV1();
        data.dimensionPools = gson().fromJson(json, DimensionPoolListSerializerPair_SV1.TYPE);
        return data;
    }
}
