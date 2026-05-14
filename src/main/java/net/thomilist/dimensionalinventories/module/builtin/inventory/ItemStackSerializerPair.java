package net.thomilist.dimensionalinventories.module.builtin.inventory;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.thomilist.dimensionalinventories.compatibility.Compat;
import net.thomilist.dimensionalinventories.lostandfound.LostAndFound;
import net.thomilist.dimensionalinventories.util.gson.SerializerPair;

import java.lang.reflect.Type;

public class ItemStackSerializerPair
    implements SerializerPair<ItemStack>
{
    @Override
    public ItemStack fromJson( final JsonElement json, final Type typeOfT, final JsonDeserializationContext context )
        throws JsonParseException
    {
        final CompoundTag nbt = context.deserialize( json, CompoundTag.class );

        if ( (nbt == null) || nbt.isEmpty() )
        {
            return null;
        }

        final ItemStack itemStack = Compat.NBT.toItemStack( nbt );

        if ( itemStack == null )
        {
            LostAndFound.log( "Invalid NBT compound for item stack", nbt.toString() );
            return null;
        }

        return itemStack;
    }

    @Override
    public JsonElement toJson( final ItemStack src, final Type typeOfSrc, final JsonSerializationContext context )
    {
        if ( src.isEmpty() )
        {
            return null;
        }

        return context.serialize( Compat.NBT.fromItemStack( src ), CompoundTag.class );
    }
}
