package net.thomilist.dimensionalinventories.module.builtin.status;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.nbt.NbtCompound;
import net.thomilist.dimensionalinventories.lostandfound.LostAndFound;
import net.thomilist.dimensionalinventories.util.gson.SerializerPair;

import java.lang.reflect.Type;

public class StatusEffectSerializerPair
    implements SerializerPair<StatusEffectInstance>
{
    @Override
    public StatusEffectInstance fromJson(JsonElement json, Type typeOfT, JsonDeserializationContext context)
        throws JsonParseException
    {
        NbtCompound nbt = context.deserialize(json, NbtCompound.class);

        if (nbt == null)
        {
            return null;
        }

        StatusEffectInstance effect = StatusEffectInstance.fromNbt(nbt);

        if (effect == null)
        {
            LostAndFound.log("Invalid NBT compound for status effect instance", nbt.toString());
            return null;
        }

        return effect;
    }

    @Override
    public JsonElement toJson(StatusEffectInstance src, Type typeOfSrc, JsonSerializationContext context)
    {
        return context.serialize(src.writeNbt(new NbtCompound()), NbtCompound.class);
    }
}
