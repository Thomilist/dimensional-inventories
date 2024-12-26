package net.thomilist.dimensionalinventories.module.builtin.legacy.pool;

import com.google.gson.*;
import net.thomilist.dimensionalinventories.lostandfound.LostAndFound;
import net.thomilist.dimensionalinventories.module.builtin.pool.DimensionPoolMapSerializerPair;
import net.thomilist.dimensionalinventories.util.gson.SerializerPair;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * @deprecated This is only intended to load data of storage version 1 during migration. When loading or saving new
 * data, use {@link DimensionPoolMapSerializerPair} instead.
 */
@Deprecated
public class DimensionPoolListSerializerPair_SV1
    implements SerializerPair<ArrayList<DimensionPool_SV1>>
{
    public static final Type TYPE = new DimensionPoolListSerializerPair_SV1().type();

    @Override
    public ArrayList<DimensionPool_SV1> fromJson( final JsonElement json,
                                                  final Type typeOfT,
                                                  final JsonDeserializationContext context )
        throws JsonParseException
    {
        if ( !json.isJsonArray() )
        {
            LostAndFound.log(
                "Unexpected JSON structure for list of legacy dimension pools (expected an array)",
                json.getAsString()
            );

            return new ArrayList<>();
        }

        final JsonArray jsonArray = json.getAsJsonArray();
        final ArrayList<DimensionPool_SV1> dimensionPools = new ArrayList<>();

        for ( final JsonElement jsonElement : jsonArray )
        {
            dimensionPools.add( context.deserialize( jsonElement, DimensionPool_SV1.class ) );
        }

        return dimensionPools;
    }

    @Override
    public JsonElement toJson( final ArrayList<DimensionPool_SV1> src,
                               final Type typeOfSrc,
                               final JsonSerializationContext context )
    {
        final JsonArray json = new JsonArray();

        for ( final DimensionPool_SV1 dimensionPool : src )
        {
            json.add( context.serialize( dimensionPool, DimensionPool_SV1.class ) );
        }

        return json;
    }
}
