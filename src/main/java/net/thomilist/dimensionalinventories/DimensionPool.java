package net.thomilist.dimensionalinventories;

import java.util.ArrayList;

import net.minecraft.world.GameMode;

public class DimensionPool
{
    private String name;
    private ArrayList<String> dimensions = new ArrayList<String>();
    private GameMode gameMode;
    private boolean progressAdvancements = true;
    private boolean incrementStatistics = true;

    public DimensionPool(String poolName, GameMode poolGameMode)
    {
        name = poolName;
        gameMode = poolGameMode;
    }

    public void setName(String newName)
    {
        name = newName;
        return;
    }

    public String getName()
    {
        return name;
    }

    public void addDimension(String newDimension)
    {
        if (!dimensions.contains(newDimension))
        {
            dimensions.add(newDimension);
        }

        return;
    }

    public boolean removeDimension(String dimension)
    {
        return !dimensions.remove(dimension);
    }

    public ArrayList<String> getDimensions()
    {
        return dimensions;
    }

    public void setGameMode(GameMode newGameMode)
    {
        gameMode = newGameMode;
        return;
    }

    public GameMode getGameMode()
    {
        return gameMode;
    }

    public void setProgressAdvancements(boolean setting)
    {
        progressAdvancements = setting;
        return;
    }

    public boolean canProgressAdvancements()
    {
        return progressAdvancements;
    }

    public void setIncrementStatistics(boolean setting)
    {
        incrementStatistics = setting;
        return;
    }

    public boolean canIncrementStatistics()
    {
        return incrementStatistics;
    }

    public String asString()
    {
        StringBuilder dimensionPoolString = new StringBuilder();

        // Dimension pool header
        dimensionPoolString.append("\n[").append(getName()).append("]");

        // Rules
        dimensionPoolString.append("\n    Rules:");
        dimensionPoolString.append("\n        Gamemode: ").append(getGameMode().getName());
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
