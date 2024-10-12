package net.thomilist.dimensionalinventories.util;

import com.google.gson.stream.JsonReader;
import net.fabricmc.loader.api.FabricLoader;
import net.thomilist.dimensionalinventories.DimensionalInventories;
import net.thomilist.dimensionalinventories.exception.PropertyReadException;
import org.slf4j.Logger;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ModProperties
{
    private final String id;
    private String namePretty = "<unknown mod name>";
    private String namePascal = "<unknown mod name>";
    private String version = "<unknown mod version>";
    private String description = "<unknown mod description>";
    private final List<String> authors = new ArrayList<>();

    public ModProperties(String modId, Logger logger)
    {
        this.id = modId;

        try
        {
            Optional<Path> fabricModJsonPath = FabricLoader
                .getInstance()
                .getModContainer(this.id)
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
                            this.version = jsonReader.nextString();
                        }
                        else if (name.equals("name"))
                        {
                            this.namePretty = jsonReader.nextString();
                            this.namePascal = StringHelper.toPascalCase(this.namePretty);
                        }
                        else if (name.equals("description"))
                        {
                            this.description = jsonReader.nextString();
                        }
                        else if (name.equals("authors"))
                        {
                            jsonReader.beginArray();

                            while (jsonReader.hasNext())
                            {
                                this.authors.add(jsonReader.nextString());
                            }

                            jsonReader.endArray();
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
            logger.warn("Failed to read mod properties", e);
        }
    }

    public ModProperties(String modId)
    {
        this(modId, DimensionalInventories.LOGGER);
    }

    public String version()
    {
        return this.version;
    }

    public String id()
    {
        return this.id;
    }

    public String namePretty()
    {
        return this.namePretty;
    }

    public String namePascal()
    {
        return this.namePascal;
    }

    public String description()
    {
        return this.description;
    }

    public List<String> authors()
    {
        return this.authors;
    }

    public String authorsPretty()
    {
        if (this.authors.isEmpty())
        {
            return "<no authors found>";
        }
        else
        {
            return StringHelper.joinLastDifferent(
                ", ",
                " & ",
                this.authors
            );
        }
    }
}
