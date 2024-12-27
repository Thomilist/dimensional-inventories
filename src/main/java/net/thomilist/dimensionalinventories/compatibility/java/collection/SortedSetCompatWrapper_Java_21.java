package net.thomilist.dimensionalinventories.compatibility.java.collection;

import net.thomilist.dimensionalinventories.compatibility.LimitedCompatibility;

import java.util.SortedSet;

@LimitedCompatibility( target = "Java",
                       versions = ">=21" )
public class SortedSetCompatWrapper_Java_21
    extends SortedSetCompatWrapper_Java_17
{
    @Override
    public <E> SortedSet<E> reversed( final SortedSet<E> set )
    {
        return set.reversed();
    }
}
