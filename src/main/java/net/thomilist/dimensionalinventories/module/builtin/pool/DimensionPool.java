package net.thomilist.dimensionalinventories.module.builtin.pool;

import java.util.List;
import java.util.TreeSet;

import net.minecraft.world.GameMode;
import net.thomilist.dimensionalinventories.module.builtin.legacy.pool.DimensionPool_SV1;

public class DimensionPool
{
    private static final List<String> DEFAULT_DIMENSIONS = List.of(
        "minecraft:overworld",
        "minecraft:the_nether",
        "minecraft:the_end"
    );

    private static final String DEFAULT_DIMENSION_POOL_ID = "default";

    private String id;
    private String displayName;
    private final TreeSet<String> dimensions = new TreeSet<>();
    private GameMode gameMode = GameMode.DEFAULT;
    private boolean progressAdvancements = true;
    private boolean incrementStatistics = true;

    public static DimensionPool createDefault()
    {
        DimensionPool dimensionPool = new DimensionPool(DimensionPool.DEFAULT_DIMENSION_POOL_ID);

        for (String dimension : DimensionPool.DEFAULT_DIMENSIONS)
        {
            dimensionPool.addDimension(dimension);
        }

        return dimensionPool;
    }

    @SuppressWarnings("deprecation")
    public static DimensionPool fromLegacy(DimensionPool_SV1 legacyDimensionPool)
    {
        var newDimensionPool = new DimensionPool();

        newDimensionPool.setId(legacyDimensionPool.name());
        newDimensionPool.setDisplayName(legacyDimensionPool.name());
        newDimensionPool.setGameMode(legacyDimensionPool.gameMode());
        newDimensionPool.setProgressAdvancements(legacyDimensionPool.progressAdvancements());
        newDimensionPool.setIncrementStatistics(legacyDimensionPool.incrementStatistics());

        for (String dimension : legacyDimensionPool.dimensions())
        {
            newDimensionPool.addDimension(dimension);
        }

        return newDimensionPool;
    }

    protected DimensionPool()
    { }

    public DimensionPool(String id)
    {
        setId(id);
        setDisplayName(id);
    }

    public DimensionPool(String id, GameMode gameMode)
    {
        this(id);
        setGameMode(gameMode);
    }

    protected void setId(String id)
    {
        this.id = id;
    }

    public String getId()
    {
        return id;
    }

    public void setDisplayName(String displayName)
    {
        this.displayName = displayName;
    }

    public String getDisplayName()
    {
        return displayName;
    }

    public void addDimension(String dimension)
    {
        dimensions.add(dimension);
    }

    public void removeDimension(String dimension)
    {
        dimensions.remove(dimension);
    }

    public TreeSet<String> getDimensions()
    {
        return dimensions;
    }

    public boolean hasDimensions(String... dimensions)
    {
        for (String dimension : dimensions)
        {
            if (!this.dimensions.contains(dimension))
            {
                return false;
            }
        }

        return true;
    }

    public void setGameMode(GameMode gameMode)
    {
        this.gameMode = gameMode;
    }

    public GameMode getGameMode()
    {
        return gameMode;
    }

    public void setProgressAdvancements(boolean setting)
    {
        progressAdvancements = setting;
    }

    public boolean canProgressAdvancements()
    {
        return progressAdvancements;
    }

    public void setIncrementStatistics(boolean setting)
    {
        incrementStatistics = setting;
    }

    public boolean canIncrementStatistics()
    {
        return incrementStatistics;
    }

    public String asString()
    {
        StringBuilder dimensionPoolString = new StringBuilder();

        // Dimension pool header
        dimensionPoolString.append("\n[").append(getId()).append("]");

        // Rules
        dimensionPoolString.append("\n    Rules:");
        dimensionPoolString.append("\n        Gamemode: ").append(getGameMode().asString());
        dimensionPoolString.append("\n        Progress advancements: ").append(canProgressAdvancements());
        dimensionPoolString.append("\n        Increment statistics: ").append(canIncrementStatistics());

        // Dimensions
        dimensionPoolString.append("\n    Dimensions:");

        for (String dimension : getDimensions())
        {
            dimensionPoolString.append("\n        ").append(dimension);
        }

        return dimensionPoolString.toString();
    }
}
