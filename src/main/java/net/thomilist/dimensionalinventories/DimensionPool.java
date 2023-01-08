package net.thomilist.dimensionalinventories;

import java.util.ArrayList;

import net.minecraft.world.GameMode;

public class DimensionPool
{
    private String name;
    private ArrayList<String> dimensions = new ArrayList<String>();
    private GameMode gameMode;

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

    public String asString()
    {
        StringBuilder dimensionPoolString = new StringBuilder();

        dimensionPoolString.append("\n").append(getName());
        dimensionPoolString.append(" [").append(getGameMode().asString()).append("]");

        for (String dimension : getDimensions())
        {
            dimensionPoolString.append("\n    ").append(dimension);
        }

        return dimensionPoolString.toString();
    }
}
