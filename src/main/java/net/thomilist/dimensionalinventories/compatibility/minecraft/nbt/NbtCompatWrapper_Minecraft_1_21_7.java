package net.thomilist.dimensionalinventories.compatibility.minecraft.nbt;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.component.ComponentChanges;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.dynamic.Codecs;
import net.thomilist.dimensionalinventories.compatibility.LimitedCompatibility;

import java.util.List;
import java.util.Optional;

@LimitedCompatibility( target = "Minecraft",
                       versions = ">=1.21.7" )
public final class NbtCompatWrapper_Minecraft_1_21_7
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

//        return ItemStack.fromNbt( this.wrapperLookup, nbtCompound ).orElse( ItemStack.EMPTY );
//         return ItemStack.CODEC.parse(this.wrapperLookup.getOps(NbtOps.INSTANCE), nbtCompound).resultOrPartial(error -> ItemStack.LOGGER.error( "Tried to " + "load invalid item: '{}'", error)).orElse( ItemStack.EMPTY );
         return ItemStack.CODEC.parse(this.wrapperLookup.getOps(NbtOps.INSTANCE), nbtCompound).resultOrPartial().orElse( ItemStack.EMPTY );
    }

    @Override
    public NbtCompound fromItemStack( final ItemStack itemStack )
    {
        if ( itemStack.isEmpty() )
        {
            return null;
        }

        return (NbtCompound) ItemStack.CODEC.encodeStart(this.wrapperLookup.getOps(NbtOps.INSTANCE), itemStack).getOrThrow();
//        return (NbtCompound) itemStack.toNbt( this.wrapperLookup );
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
}
