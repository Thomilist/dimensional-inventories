package net.thomilist.dimensionalinventories.module.builtin.legacy.pool;

import net.minecraft.world.GameMode;

import java.util.ArrayList;

@Deprecated
public record DimensionPool_SV1(
    String name,
    ArrayList<String> dimensions,
    GameMode gameMode,
    boolean progressAdvancements,
    boolean incrementStatistics
)
{ }
