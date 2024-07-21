package net.thomilist.dimensionalinventories.module.builtin.status;

import com.google.gson.*;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.thomilist.dimensionalinventories.lostandfound.LostAndFound;
import net.thomilist.dimensionalinventories.util.gson.SerializerPair;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;

public class StatusEffectCollectionSerializerPair
    implements SerializerPair<Collection<StatusEffectInstance>>
{
    public static final Type TYPE = new StatusEffectCollectionSerializerPair().type();

    @Override
    public Collection<StatusEffectInstance> fromJson(JsonElement json, Type typeOfT, JsonDeserializationContext context)
        throws JsonParseException
    {
        if (!json.isJsonArray())
        {
            LostAndFound.log("Unexpected JSON structure for collection of status effects (expected an array)",
                json.getAsString());
            return new ArrayList<>();
        }

        final ArrayList<StatusEffectInstance> effects = new ArrayList<>();
        final JsonArray jsonArray = json.getAsJsonArray();

        for (int i = 0; i < jsonArray.size(); i++)
        {
            StatusEffectInstance effect = context.deserialize(jsonArray.get(i), StatusEffectInstance.class);

            if (effect == null)
            {
                continue;
            }

            effects.add(effect);
        }

        return effects;
    }

    @Override
    public JsonElement toJson(Collection<StatusEffectInstance> src, Type typeOfSrc, JsonSerializationContext context)
    {
        final JsonArray json = new JsonArray();

        for (StatusEffectInstance statusEffect : src)
        {
            json.add(context.serialize(statusEffect, StatusEffectInstance.class));
        }

        return json;
    }
}
