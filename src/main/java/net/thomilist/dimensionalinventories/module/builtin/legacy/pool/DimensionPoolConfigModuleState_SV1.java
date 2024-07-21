package net.thomilist.dimensionalinventories.module.builtin.legacy.pool;

import net.thomilist.dimensionalinventories.module.base.config.ConfigModuleState;

import java.lang.reflect.Type;
import java.util.ArrayList;

@Deprecated
public class DimensionPoolConfigModuleState_SV1
    implements ConfigModuleState
{
    @Override
    public Type type()
    {
        return DimensionPoolConfigModuleState_SV1.class;
    }

    public ArrayList<DimensionPool_SV1> dimensionPools = new ArrayList<>();
}
