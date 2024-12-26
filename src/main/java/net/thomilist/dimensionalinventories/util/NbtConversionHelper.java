package net.thomilist.dimensionalinventories.util;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.MinecraftServer;

public final class NbtConversionHelper
{
    private static RegistryWrapper.WrapperLookup WRAPPER_LOOKUP;

    private NbtConversionHelper()
    { }

    public static void onServerStarted( final MinecraftServer server )
    {
        NbtConversionHelper.WRAPPER_LOOKUP = server.getRegistryManager();
    }

    public static ItemStack fromNbt( final NbtCompound nbt )
    {
        if ( nbt.isEmpty() || nbt.getString( "id" ).matches( "^minecraft:air$" ) )
        {
            return ItemStack.EMPTY;
        }

        return ItemStack.fromNbtOrEmpty( NbtConversionHelper.WRAPPER_LOOKUP, nbt );
    }

    public static NbtCompound toNbt( final ItemStack itemStack )
    {
        if ( itemStack.isEmpty() )
        {
            return null;
        }

        return (NbtCompound) itemStack.toNbt( NbtConversionHelper.WRAPPER_LOOKUP );
    }
}
