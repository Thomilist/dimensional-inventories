package net.thomilist.dimensionalinventories.module.builtin.legacy.pool;

import com.google.gson.Gson;
import net.thomilist.dimensionalinventories.module.base.JsonModule;
import net.thomilist.dimensionalinventories.module.base.ModuleBase;
import net.thomilist.dimensionalinventories.module.base.config.JsonConfigModule;
import net.thomilist.dimensionalinventories.module.builtin.legacy.ModuleHelper_SV1;
import net.thomilist.dimensionalinventories.module.builtin.pool.DimensionPoolConfigModule;
import net.thomilist.dimensionalinventories.module.version.StorageVersion;
import net.thomilist.dimensionalinventories.module.base.config.JsonConfigModule;

@Deprecated
public final class DimensionPoolConfigModule_SV1
    extends ModuleBase
    implements JsonConfigModule<DimensionPoolConfigModuleState_SV1>
{
    private static final String MODULE_ID = "dimension-pools";
    private static final String DESCRIPTION =
        "Configuration of dimension pools, including assigned dimensions, game modes & more.";

    private static final StorageVersion[] STORAGE_VERSIONS =
    {
        StorageVersion.V1
    };

    private final DimensionPoolConfigModuleState_SV1 state = new DimensionPoolConfigModuleState_SV1();

    private static final Gson GSON = JsonModule.GSON_BUILDER
        .registerTypeAdapter(DimensionPoolListSerializerPair_SV1.TYPE, new DimensionPoolListSerializerPair_SV1())
        .create();

    public DimensionPoolConfigModule_SV1(String groupId)
    {
        super(
            DimensionPoolConfigModule_SV1.STORAGE_VERSIONS,
            groupId,
            DimensionPoolConfigModule_SV1.MODULE_ID,
            DimensionPoolConfigModule_SV1.DESCRIPTION
        );
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

    @Override
    public void save()
    {
        // Intentionally not implemented
        ModuleHelper_SV1.ThrowOnDeprecatedSave(DimensionPoolConfigModule.class);
    }
}
