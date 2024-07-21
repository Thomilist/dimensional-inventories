package net.thomilist.dimensionalinventories.module.version;

import com.google.gson.*;
import net.thomilist.dimensionalinventories.lostandfound.LostAndFound;
import net.thomilist.dimensionalinventories.util.gson.SerializerPair;

import java.lang.reflect.Type;

public class VersionedJsonDataSerializerPair
    implements SerializerPair<VersionedJsonData>
{
    private static final String VERSION = "version";
    private static final String DATA = "data";

    @Override
    public VersionedJsonData fromJson(JsonElement json, Type typeOfT, JsonDeserializationContext context)
        throws JsonParseException
    {
        if (!json.isJsonObject())
        {
            if (!json.isJsonObject())
            {
                LostAndFound.log("Unexpected JSON structure for versioned data (expected an object)",
                    json.toString());
                return null;
            }
        }

        var jsonObject = json.getAsJsonObject();

        if (!jsonObject.has(VersionedJsonDataSerializerPair.VERSION))
        {
            VersionedJsonDataSerializerPair.logMissingField(VersionedJsonDataSerializerPair.VERSION, json);
            return null;
        }

        var versionJson = jsonObject.get(VersionedJsonDataSerializerPair.VERSION);

        if (!versionJson.isJsonPrimitive())
        {
            LostAndFound.log("Unexpected JSON structure for field '" + VersionedJsonDataSerializerPair.VERSION + "' (expected an integer)",
                json.toString());
            return null;
        }

        int version = versionJson.getAsInt();

        if (!jsonObject.has(VersionedJsonDataSerializerPair.DATA))
        {
            VersionedJsonDataSerializerPair.logMissingField(VersionedJsonDataSerializerPair.DATA, json);
            return null;
        }

        var dataJson = jsonObject.get(VersionedJsonDataSerializerPair.DATA);

        return new VersionedJsonData(version, dataJson);
    }

    @Override
    public JsonElement toJson(VersionedJsonData src, Type typeOfSrc, JsonSerializationContext context)
    {
        var json = new JsonObject();

        json.add(VersionedJsonDataSerializerPair.VERSION, context.serialize(src.version()));
        json.add(VersionedJsonDataSerializerPair.DATA, context.serialize(src.data()));

        return json;
    }

    private static void logMissingField(String field, JsonElement json)
    {
        LostAndFound.log("Missing field '" + field + "' in versioned data JSON", json.toString());
    }
}
