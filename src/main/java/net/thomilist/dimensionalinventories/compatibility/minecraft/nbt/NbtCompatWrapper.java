package net.thomilist.dimensionalinventories.compatibility.minecraft.nbt;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.thomilist.dimensionalinventories.compatibility.CompatWrapper;

public interface NbtCompatWrapper
    extends CompatWrapper
{
    ItemStack toItemStack( CompoundTag nbtCompound );

    CompoundTag fromItemStack( ItemStack itemStack );

    MobEffectInstance toStatusEffectInstance( CompoundTag nbtCompound );

    CompoundTag fromStatusEffectInstance( MobEffectInstance statusEffectInstance );

    CompoundTag fromEntity( Entity entity );
}
