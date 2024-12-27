package net.thomilist.dimensionalinventories.compatibility.minecraft.inventory;

import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import net.thomilist.dimensionalinventories.compatibility.CompatWrapper;

public interface SimpleInventoryCompatWrapper
    extends CompatWrapper
{
    DefaultedList<ItemStack> getHeldStacks( SimpleInventory simpleInventory );
}
