package net.thomilist.dimensionalinventories.gametest.util;

import net.minecraft.world.GameMode;
import net.thomilist.dimensionalinventories.DimensionalInventories;
import net.thomilist.dimensionalinventories.module.builtin.pool.DimensionPoolConfigModule;

public class BasicModSetup
{
    public static final String ORIGIN_DIMENSION_POOL_ID = "origin";
    public static final String DESTINATION_DIMENSION_POOL_ID = "destination";

    public static final String ORIGIN_DIMENSION = "minecraft:overworld";
    public static final String DESTINATION_DIMENSION = "minecraft:the_nether";
    public static final String UNCONFIGURED_DIMENSION = "minecraft:the_end";

    public final DimensionalInventories instance = new DimensionalInventories();
    public final DimensionPoolConfigModule dimensionPoolConfig;

    public BasicModSetup()
    {
        instance.registerBuiltinModules();
        dimensionPoolConfig = instance.configModules.get(DimensionPoolConfigModule.class);

        dimensionPoolConfig.state().createPool(
            BasicModSetup.ORIGIN_DIMENSION_POOL_ID,
            GameMode.DEFAULT
        );

        dimensionPoolConfig.state().createPool(
            BasicModSetup.DESTINATION_DIMENSION_POOL_ID,
            GameMode.DEFAULT
        );

        dimensionPoolConfig.state().assignDimensionToPool(
            BasicModSetup.ORIGIN_DIMENSION,
            BasicModSetup.ORIGIN_DIMENSION_POOL_ID
        );

        dimensionPoolConfig.state().assignDimensionToPool(
            BasicModSetup.DESTINATION_DIMENSION,
            BasicModSetup.DESTINATION_DIMENSION_POOL_ID
        );
    }
}
