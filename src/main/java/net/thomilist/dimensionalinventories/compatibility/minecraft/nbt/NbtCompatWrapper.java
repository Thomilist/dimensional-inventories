package net.thomilist.dimensionalinventories.compatibility.minecraft.nbt;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.thomilist.dimensionalinventories.compatibility.CompatWrapper;

public interface NbtCompatWrapper
    extends CompatWrapper
{
    ItemStack toItemStack( NbtCompound nbtCompound );

    NbtCompound fromItemStack( ItemStack itemStack );

    StatusEffectInstance toStatusEffectInstance( NbtCompound nbtCompound );

    NbtCompound fromStatusEffectInstance( StatusEffectInstance statusEffectInstance );
}
