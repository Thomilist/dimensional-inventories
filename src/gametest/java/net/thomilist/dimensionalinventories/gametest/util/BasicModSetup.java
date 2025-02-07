package net.thomilist.dimensionalinventories.gametest.util;

import net.minecraft.world.GameMode;
import net.thomilist.dimensionalinventories.DimensionalInventories;
import net.thomilist.dimensionalinventories.module.builtin.MainModuleGroup;
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
        this.instance.registerModules( new MainModuleGroup() );
        this.dimensionPoolConfig = this.instance.configModules.get( DimensionPoolConfigModule.class );

        this.dimensionPoolConfig.state().createPool( BasicModSetup.ORIGIN_DIMENSION_POOL_ID, GameMode.DEFAULT );
        this.dimensionPoolConfig.state().createPool( BasicModSetup.DESTINATION_DIMENSION_POOL_ID, GameMode.DEFAULT );

        this.dimensionPoolConfig
            .state()
            .assignDimensionToPool( BasicModSetup.ORIGIN_DIMENSION, BasicModSetup.ORIGIN_DIMENSION_POOL_ID );

        this.dimensionPoolConfig
            .state()
            .assignDimensionToPool( BasicModSetup.DESTINATION_DIMENSION, BasicModSetup.DESTINATION_DIMENSION_POOL_ID );
    }
}
