package net.thomilist.dimensionalinventories.util;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.BuiltinRegistries;
import net.minecraft.registry.RegistryWrapper;

public class NbtConversionHelper
{
    private static final RegistryWrapper.WrapperLookup WRAPPER_LOOKUP = BuiltinRegistries.createWrapperLookup();

    public static ItemStack fromNbt(NbtCompound nbt)
    {
        if (nbt.isEmpty() || nbt.getString("id").matches("^minecraft:air$"))
        {
            return ItemStack.EMPTY;
        }

        return ItemStack.fromNbtOrEmpty(NbtConversionHelper.WRAPPER_LOOKUP, nbt);
    }

    public static NbtCompound toNbt(ItemStack itemStack)
    {
        if (itemStack.isEmpty())
        {
            return null;
        }

        return (NbtCompound) itemStack.encode(NbtConversionHelper.WRAPPER_LOOKUP);
    }
}
