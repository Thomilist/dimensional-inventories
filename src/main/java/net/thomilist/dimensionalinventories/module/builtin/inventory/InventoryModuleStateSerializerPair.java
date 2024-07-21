package net.thomilist.dimensionalinventories.module.builtin.inventory;

import com.google.gson.*;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import net.thomilist.dimensionalinventories.lostandfound.LostAndFound;
import net.thomilist.dimensionalinventories.util.ItemStackListHelper;
import net.thomilist.dimensionalinventories.util.gson.SerializerPair;

import java.lang.reflect.Type;

public class InventoryModuleStateSerializerPair
    implements SerializerPair<InventoryModuleState>
{
    @Override
    public InventoryModuleState fromJson(JsonElement json, Type typeOfT, JsonDeserializationContext context)
        throws JsonParseException
    {
        if (!json.isJsonObject())
        {
            LostAndFound.log("Unexpected JSON structure for inventory data (expected an object)",
                json.toString());
            return new InventoryModuleState();
        }

        final InventoryModuleState inventoryModuleState = new InventoryModuleState();
        final JsonObject inventoryJson = json.getAsJsonObject();

        for (InventorySection label : InventorySection.list())
        {
            try (var LAF = LostAndFound.push(label))
            {
                JsonElement stacksJson = inventoryJson.get(label.toString());
                DefaultedList<ItemStack> items = context.deserialize(stacksJson, ItemStackListSerializerPair.TYPE);

                if (!items.isEmpty())
                {
                    ItemStackListHelper.assignItemStacks(items, inventoryModuleState.section(label));
                }
            }
        }

        return inventoryModuleState;
    }

    @Override
    public JsonElement toJson(InventoryModuleState src, Type typeOfSrc, JsonSerializationContext context)
    {
        final JsonObject json = new JsonObject();

        for (InventorySection label : InventorySection.list())
        {
            try (var LAF = LostAndFound.push(label))
            {
                json.add(label.toString(), context.serialize(src.section(label), ItemStackListSerializerPair.TYPE));
            }
        }

        return json;
    }
}
