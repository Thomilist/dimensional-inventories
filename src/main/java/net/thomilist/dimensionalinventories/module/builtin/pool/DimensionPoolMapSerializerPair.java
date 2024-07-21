package net.thomilist.dimensionalinventories.module.builtin.pool;

import com.google.gson.*;
import net.thomilist.dimensionalinventories.lostandfound.LostAndFound;
import net.thomilist.dimensionalinventories.util.gson.SerializerPair;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class DimensionPoolMapSerializerPair
    implements SerializerPair<HashMap<String, DimensionPool>>
{
    public static final Type TYPE = new DimensionPoolMapSerializerPair().type();

    @Override
    public HashMap<String, DimensionPool> fromJson(JsonElement json, Type typeOfT, JsonDeserializationContext context)
        throws JsonParseException
    {
        if (!json.isJsonArray())
        {
            LostAndFound.log("Unexpected JSON structure for dimension pool config (expected an array)",
                json.getAsString());
            return new HashMap<>();
        }

        var dimensionPools = new HashMap<String, DimensionPool>();
        var dimensionPoolsJson = json.getAsJsonArray();

        for (var entry : dimensionPoolsJson)
        {
            DimensionPool dimensionPool = context.deserialize(entry, DimensionPool.class);
            dimensionPools.put(dimensionPool.getId(), dimensionPool);
        }

        return dimensionPools;
    }

    @Override
    public JsonElement toJson(HashMap<String, DimensionPool> src, Type typeOfSrc, JsonSerializationContext context)
    {
        var json = new JsonArray();

        for (Map.Entry<String, DimensionPool> entry : src.entrySet())
        {
            json.add(context.serialize(entry.getValue(), DimensionPool.class));
        }

        return json;
    }
}
