package net.thomilist.dimensionalinventories.compatibility.minecraft.inventory;

import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import net.thomilist.dimensionalinventories.compatibility.LimitedCompatibility;

@LimitedCompatibility( target = "Minecraft",
                       versions = ">=1.20.3" )
public class SimpleInventoryCompatWrapper_Minecraft_1_20_3
    implements SimpleInventoryCompatWrapper
{
    @Override
    public DefaultedList<ItemStack> getHeldStacks( final SimpleInventory simpleInventory )
    {
        return simpleInventory.getHeldStacks();
    }
}
