package net.thomilist.dimensionalinventories.gametest.util;

import net.minecraft.world.GameMode;
import net.thomilist.dimensionalinventories.DimensionalInventories;
import net.thomilist.dimensionalinventories.module.ModuleGroup;
import net.thomilist.dimensionalinventories.module.builtin.MainModuleGroup;
import net.thomilist.dimensionalinventories.module.builtin.pool.DimensionPoolConfigModule;

import java.util.ArrayList;
import java.util.Arrays;

public class BasicModSetup
{
    public static final String ORIGIN_DIMENSION_POOL_ID = "origin";
    public static final String DESTINATION_DIMENSION_POOL_ID = "destination";

    public static final String ORIGIN_DIMENSION = "minecraft:overworld";
    public static final String DESTINATION_DIMENSION = "minecraft:the_nether";
    public static final String UNCONFIGURED_DIMENSION = "minecraft:the_end";

    public final DimensionalInventories instance = new DimensionalInventories();
    public final DimensionPoolConfigModule dimensionPoolConfig;

    private BasicModSetup( final ModuleGroup... moduleGroups )
    {
        for ( final ModuleGroup moduleGroup : moduleGroups )
        {
            this.instance.registerModules( moduleGroup );
        }

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

    public static BasicModSetup withDefaultModules()
    {
        return new BasicModSetup( new MainModuleGroup() );
    }

    public static BasicModSetup withDefaultAndAdditionalModules( final ModuleGroup... additionalModuleGroups )
    {
        final ArrayList<ModuleGroup> moduleGroups = new ArrayList<>();

        moduleGroups.add( new MainModuleGroup() );
        moduleGroups.addAll( Arrays.stream( additionalModuleGroups ).toList() );

        return new BasicModSetup( moduleGroups.toArray( ModuleGroup[]::new ) );
    }

    public static BasicModSetup withModules( final ModuleGroup... moduleGroups )
    {
        return new BasicModSetup( moduleGroups );
    }
}
