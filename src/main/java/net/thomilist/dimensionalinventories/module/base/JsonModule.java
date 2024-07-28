package net.thomilist.dimensionalinventories.module.base;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import net.minecraft.nbt.NbtCompound;
import net.thomilist.dimensionalinventories.DimensionalInventories;
import net.thomilist.dimensionalinventories.lostandfound.LostAndFound;
import net.thomilist.dimensionalinventories.module.version.VersionedJsonData;
import net.thomilist.dimensionalinventories.module.version.VersionedJsonDataSerializerPair;
import net.thomilist.dimensionalinventories.util.gson.NbtCompoundSerializerPair;
import net.thomilist.dimensionalinventories.util.gson.OptionalSerializerPair;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.Optional;

public interface JsonModule<T extends ModuleState>
    extends StatefulModule<T>
{
    GsonBuilder GSON_BUILDER = new GsonBuilder()
        .registerTypeAdapter(VersionedJsonData.class, new VersionedJsonDataSerializerPair())
        .registerTypeAdapter(Optional.class, new OptionalSerializerPair<>())
        .registerTypeAdapter(NbtCompound.class, new NbtCompoundSerializerPair())
        .setPrettyPrinting();

    Gson gson();

    default String noSuchFileWarning()
    {
        return "No data found (default data loaded instead)";
    }

    default String saveFileName()
    {
        return moduleId() + ".json";
    }

    default T load(Path saveFile)
    {
        String json;

        try
        {
            json = Files.readString(saveFile);
        }
        catch (NoSuchFileException e)
        {
            DimensionalInventories.LOGGER.warn(noSuchFileWarning());
            DimensionalInventories.LOGGER.warn("Context: {}", LostAndFound.CONTEXT);
            return defaultState();
        }
        catch (IOException e)
        {
            LostAndFound.log("Failed to load data", saveFile.toString(), e);
            return state();
        }

        T data;

        try
        {
            data = loadFromJsonString(json);
        }
        catch (JsonParseException e)
        {
            LostAndFound.log("Failed to parse JSON data", json, e);
            return state();
        }

        return data;
    }

    default T loadFromJsonString(String json) throws JsonParseException
    {
        var versionedData = gson().fromJson(json, VersionedJsonData.class);
        return loadVersionedData(versionedData);
    }

    default T loadVersionedData(VersionedJsonData versionedData) throws JsonParseException
    {
        return loadAsCurrentVersion(versionedData.data());
    }

    default T loadAsCurrentVersion(JsonElement data) throws JsonParseException
    {
        return gson().fromJson(data, state().type());
    }

    default void save(Path saveFile, T data)
    {
        final JsonElement dataJson = gson().toJsonTree(data);
        final var versionedData = new VersionedJsonData(moduleVersion(), dataJson);
        final String json = gson().toJson(versionedData);

        try
        {
            Files.createDirectories(saveFile.getParent());
            Files.writeString(saveFile, json);
        }
        catch (IOException e)
        {
            LostAndFound.log("Unable to save data", json, e);
        }
    }
}
