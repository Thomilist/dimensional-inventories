package net.thomilist.dimensionalinventories.util;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.MinecraftServer;

public class NbtConversionHelper
{
    private static RegistryWrapper.WrapperLookup WRAPPER_LOOKUP;

    public static void onServerStarted(MinecraftServer server)
    {
        NbtConversionHelper.WRAPPER_LOOKUP = server.getRegistryManager();
    }

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
