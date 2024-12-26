package net.thomilist.dimensionalinventories.module.builtin.legacy.pool;

import net.thomilist.dimensionalinventories.module.base.config.ConfigModuleState;
import net.thomilist.dimensionalinventories.module.builtin.pool.DimensionPoolConfigModuleState;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * @deprecated This is only intended to hold data of storage version 1 when loading old data during migration. When
 * loading or saving new data, use {@link DimensionPoolConfigModuleState} instead.
 */
@Deprecated
public class DimensionPoolConfigModuleState_SV1
    implements ConfigModuleState
{
    public ArrayList<DimensionPool_SV1> dimensionPools = new ArrayList<>();

    @Override
    public Type type()
    {
        return DimensionPoolConfigModuleState_SV1.class;
    }
}
