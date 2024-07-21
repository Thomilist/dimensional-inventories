package net.thomilist.dimensionalinventories.module.builtin.pool;

import net.minecraft.world.GameMode;
import net.thomilist.dimensionalinventories.DimensionalInventoriesMod;
import net.thomilist.dimensionalinventories.module.builtin.legacy.pool.DimensionPoolConfigModuleState_SV1;
import net.thomilist.dimensionalinventories.module.base.config.ConfigModuleState;

import java.lang.reflect.Type;
import java.util.*;

public class DimensionPoolConfigModuleState
    implements ConfigModuleState
{
    @Override
    public Type type()
    {
        return DimensionPoolConfigModuleState.class;
    }

    public final HashMap<String, DimensionPool> dimensionPools = new HashMap<>();

    public static DimensionPoolConfigModuleState createDefault()
    {
        DimensionPoolConfigModuleState config = new DimensionPoolConfigModuleState();
        DimensionPool dimensionPool = DimensionPool.createDefault();
        config.dimensionPools.put(dimensionPool.getId(), dimensionPool);
        return config;
    }

    @SuppressWarnings("deprecation")
    public static DimensionPoolConfigModuleState fromLegacy(DimensionPoolConfigModuleState_SV1 legacyConfigData)
    {
        var newConfigData = new DimensionPoolConfigModuleState();

        for (var legacyPool : legacyConfigData.dimensionPools)
        {
            var newPool = DimensionPool.fromLegacy(legacyPool);
            newConfigData.dimensionPools.put(newPool.getId(), newPool);
        }

        return newConfigData;
    }

    public boolean poolExists(String dimensionPoolId)
    {
        return dimensionPools.containsKey(dimensionPoolId);
    }

    public Optional<DimensionPool> poolWithId(String dimensionPoolId)
    {
        return Optional.ofNullable(dimensionPools.get(dimensionPoolId));
    }

    public Optional<DimensionPool> poolWithDimension(String dimension)
    {
        return poolWithDimension(dimension, false);
    }

    public Optional<DimensionPool> poolWithDimension(String dimension, boolean logging)
    {
        for (DimensionPool dimensionPool : dimensionPools.values())
        {
            if (dimensionPool.hasDimensions(dimension))
            {
                return Optional.of(dimensionPool);
            }
        }

        if (logging)
        {
            DimensionalInventoriesMod.LOGGER.warn("No dimension pool contains the dimension '{}'", dimension);
        }

        return Optional.empty();
    }

    public DimensionPoolOperationResult createPool(String dimensionPoolId, GameMode gameMode)
    {
        boolean exists = poolExists(dimensionPoolId);

        if (!exists)
        {
            DimensionPool dimensionPool = new DimensionPool(dimensionPoolId, gameMode);
            dimensionPools.put(dimensionPool.getId(), dimensionPool);
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

    public DimensionPoolOperationResult deletePool(String dimensionPoolId)
    {
        boolean exists = poolExists(dimensionPoolId);
        dimensionPools.remove(dimensionPoolId);

        return new DimensionPoolOperationResult(
            DimensionPoolOperation.DELETE_POOL,
            DimensionPoolOperation.DELETE_POOL,
            dimensionPoolId,
            null,
            null,
            exists
        );
    }

    public DimensionPoolOperationResult assignDimensionToPool(String dimension, String dimensionPoolId)
    {
        if (!poolExists(dimensionPoolId))
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

        DimensionPool dimensionPool = dimensionPools.get(dimensionPoolId);
        return assignDimensionToPool(dimension, dimensionPool);
    }

    public DimensionPoolOperationResult assignDimensionToPool(String dimension, DimensionPool dimensionPool)
    {
        DimensionPoolOperation operation;
        Optional<DimensionPool> currentDimensionPool = poolWithDimension(dimension);

        if (currentDimensionPool.isEmpty())
        {
            operation = DimensionPoolOperation.ADD_DIMENSION;
        }
        else if (currentDimensionPool.get() == dimensionPool)
        {
            operation = DimensionPoolOperation.NO_OP;
        }
        else
        {
            operation = DimensionPoolOperation.MOVE_DIMENSION;
            currentDimensionPool.get().removeDimension(dimension);
        }

        dimensionPool.addDimension(dimension);

        return new DimensionPoolOperationResult(
            DimensionPoolOperation.ADD_DIMENSION,
            operation,
            dimension,
            operation == DimensionPoolOperation.MOVE_DIMENSION ? currentDimensionPool.get().getId() : null,
            dimensionPool.getId(),
            true
        );
    }

    public DimensionPoolOperationResult removeDimensionFromPool(String dimension)
    {
        Optional<DimensionPool> currentDimensionPool = poolWithDimension(dimension);

        if (currentDimensionPool.isEmpty())
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

        return removeDimensionFromPool(dimension, currentDimensionPool.get());
    }

    public DimensionPoolOperationResult removeDimensionFromPool(String dimension, String dimensionPoolId)
    {
        if (!poolExists(dimensionPoolId))
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

        DimensionPool dimensionPool = dimensionPools.get(dimensionPoolId);
        return removeDimensionFromPool(dimension, dimensionPool);
    }

    public DimensionPoolOperationResult removeDimensionFromPool(String dimension, DimensionPool dimensionPool)
    {
        DimensionPoolOperation operation;

        if (dimensionPool.hasDimensions(dimension))
        {
            operation = DimensionPoolOperation.REMOVE_DIMENSION;
            dimensionPool.removeDimension(dimension);
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

    public boolean dimensionsAreInSamePool(String... dimensions)
    {
        for (DimensionPool dimensionPool : dimensionPools.values())
        {
            if (dimensionPool.hasDimensions(dimensions))
            {
                return true;
            }
        }

        return false;
    }

    public String asString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("Dimension pools:");

        for (DimensionPool dimensionPool : dimensionPools.values())
        {
            builder.append(dimensionPool.asString());
        }

        return builder.toString();
    }
}
