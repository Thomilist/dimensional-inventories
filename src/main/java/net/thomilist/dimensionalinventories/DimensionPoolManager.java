package net.thomilist.dimensionalinventories;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

import org.apache.commons.lang3.ObjectUtils.Null;
import org.slf4j.Logger;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import net.minecraft.server.MinecraftServer;
import net.minecraft.util.WorldSavePath;

public class DimensionPoolManager
{
    public static Logger LOGGER;
    private static Path saveFile;
    private static ArrayList<DimensionPool> pools = new ArrayList<DimensionPool>();
    private static Type listType = new TypeToken<ArrayList<DimensionPool>>(){}.getType();
    
    public DimensionPoolManager()
    { }
    
    public static void onServerStart(MinecraftServer server, Logger logger)
    {
        DimensionPoolManager.LOGGER = logger;

        Path saveDirectory = server.getSavePath(WorldSavePath.ROOT).resolve("dimensionalinventories");
        try
        {
            Files.createDirectories(saveDirectory);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        DimensionPoolManager.saveFile = saveDirectory.resolve("dimension-pools.json");
        loadFromFile();
        //test();

        return;
    }

    public static void test()
    {
        createPool("test");
        addDimensionToPool("test", "minecraft:the_end");
        return;
    }

    public static void createDefaultPool()
    {
        String[] defaultDimensions = {
            "minecraft:overworld",
            "minecraft:the_nether",
            "minecraft:the_end"
        };

        createPool("default");

        for (String dimension : defaultDimensions)
        {
            pools.get(0).addDimension(dimension);
        }

        return;
    }

    public static boolean createPool(String poolName)
    {
        if (poolExists(poolName))
        {
            return true;
        }
        
        DimensionPool pool = new DimensionPool(poolName);
        pools.add(pool);

        return false;
    }

    public static boolean removePool(String poolName)
    {
        if (!poolExists(poolName))
        {
            return true;
        }

        for (int index = 0; index < pools.size(); index++)
        {
            if (pools.get(index).getName().equals(poolName))
            {
                pools.remove(index);
                break;
            }
        }

        return false;
    }

    public static void addDimensionToPool(String pool, String dimension)
    {
        // Find the pool
        int poolIndex;
        for (poolIndex = 0; poolIndex < pools.size(); poolIndex++)
        {
            if (pools.get(poolIndex).getName().equals(pool))
            {
                break;
            }
        }

        if (poolIndex >= pools.size())
        {
            LOGGER.error("Pool " + pool + " not found.");
            return;
        }

        // Exit early if the dimension is already in the pool
        if (pools.get(poolIndex).getDimensions().contains(dimension))
        {
            LOGGER.info("Dimension " + dimension + " is already in pool " + pool + ".");
            return;
        }

        // Remove the dimension from all pools
        for (int index = 0; index < pools.size(); index++)
        {
            pools.get(index).removeDimension(dimension);
        }

        // Insert the dimension into the correct pool
        pools.get(poolIndex).addDimension(dimension);
        return;
    }

    public static void saveToFile()
    {
        Gson gson = new Gson();
        String dimensionPoolsJson = gson.toJson(pools);
        
        try
        {
            Files.writeString(saveFile, dimensionPoolsJson);
        }
        catch (IOException e)
        {
            e.printStackTrace();
            LOGGER.error("Unable to write to dimension pool file.", e);
            return;
        }

        return;
    }

    public static void loadFromFile()
    {
        String dimensionPoolsJson;
        
        try
        {
            dimensionPoolsJson = Files.readString(saveFile);
        }
        catch (IOException e)
        {
            createDefaultPool();
            saveToFile();
            return;
        }
        
        Gson gson = new Gson();
        
        try
        {
            pools = gson.fromJson(dimensionPoolsJson, listType);
        }
        catch (JsonSyntaxException e)
        {
            e.printStackTrace();
            LOGGER.error("Invalid JSON syntax.", e);
            return;
        }

        return;
    }

    public static boolean samePoolContainsBoth(String dimensionA, String dimensionB)
    {
        for (DimensionPool pool : pools)
        {
            if (pool.getDimensions().contains(dimensionA) && pool.getDimensions().contains(dimensionB))
            {
                return true;
            }
        }

        return false;
    }

    public static ArrayList<DimensionPool> getPools()
    {
        return pools;
    }

    public static String getPoolsAsString()
    {
        StringBuilder poolsString = new StringBuilder();

        poolsString.append("Dimension pools:");

        for (DimensionPool pool : getPools())
        {
            poolsString.append(pool.asString());
        }

        return poolsString.toString();
    }

    public static DimensionPool getPoolWithName(String poolName) throws NullPointerException
    {
        for (DimensionPool pool : getPools())
        {
            if (pool.getName().equals(poolName))
            {
                return pool;
            }
        }

        throw new NullPointerException("No pool named '" + poolName + "'.");
    }

    public static boolean poolExists(String poolName)
    {
        for (DimensionPool pool : getPools())
        {
            if (pool.getName().equals(poolName))
            {
                return true;
            }
        }

        return false;
    }
}
