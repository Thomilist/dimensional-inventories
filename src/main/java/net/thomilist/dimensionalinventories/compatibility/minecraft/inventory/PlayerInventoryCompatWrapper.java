package net.thomilist.dimensionalinventories.compatibility.minecraft.inventory;

import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.thomilist.dimensionalinventories.compatibility.CompatWrapper;

public interface PlayerInventoryCompatWrapper
    extends CompatWrapper
{
    NonNullList<ItemStack> getArmor( final Inventory playerInventory );

    void setArmor( final Inventory playerInventory, final NonNullList<ItemStack> itemStacks );

    NonNullList<ItemStack> getMain( final Inventory playerInventory );

    void setMain( final Inventory playerInventory, final NonNullList<ItemStack> itemStacks );

    NonNullList<ItemStack> getOffHand( final Inventory playerInventory );

    void setOffHand( final Inventory playerInventory, final NonNullList<ItemStack> itemStacks );
}
