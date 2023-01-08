package net.thomilist.dimensionalinventories;

import java.util.ArrayList;

public class DimensionPool
{
    private String name;
    private ArrayList<String> dimensions = new ArrayList<String>();

    public DimensionPool(String poolName)
    {
        name = poolName;
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

    public String asString()
    {
        StringBuilder dimensionPoolString = new StringBuilder();

        dimensionPoolString.append("\n").append(getName());

        for (String dimension : getDimensions())
        {
            dimensionPoolString.append("\n    ").append(dimension);
        }

        return dimensionPoolString.toString();
    }
}
