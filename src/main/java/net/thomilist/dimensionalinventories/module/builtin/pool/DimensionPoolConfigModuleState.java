package net.thomilist.dimensionalinventories.module.builtin.pool;

import net.minecraft.world.level.GameType;
import net.thomilist.dimensionalinventories.DimensionalInventories;
import net.thomilist.dimensionalinventories.module.base.config.ConfigModuleState;
import net.thomilist.dimensionalinventories.module.builtin.legacy.pool.DimensionPoolConfigModuleState_SV1;
import net.thomilist.dimensionalinventories.module.builtin.legacy.pool.DimensionPool_SV1;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Optional;

public class DimensionPoolConfigModuleState
    implements ConfigModuleState
{
    public HashMap<String, DimensionPool> dimensionPools = new HashMap<>();

    public static DimensionPoolConfigModuleState createDefault()
    {
        final DimensionPoolConfigModuleState config = new DimensionPoolConfigModuleState();
        final DimensionPool dimensionPool = DimensionPool.createDefault();
        config.dimensionPools.put( dimensionPool.getId(), dimensionPool );
        return config;
    }

    @SuppressWarnings( "deprecation" )
    public static DimensionPoolConfigModuleState fromLegacy( final DimensionPoolConfigModuleState_SV1 legacyConfigData )
    {
        final DimensionPoolConfigModuleState newConfigData = new DimensionPoolConfigModuleState();

        for ( final DimensionPool_SV1 legacyPool : legacyConfigData.dimensionPools )
        {
            final DimensionPool newPool = DimensionPool.fromLegacy( legacyPool );
            newConfigData.dimensionPools.put( newPool.getId(), newPool );
        }

        return newConfigData;
    }

    @Override
    public Type type()
    {
        return DimensionPoolConfigModuleState.class;
    }

    public boolean poolExists( final String dimensionPoolId )
    {
        return this.dimensionPools.containsKey( dimensionPoolId );
    }

    public Optional<DimensionPool> poolWithId( final String dimensionPoolId )
    {
        return Optional.ofNullable( this.dimensionPools.get( dimensionPoolId ) );
    }

    public Optional<DimensionPool> poolWithDimension( final String dimension )
    {
        return this.poolWithDimension( dimension, false );
    }

    public Optional<DimensionPool> poolWithDimension( final String dimension, final boolean logging )
    {
        for ( final DimensionPool dimensionPool : this.dimensionPools.values() )
        {
            if ( dimensionPool.hasDimensions( dimension ) )
            {
                return Optional.of( dimensionPool );
            }
        }

        if ( logging )
        {
            DimensionalInventories.LOGGER.warn( "No dimension pool contains the dimension '{}'", dimension );
        }

        return Optional.empty();
    }

    public DimensionPoolOperationResult createPool( final String dimensionPoolId, final GameType gameMode )
    {
        final boolean exists = this.poolExists( dimensionPoolId );

        if ( !exists )
        {
            final DimensionPool dimensionPool = new DimensionPool( dimensionPoolId, gameMode );
            this.dimensionPools.put( dimensionPool.getId(), dimensionPool );
        }

        return new DimensionPoolOperationResult(
            DimensionPoolOperation.CREATE_POOL,
            exists ? DimensionPoolOperation.NO_OP : DimensionPoolOperation.CREATE_POOL,
            dimensionPoolId,
            null,
            null,
            !exists
        );
    }

    public DimensionPoolOperationResult deletePool( final String dimensionPoolId )
    {
        final boolean exists = this.poolExists( dimensionPoolId );
        this.dimensionPools.remove( dimensionPoolId );

        return new DimensionPoolOperationResult(
            DimensionPoolOperation.DELETE_POOL,
            DimensionPoolOperation.DELETE_POOL,
            dimensionPoolId,
            null,
            null,
            exists
        );
    }

    public DimensionPoolOperationResult assignDimensionToPool( final String dimension, final String dimensionPoolId )
    {
        if ( !this.poolExists( dimensionPoolId ) )
        {
            return new DimensionPoolOperationResult(
                DimensionPoolOperation.ADD_DIMENSION,
                DimensionPoolOperation.NO_OP,
                dimension,
                null,
                null,
                false
            );
        }

        final DimensionPool dimensionPool = this.dimensionPools.get( dimensionPoolId );
        return this.assignDimensionToPool( dimension, dimensionPool );
    }

    public DimensionPoolOperationResult assignDimensionToPool( final String dimension,
                                                               final DimensionPool dimensionPool )
    {
        final DimensionPoolOperation operation;
        final Optional<DimensionPool> currentDimensionPool = this.poolWithDimension( dimension );

        if ( currentDimensionPool.isEmpty() )
        {
            operation = DimensionPoolOperation.ADD_DIMENSION;
        }
        else if ( currentDimensionPool.get() == dimensionPool )
        {
            operation = DimensionPoolOperation.NO_OP;
        }
        else
        {
            operation = DimensionPoolOperation.MOVE_DIMENSION;
            currentDimensionPool.get().removeDimension( dimension );
        }

        dimensionPool.addDimension( dimension );

        return new DimensionPoolOperationResult(
            DimensionPoolOperation.ADD_DIMENSION,
            operation,
            dimension,
            (operation == DimensionPoolOperation.MOVE_DIMENSION) ? currentDimensionPool.get().getId() : null,
            dimensionPool.getId(),
            true
        );
    }

    public DimensionPoolOperationResult removeDimensionFromPool( final String dimension )
    {
        final Optional<DimensionPool> currentDimensionPool = this.poolWithDimension( dimension );

        if ( currentDimensionPool.isEmpty() )
        {
            return new DimensionPoolOperationResult(
                DimensionPoolOperation.REMOVE_DIMENSION,
                DimensionPoolOperation.NO_OP,
                dimension,
                null,
                null,
                false
            );
        }

        return this.removeDimensionFromPool( dimension, currentDimensionPool.get() );
    }

    public DimensionPoolOperationResult removeDimensionFromPool( final String dimension, final String dimensionPoolId )
    {
        if ( !this.poolExists( dimensionPoolId ) )
        {
            return new DimensionPoolOperationResult(
                DimensionPoolOperation.REMOVE_DIMENSION,
                DimensionPoolOperation.NO_OP,
                dimension,
                null,
                null,
                false
            );
        }

        final DimensionPool dimensionPool = this.dimensionPools.get( dimensionPoolId );
        return this.removeDimensionFromPool( dimension, dimensionPool );
    }

    public DimensionPoolOperationResult removeDimensionFromPool( final String dimension,
                                                                 final DimensionPool dimensionPool )
    {
        final DimensionPoolOperation operation;

        if ( dimensionPool.hasDimensions( dimension ) )
        {
            operation = DimensionPoolOperation.REMOVE_DIMENSION;
            dimensionPool.removeDimension( dimension );
        }
        else
        {
            operation = DimensionPoolOperation.NO_OP;
        }

        return new DimensionPoolOperationResult(
            DimensionPoolOperation.REMOVE_DIMENSION,
            operation,
            dimension,
            dimensionPool.getId(),
            null,
            true
        );
    }

    public boolean dimensionsAreInSamePool( final String... dimensions )
    {
        for ( final DimensionPool dimensionPool : this.dimensionPools.values() )
        {
            if ( dimensionPool.hasDimensions( dimensions ) )
            {
                return true;
            }
        }

        return false;
    }

    public String asString()
    {
        final StringBuilder builder = new StringBuilder();
        builder.append( "Dimension pools:" );

        for ( final DimensionPool dimensionPool : this.dimensionPools.values() )
        {
            builder.append( dimensionPool.asString() );
        }

        return builder.toString();
    }
}
