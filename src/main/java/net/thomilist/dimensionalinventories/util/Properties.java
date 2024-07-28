package net.thomilist.dimensionalinventories.util;

import com.google.gson.stream.JsonReader;
import net.fabricmc.loader.api.FabricLoader;
import net.thomilist.dimensionalinventories.DimensionalInventories;
import net.thomilist.dimensionalinventories.exception.PropertyReadException;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

public class Properties
{
    private static final String MOD_ID = "dimensional-inventories";
    private static final String MOD_NAME_PRETTY = "Dimensional Inventories";
    private static final String MOD_NAME_PASCAL = "DimensionalInventories";
    private static String MOD_VERSION = "<unknown version>";

    static
    {
        readFabricModJson();
    }

    public static String modVersion()
    {
        return Properties.MOD_VERSION;
    }

    public static String modId()
    {
        return Properties.MOD_ID;
    }

    public static String modNamePretty()
    {
        return Properties.MOD_NAME_PRETTY;
    }

    public static String modNamePascal()
    {
        return Properties.MOD_NAME_PASCAL;
    }

    private static void readFabricModJson()
    {
        try
        {
            Optional<Path> fabricModJsonPath = FabricLoader
                .getInstance()
                .getModContainer(Properties.MOD_ID)
                .orElseThrow()
                .findPath("fabric.mod.json");

            if (fabricModJsonPath.isEmpty())
            {
                throw new PropertyReadException();
            }

            try (InputStream stream = Files.newInputStream(fabricModJsonPath.get()))
            {
                InputStreamReader reader = new InputStreamReader(stream);

                try (JsonReader jsonReader = new JsonReader(reader))
                {
                    jsonReader.beginObject();
                    String name;

                    while (jsonReader.hasNext())
                    {
                        name = jsonReader.nextName();

                        if (name.equals("version"))
                        {
                            Properties.MOD_VERSION = jsonReader.nextString();
                        }
                        else
                        {
                            jsonReader.skipValue();
                        }
                    }

                    jsonReader.endObject();
                }
            }
        }
        catch (Exception e)
        {
            DimensionalInventories.LOGGER.warn("Failed to read mod properties", e);
        }
    }
}
