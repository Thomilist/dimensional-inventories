package net.thomilist.dimensionalinventories.module.builtin.inventory;

import com.google.gson.*;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import net.thomilist.dimensionalinventories.lostandfound.LostAndFound;
import net.thomilist.dimensionalinventories.lostandfound.LostAndFoundScope;
import net.thomilist.dimensionalinventories.util.ItemStackListHelper;
import net.thomilist.dimensionalinventories.util.gson.SerializerPair;

import java.lang.reflect.Type;

public class InventoryModuleStateSerializerPair
    implements SerializerPair<InventoryModuleState>
{
    @Override
    public InventoryModuleState fromJson( final JsonElement json,
                                          final Type typeOfT,
                                          final JsonDeserializationContext context )
        throws JsonParseException
    {
        if ( !json.isJsonObject() )
        {
            LostAndFound.log( "Unexpected JSON structure for inventory data (expected an object)", json.toString() );

            return new InventoryModuleState();
        }

        final InventoryModuleState inventoryModuleState = new InventoryModuleState();
        final JsonObject inventoryJson = json.getAsJsonObject();

        for ( final InventorySection label : InventorySection.list() )
        {
            try ( final LostAndFoundScope LAF = LostAndFound.push( label ) )
            {
                final JsonElement stacksJson = inventoryJson.get( label.toString() );
                final DefaultedList<ItemStack> items = context.deserialize(
                    stacksJson,
                    ItemStackListSerializerPair.TYPE
                );

                if ( !items.isEmpty() )
                {
                    ItemStackListHelper.assignItemStacks( items, inventoryModuleState.section( label ) );
                }
            }
        }

        return inventoryModuleState;
    }

    @Override
    public JsonElement toJson( final InventoryModuleState src,
                               final Type typeOfSrc,
                               final JsonSerializationContext context )
    {
        final JsonObject json = new JsonObject();

        for ( final InventorySection label : InventorySection.list() )
        {
            try ( final LostAndFoundScope LAF = LostAndFound.push( label ) )
            {
                json.add(
                    label.toString(),
                    context.serialize( src.section( label ), ItemStackListSerializerPair.TYPE )
                );
            }
        }

        return json;
    }
}
