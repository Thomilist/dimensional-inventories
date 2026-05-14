package net.thomilist.dimensionalinventories.compatibility.minecraft.inventory;

import net.minecraft.core.NonNullList;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.thomilist.dimensionalinventories.compatibility.LimitedCompatibility;
import net.thomilist.dimensionalinventories.util.ItemStackListHelper;

@LimitedCompatibility( target = "Minecraft",
                       versions = ">=1.20.3" )
public class SimpleInventoryCompatWrapper_Minecraft_1_20_3
    implements SimpleInventoryCompatWrapper
{
    @Override
    public NonNullList<ItemStack> getHeldStacks( final SimpleContainer simpleInventory )
    {
        return simpleInventory.getItems();
    }

    @Override
    public void setHeldStacks( final SimpleContainer simpleInventory, final NonNullList<ItemStack> itemStacks )
    {
        ItemStackListHelper.assignItemStacks( itemStacks, simpleInventory.getItems() );
    }
}
