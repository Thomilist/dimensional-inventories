package net.thomilist.dimensionalinventories.module.builtin.inventory;

import com.google.gson.*;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.thomilist.dimensionalinventories.lostandfound.LostAndFound;
import net.thomilist.dimensionalinventories.util.gson.SerializerPair;

import java.lang.reflect.Type;
import java.util.Objects;

public class ItemStackListSerializerPair
    implements SerializerPair<NonNullList<ItemStack>>
{
    public static final Type TYPE = new ItemStackListSerializerPair().type();

    @Override
    public NonNullList<ItemStack> fromJson( final JsonElement json,
                                            final Type typeOfT,
                                            final JsonDeserializationContext context )
        throws JsonParseException
    {
        if ( !json.isJsonArray() )
        {
            LostAndFound.log(
                "Unexpected JSON structure for list of item stacks (expected an array)",
                json.getAsString()
            );

            return NonNullList.createWithCapacity( 0 );
        }

        final JsonArray jsonArray = json.getAsJsonArray();
        final NonNullList<ItemStack> items = NonNullList.withSize( jsonArray.size(), ItemStack.EMPTY );

        for ( int i = 0; i < jsonArray.size(); i++ )
        {
            items.set(
                i,
                Objects.requireNonNullElse(
                    context.deserialize( jsonArray.get( i ), ItemStack.class ),
                    ItemStack.EMPTY
                )
            );
        }

        return items;
    }

    @Override
    public JsonElement toJson( final NonNullList<ItemStack> src,
                               final Type typeOfSrc,
                               final JsonSerializationContext context )
    {
        final JsonArray json = new JsonArray();

        for ( final ItemStack itemStack : src )
        {
            json.add( context.serialize( itemStack, ItemStack.class ) );
        }

        return json;
    }
}
