package net.thomilist.dimensionalinventories.util;

import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;

public final class ItemStackListHelper
{
    private ItemStackListHelper()
    { }

    public static void assignItemStacks( final DefaultedList<ItemStack> source, final DefaultedList<ItemStack> target )
    {
        if ( source.size() != target.size() )
        {
            return;
        }

        for ( int i = 0; i < source.size(); i++ )
        {
            target.set( i, source.get( i ) );
        }
    }
}
