package net.thomilist.dimensionalinventories.compatibility.minecraft.nbt;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.TagValueOutput;
import net.thomilist.dimensionalinventories.compatibility.LimitedCompatibility;

import java.util.Optional;

@LimitedCompatibility( target = "Minecraft",
                       versions = ">=1.21.6" )
public final class NbtCompatWrapper_Minecraft_1_21_6
    implements NbtCompatWrapper
{
    private HolderLookup.Provider wrapperLookup;

    @Override
    public void onServerStarted( final MinecraftServer server )
    {
        NbtCompatWrapper.super.onServerStarted( server );
        this.wrapperLookup = server.registryAccess();
    }

    @Override
    public ItemStack toItemStack( final CompoundTag nbtCompound )
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
            .parse( this.wrapperLookup.createSerializationContext( NbtOps.INSTANCE ), nbtCompound )
            .result()
            .orElse( null );
    }

    @Override
    public CompoundTag fromItemStack( final ItemStack itemStack )
    {
        if ( itemStack.isEmpty() )
        {
            return null;
        }

        return (CompoundTag) ItemStack.CODEC
            .encodeStart( this.wrapperLookup.createSerializationContext( NbtOps.INSTANCE ), itemStack )
            .getOrThrow();
    }

    @Override
    public MobEffectInstance toStatusEffectInstance( final CompoundTag nbtCompound )
    {
        return MobEffectInstance.CODEC.parse( NbtOps.INSTANCE, nbtCompound ).result().orElse( null );
    }

    @Override
    public CompoundTag fromStatusEffectInstance( final MobEffectInstance statusEffectInstance )
    {
        return (CompoundTag) MobEffectInstance.CODEC
            .encodeStart( NbtOps.INSTANCE, statusEffectInstance )
            .result()
            .orElse( new CompoundTag() );
    }

    @Override
    public CompoundTag fromEntity( final Entity entity )
    {
        final TagValueOutput nbtWriteView = TagValueOutput.createWithContext( new ProblemReporter.Collector(), this.wrapperLookup );
        entity.saveWithoutId( nbtWriteView );
        return nbtWriteView.buildResult();
    }
}
