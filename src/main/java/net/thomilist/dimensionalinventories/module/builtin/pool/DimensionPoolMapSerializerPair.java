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
    public HashMap<String, DimensionPool> fromJson( final JsonElement json,
                                                    final Type typeOfT,
                                                    final JsonDeserializationContext context )
        throws JsonParseException
    {
        if ( !json.isJsonArray() )
        {
            LostAndFound.log(
                "Unexpected JSON structure for dimension pool config (expected an array)",
                json.getAsString()
            );

            return new HashMap<>();
        }

        final HashMap<String, DimensionPool> dimensionPools = new HashMap<>();
        final JsonArray dimensionPoolsJson = json.getAsJsonArray();

        for ( final JsonElement entry : dimensionPoolsJson )
        {
            final DimensionPool dimensionPool = context.deserialize( entry, DimensionPool.class );
            dimensionPools.put( dimensionPool.getId(), dimensionPool );
        }

        return dimensionPools;
    }

    @Override
    public JsonElement toJson( final HashMap<String, DimensionPool> src,
                               final Type typeOfSrc,
                               final JsonSerializationContext context )
    {
        final JsonArray json = new JsonArray();

        for ( final Map.Entry<String, DimensionPool> entry : src.entrySet() )
        {
            json.add( context.serialize( entry.getValue(), DimensionPool.class ) );
        }

        return json;
    }
}
