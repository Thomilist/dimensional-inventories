package net.thomilist.dimensionalinventories.util.gson;

import com.google.common.reflect.TypeToken;
import com.google.gson.*;
import net.thomilist.dimensionalinventories.lostandfound.LostAndFound;
import net.thomilist.dimensionalinventories.lostandfound.LostAndFoundScope;

import java.lang.reflect.Type;

public interface SerializerPair<T>
    extends JsonSerializer<T>, JsonDeserializer<T>
{
    T fromJson( JsonElement json, Type typeOfT, JsonDeserializationContext context )
        throws JsonParseException;

    JsonElement toJson( T src, Type typeOfSrc, JsonSerializationContext context );

    default Type type()
    {
        return new TypeToken<T>( this.getClass() ) { }.getType();
    }

    @Override
    default T deserialize( final JsonElement json, final Type typeOfT, final JsonDeserializationContext context )
    {
        try ( final LostAndFoundScope LAF = LostAndFound.push( "deserialize (" + typeOfT.getTypeName() + ')' ) )
        {
            try
            {
                return this.fromJson( json, typeOfT, context );
            }
            catch ( final JsonParseException e )
            {
                LostAndFound.log( "Failed to parse JSON data", json.toString(), e );
                return null;
            }
        }
    }

    @Override
    default JsonElement serialize( final T src, final Type typeOfSrc, final JsonSerializationContext context )
    {
        try ( final LostAndFoundScope LAF = LostAndFound.push( "serialize (" + typeOfSrc.getTypeName() + ')' ) )
        {
            return this.toJson( src, typeOfSrc, context );
        }
    }
}
