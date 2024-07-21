package net.thomilist.dimensionalinventories.util.gson;

import com.google.common.reflect.TypeToken;
import com.google.gson.*;
import net.thomilist.dimensionalinventories.lostandfound.LostAndFound;

import java.lang.reflect.Type;

public interface SerializerPair<T>
    extends JsonSerializer<T>, JsonDeserializer<T>
{
    default Type type()
    {
        return new TypeToken<T>(getClass()){}.getType();
    }

    @Override
    default T deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
    {
        try (var LAF = LostAndFound.push("deserialize (" + typeOfT.getTypeName() + ")"))
        {
            try
            {
                return fromJson(json, typeOfT, context);
            }
            catch (JsonParseException e)
            {
                LostAndFound.log("Failed to parse JSON data", json.toString(), e);
                return null;
            }
        }
    }

    @Override
    default JsonElement serialize(T src, Type typeOfSrc, JsonSerializationContext context)
    {
        try (var LAF = LostAndFound.push("serialize (" + typeOfSrc.getTypeName() + ")"))
        {
            return toJson(src, typeOfSrc, context);
        }
    }

    T fromJson(JsonElement json, Type typeOfT, JsonDeserializationContext context)
        throws JsonParseException;

    JsonElement toJson(T src, Type typeOfSrc, JsonSerializationContext context);
}
