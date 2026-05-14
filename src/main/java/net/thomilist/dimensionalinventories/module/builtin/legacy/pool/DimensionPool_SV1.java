package net.thomilist.dimensionalinventories.module.builtin.legacy.pool;

import net.minecraft.world.level.GameType;
import net.thomilist.dimensionalinventories.module.builtin.pool.DimensionPool;

import java.util.ArrayList;

/**
 * @deprecated This is only intended to hold data of storage version 1 when loading old data during migration. When
 * loading or saving new data, use {@link DimensionPool} instead.
 */
@Deprecated
public record DimensionPool_SV1(
    String name,
    ArrayList<String> dimensions,
    GameType gameMode,
    boolean progressAdvancements,
    boolean incrementStatistics
)
{ }
