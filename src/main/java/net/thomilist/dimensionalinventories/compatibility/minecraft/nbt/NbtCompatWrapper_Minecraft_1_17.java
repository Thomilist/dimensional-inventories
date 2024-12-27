package net.thomilist.dimensionalinventories.compatibility.minecraft.nbt;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.MinecraftServer;
import net.thomilist.dimensionalinventories.compatibility.LimitedCompatibility;

@LimitedCompatibility( target = "Minecraft",
                       versions = ">=1.17" )
public final class NbtCompatWrapper_Minecraft_1_17
    implements NbtCompatWrapper
{
    @Override
    public ItemStack toItemStack( final NbtCompound nbtCompound )
    {
        if ( nbtCompound.isEmpty() || nbtCompound.getString( "id" ).matches( "^minecraft:air$" ) )
        {
            return ItemStack.EMPTY;
        }

        return ItemStack.fromNbt( nbtCompound );
    }

    @Override
    public NbtCompound fromItemStack( final ItemStack itemStack )
    {
        if ( itemStack.isEmpty() )
        {
            return null;
        }

        return itemStack.writeNbt( new NbtCompound() );
    }

    @Override
    public StatusEffectInstance toStatusEffectInstance( final NbtCompound nbtCompound )
    {
        return StatusEffectInstance.fromNbt( nbtCompound );
    }

    @Override
    public NbtCompound fromStatusEffectInstance( final StatusEffectInstance statusEffectInstance )
    {
        return statusEffectInstance.writeNbt( new NbtCompound() );
    }
}
