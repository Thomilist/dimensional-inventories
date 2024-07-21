package net.thomilist.dimensionalinventories.util.gson;

import com.google.gson.*;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.thomilist.dimensionalinventories.lostandfound.LostAndFound;

import java.lang.reflect.Type;

public class NbtCompoundSerializerPair
    implements SerializerPair<NbtCompound>
{
    @Override
    public NbtCompound fromJson(JsonElement json, Type typeOfT, JsonDeserializationContext context)
        throws JsonParseException
    {
        if (!json.isJsonPrimitive())
        {
            LostAndFound.log("Unexpected JSON structure for NBT compound (expected a string)",
                json.toString());
            return null;
        }

        final String nbtString = json.getAsJsonPrimitive().getAsString();

        try
        {
            return NbtHelper.fromNbtProviderString(nbtString);
        }
        catch (CommandSyntaxException e)
        {
            LostAndFound.log("Invalid NBT string", nbtString, e);
            return null;
        }
    }

    @Override
    public JsonElement toJson(NbtCompound src, Type typeOfSrc, JsonSerializationContext context)
    {
        if (src == null)
        {
            return null;
        }

        return new JsonPrimitive(NbtHelper.toNbtProviderString(src));
    }
}
