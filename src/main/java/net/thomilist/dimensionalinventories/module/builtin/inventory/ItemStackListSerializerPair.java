package net.thomilist.dimensionalinventories.module.builtin.inventory;

import com.google.gson.*;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import net.thomilist.dimensionalinventories.lostandfound.LostAndFound;
import net.thomilist.dimensionalinventories.util.gson.SerializerPair;

import java.lang.reflect.Type;
import java.util.Objects;

public class ItemStackListSerializerPair
    implements SerializerPair<DefaultedList<ItemStack>>
{
    public static final Type TYPE = new ItemStackListSerializerPair().type();

    @Override
    public DefaultedList<ItemStack> fromJson(JsonElement json, Type typeOfT, JsonDeserializationContext context)
        throws JsonParseException
    {
        if (!json.isJsonArray())
        {
            LostAndFound.log("Unexpected JSON structure for list of item stacks (expected an array)",
                json.getAsString());
            return DefaultedList.ofSize(0);
        }

        final JsonArray jsonArray = json.getAsJsonArray();
        final DefaultedList<ItemStack> items = DefaultedList.ofSize(jsonArray.size(), ItemStack.EMPTY);

        for (int i = 0; i < jsonArray.size(); i++)
        {
            items.set(i, Objects.requireNonNullElse(
                context.deserialize(jsonArray.get(i), ItemStack.class),
                ItemStack.EMPTY
            ));
        }

        return items;
    }

    @Override
    public JsonElement toJson(DefaultedList<ItemStack> src, Type typeOfSrc, JsonSerializationContext context)
    {
        final JsonArray json = new JsonArray();

        for (ItemStack itemStack : src)
        {
            json.add(context.serialize(itemStack, ItemStack.class));
        }

        return json;
    }
}
