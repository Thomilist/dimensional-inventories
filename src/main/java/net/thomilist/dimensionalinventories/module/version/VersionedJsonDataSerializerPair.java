package net.thomilist.dimensionalinventories.module.version;

import com.google.gson.*;
import net.thomilist.dimensionalinventories.lostandfound.LostAndFound;
import net.thomilist.dimensionalinventories.util.gson.SerializerPair;

import java.lang.reflect.Type;

public class VersionedJsonDataSerializerPair
    implements SerializerPair<VersionedJsonData>
{
    private static final String VERSION_FIELD = "version";
    private static final String DATA_FIELD = "data";

    private static void logMissingField( final String field, final JsonElement json )
    {
        LostAndFound.log( "Missing field '%s' in versioned data JSON".formatted( field ), json.toString() );
    }

    @Override
    public VersionedJsonData fromJson( final JsonElement json,
                                       final Type typeOfT,
                                       final JsonDeserializationContext context )
        throws JsonParseException
    {
        if ( !json.isJsonObject() )
        {
            if ( !json.isJsonObject() )
            {
                LostAndFound.log(
                    "Unexpected JSON structure for versioned data (expected an object)",
                    json.toString()
                );

                return null;
            }
        }

        final JsonObject jsonObject = json.getAsJsonObject();

        if ( !jsonObject.has( VersionedJsonDataSerializerPair.VERSION_FIELD ) )
        {
            VersionedJsonDataSerializerPair.logMissingField( VersionedJsonDataSerializerPair.VERSION_FIELD, json );

            return null;
        }

        final JsonElement versionJson = jsonObject.get( VersionedJsonDataSerializerPair.VERSION_FIELD );

        if ( !versionJson.isJsonPrimitive() )
        {
            LostAndFound.log(
                "Unexpected JSON structure for field '%s' (expected an integer)".formatted(
                    VersionedJsonDataSerializerPair.VERSION_FIELD ), json.toString()
            );

            return null;
        }

        final int version = versionJson.getAsInt();

        if ( !jsonObject.has( VersionedJsonDataSerializerPair.DATA_FIELD ) )
        {
            VersionedJsonDataSerializerPair.logMissingField( VersionedJsonDataSerializerPair.DATA_FIELD, json );

            return null;
        }

        final JsonElement dataJson = jsonObject.get( VersionedJsonDataSerializerPair.DATA_FIELD );

        return new VersionedJsonData( version, dataJson );
    }

    @Override
    public JsonElement toJson( final VersionedJsonData src,
                               final Type typeOfSrc,
                               final JsonSerializationContext context )
    {
        final JsonObject json = new JsonObject();

        json.add( VersionedJsonDataSerializerPair.VERSION_FIELD, context.serialize( src.version() ) );

        json.add( VersionedJsonDataSerializerPair.DATA_FIELD, context.serialize( src.data() ) );

        return json;
    }
}
