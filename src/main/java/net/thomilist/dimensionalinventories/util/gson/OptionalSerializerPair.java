package net.thomilist.dimensionalinventories.util.gson;

import com.google.gson.*;
import net.thomilist.dimensionalinventories.lostandfound.LostAndFound;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Optional;

// https://stackoverflow.com/a/12164531
public class OptionalSerializerPair<T>
    implements SerializerPair<Optional<T>>
{
    @Override
    public Optional<T> fromJson(JsonElement json, Type typeOfT, JsonDeserializationContext context)
        throws JsonParseException
    {
        if (!json.isJsonArray())
        {
            LostAndFound.log("Unexpected JSON structure for Optional<T> (expected an array)",
                json.toString());
            return Optional.empty();
        }

        final JsonArray asJsonArray = json.getAsJsonArray();
        final JsonElement jsonElement = asJsonArray.get(0);
        final T value = context.deserialize(jsonElement, ((ParameterizedType) typeOfT).getActualTypeArguments()[0]);
        return Optional.ofNullable(value);
    }

    @Override
    public JsonElement toJson(Optional<T> src, Type typeOfSrc, JsonSerializationContext context)
    {
        final JsonElement element = context.serialize(src.orElse(null));
        final JsonArray result = new JsonArray();
        result.add(element);
        return result;
    }
}
