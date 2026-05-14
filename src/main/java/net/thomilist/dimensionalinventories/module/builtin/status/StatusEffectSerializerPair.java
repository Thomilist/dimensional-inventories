package net.thomilist.dimensionalinventories.module.builtin.status;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffectInstance;
import net.thomilist.dimensionalinventories.compatibility.Compat;
import net.thomilist.dimensionalinventories.lostandfound.LostAndFound;
import net.thomilist.dimensionalinventories.util.gson.SerializerPair;

import java.lang.reflect.Type;

public class StatusEffectSerializerPair
    implements SerializerPair<MobEffectInstance>
{
    @Override
    public MobEffectInstance fromJson( final JsonElement json,
                                       final Type typeOfT,
                                       final JsonDeserializationContext context )
        throws JsonParseException
    {
        final CompoundTag nbt = context.deserialize( json, CompoundTag.class );

        if ( nbt == null )
        {
            return null;
        }

        final MobEffectInstance effect = Compat.NBT.toStatusEffectInstance( nbt );

        if ( effect == null )
        {
            LostAndFound.log( "Invalid NBT compound for status effect instance", nbt.toString() );
            return null;
        }

        return effect;
    }

    @Override
    public JsonElement toJson( final MobEffectInstance src,
                               final Type typeOfSrc,
                               final JsonSerializationContext context )
    {
        return context.serialize( Compat.NBT.fromStatusEffectInstance( src ), CompoundTag.class );
    }
}
