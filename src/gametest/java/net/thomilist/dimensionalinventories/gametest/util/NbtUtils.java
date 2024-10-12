package net.thomilist.dimensionalinventories.gametest.util;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.thomilist.dimensionalinventories.DimensionalInventories;

import java.util.ArrayList;
import java.util.Objects;

public class NbtUtils
{
    private static final NbtCompound EMPTY_FROM_NEW = new NbtCompound();
    private static final NbtCompound EMPTY_FROM_CONVERSION;
    private static final String[] EMPTY_NBT_STRINGS =
    {
        "{}",
        "{data:[],palette:[]}",
        "{blocks:[],palette:[]}",
        "{\n    data: [],\n    palette: []\n}",
        "{\n    blocks: [],\n    palette: []\n}"
    };

    public static final NbtCompound[] EMPTY_NBT;

    static
    {
        try
        {
            EMPTY_FROM_CONVERSION = NbtHelper.fromNbtProviderString(NbtHelper.toNbtProviderString(new NbtCompound()));

            ArrayList<NbtCompound> emptyNbt = new ArrayList<>();

            emptyNbt.add(EMPTY_FROM_NEW);
            emptyNbt.add(EMPTY_FROM_CONVERSION);

            for (var nbtString : EMPTY_NBT_STRINGS)
            {
                emptyNbt.add(NbtHelper.fromNbtProviderString(nbtString));
            }

            EMPTY_NBT = emptyNbt.toArray(new NbtCompound[]{});
        }
        catch (CommandSyntaxException e)
        {
            DimensionalInventories.LOGGER.error("Nbt string", e);
            throw new IllegalStateException();
        }
    }

    public static boolean isEmpty(NbtCompound nbt)
    {
        if (nbt.isEmpty())
        {
            return true;
        }

        var nbtString = NbtHelper.toNbtProviderString(nbt);

        for (var emptyNbt : EMPTY_NBT)
        {
            if (nbt == emptyNbt)
            {
                return true;
            }

            if (Objects.equals(nbtString, NbtHelper.toNbtProviderString(emptyNbt)))
            {
                return true;
            }
        }

        return false;
    }
}
