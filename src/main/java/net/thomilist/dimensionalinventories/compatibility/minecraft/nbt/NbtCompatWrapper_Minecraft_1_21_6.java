package net.thomilist.dimensionalinventories.compatibility.minecraft.nbt;

import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtOps;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.storage.NbtWriteView;
import net.minecraft.util.ErrorReporter;
import net.thomilist.dimensionalinventories.compatibility.LimitedCompatibility;

import java.util.Optional;

@LimitedCompatibility( target = "Minecraft",
                       versions = ">=1.21.6" )
public final class NbtCompatWrapper_Minecraft_1_21_6
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
        if ( nbtCompound.isEmpty() )
        {
            return ItemStack.EMPTY;
        }

        final Optional<String> id = nbtCompound.getString( "id" );

        if ( id.isEmpty() )
        {
            return ItemStack.EMPTY;
        }

        if ( id.get().matches( "^minecraft:air$" ) )
        {
            return ItemStack.EMPTY;
        }

        return ItemStack.CODEC
            .parse( this.wrapperLookup.getOps( NbtOps.INSTANCE ), nbtCompound )
            .result()
            .orElse( null );
    }

    @Override
    public NbtCompound fromItemStack( final ItemStack itemStack )
    {
        if ( itemStack.isEmpty() )
        {
            return null;
        }

        return (NbtCompound) ItemStack.CODEC
            .encodeStart( this.wrapperLookup.getOps( NbtOps.INSTANCE ), itemStack )
            .getOrThrow();
    }

    @Override
    public StatusEffectInstance toStatusEffectInstance( final NbtCompound nbtCompound )
    {
        return StatusEffectInstance.CODEC.parse( NbtOps.INSTANCE, nbtCompound ).result().orElse( null );
    }

    @Override
    public NbtCompound fromStatusEffectInstance( final StatusEffectInstance statusEffectInstance )
    {
        return (NbtCompound) StatusEffectInstance.CODEC
            .encodeStart( NbtOps.INSTANCE, statusEffectInstance )
            .result()
            .orElse( new NbtCompound() );
    }

    @Override
    public NbtCompound fromEntity( final Entity entity )
    {
        final NbtWriteView nbtWriteView = NbtWriteView.create( new ErrorReporter.Impl(), this.wrapperLookup );
        entity.writeData( nbtWriteView );
        return nbtWriteView.getNbt();
    }
}
