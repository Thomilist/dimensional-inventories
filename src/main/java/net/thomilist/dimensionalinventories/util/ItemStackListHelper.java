package net.thomilist.dimensionalinventories.util;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;

public final class ItemStackListHelper
{
    private ItemStackListHelper()
    { }

    public static void assignItemStacks( final NonNullList<ItemStack> source, final NonNullList<ItemStack> target )
    {
        if ( source.size() != target.size() )
        {
            return;
        }

        for ( int i = 0; i < source.size(); ++i )
        {
            target.set( i, source.get( i ) );
        }
    }

    public static void fillWithCopies( final NonNullList<ItemStack> target,
                                       final ItemStack itemStackToCopy,
                                       final int count )
    {
        target.replaceAll( ignored -> itemStackToCopy.copy() );

        while ( target.size() < count )
        {
            target.add( itemStackToCopy.copy() );
        }
    }
}
