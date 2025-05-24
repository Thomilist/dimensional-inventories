package net.thomilist.dimensionalinventories.compatibility.minecraft.inventory;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import net.thomilist.dimensionalinventories.compatibility.CompatWrapper;

public interface PlayerInventoryCompatWrapper
    extends CompatWrapper
{
    DefaultedList<ItemStack> getArmor( final PlayerInventory playerInventory );

    void setArmor( final PlayerInventory playerInventory, final DefaultedList<ItemStack> itemStacks );

    DefaultedList<ItemStack> getMain( final PlayerInventory playerInventory );

    void setMain( final PlayerInventory playerInventory, final DefaultedList<ItemStack> itemStacks );

    DefaultedList<ItemStack> getOffHand( final PlayerInventory playerInventory );

    void setOffHand( final PlayerInventory playerInventory, final DefaultedList<ItemStack> itemStacks );
}
