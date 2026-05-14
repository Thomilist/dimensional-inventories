package net.thomilist.dimensionalinventories.module.builtin.status;

import com.google.gson.*;
import net.minecraft.world.effect.MobEffectInstance;
import net.thomilist.dimensionalinventories.lostandfound.LostAndFound;
import net.thomilist.dimensionalinventories.util.gson.SerializerPair;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;

public class StatusEffectCollectionSerializerPair
    implements SerializerPair<Collection<MobEffectInstance>>
{
    public static final Type TYPE = new StatusEffectCollectionSerializerPair().type();

    @Override
    public Collection<MobEffectInstance> fromJson( final JsonElement json,
                                                   final Type typeOfT,
                                                   final JsonDeserializationContext context )
        throws JsonParseException
    {
        if ( !json.isJsonArray() )
        {
            LostAndFound.log(
                "Unexpected JSON structure for collection of status effects (expected an array)",
                json.getAsString()
            );

            return new ArrayList<>();
        }

        final ArrayList<MobEffectInstance> effects = new ArrayList<>();
        final JsonArray jsonArray = json.getAsJsonArray();

        for ( int i = 0; i < jsonArray.size(); i++ )
        {
            final MobEffectInstance effect = context.deserialize( jsonArray.get( i ), MobEffectInstance.class );

            if ( effect == null )
            {
                continue;
            }

            effects.add( effect );
        }

        return effects;
    }

    @Override
    public JsonElement toJson( final Collection<MobEffectInstance> src,
                               final Type typeOfSrc,
                               final JsonSerializationContext context )
    {
        final JsonArray json = new JsonArray();

        for ( final MobEffectInstance statusEffect : src )
        {
            json.add( context.serialize( statusEffect, MobEffectInstance.class ) );
        }

        return json;
    }
}
