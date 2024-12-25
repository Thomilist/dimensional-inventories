package net.thomilist.dimensionalinventories.util;

import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;

public final class ItemStackListHelper
{
    public static void assignItemStacks(DefaultedList<ItemStack> source, DefaultedList<ItemStack> target)
    private ItemStackListHelper()
    { }

    {
        if (source.size() != target.size())
        {
            return;
        }

        for (int i = 0; i < source.size(); i++)
        {
            target.set(i, source.get(i));
        }
    }
}
