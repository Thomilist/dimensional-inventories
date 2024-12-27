package net.thomilist.dimensionalinventories.compatibility.minecraft.nbt;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.MinecraftServer;
import net.thomilist.dimensionalinventories.compatibility.LimitedCompatibility;

@LimitedCompatibility( target = "Minecraft",
                       versions = ">=1.20.5" )
public final class NbtCompatWrapper_Minecraft_1_20_5
    implements NbtCompatWrapper
{
    private RegistryWrapper.WrapperLookup wrapperLookup;

    @Override
    public void onServerStarted( final MinecraftServer server )
    {
        NbtCompatWrapper.super.onServerStarted( server );
        this.wrapperLookup = server.getRegistryManager();
    }

    @Override
    public ItemStack toItemStack( final NbtCompound nbtCompound )
    {
        if ( nbtCompound.isEmpty() || nbtCompound.getString( "id" ).matches( "^minecraft:air$" ) )
        {
            return ItemStack.EMPTY;
        }

        return ItemStack.fromNbtOrEmpty( this.wrapperLookup, nbtCompound );
    }

    @Override
    public NbtCompound fromItemStack( final ItemStack itemStack )
    {
        if ( itemStack.isEmpty() )
        {
            return null;
        }

        return (NbtCompound) itemStack.encode( this.wrapperLookup );
    }

    @Override
    public StatusEffectInstance toStatusEffectInstance( final NbtCompound nbtCompound )
    {
        return StatusEffectInstance.fromNbt( nbtCompound );
    }

    @Override
    public NbtCompound fromStatusEffectInstance( final StatusEffectInstance statusEffectInstance )
    {
        return (NbtCompound) statusEffectInstance.writeNbt();
    }
}
