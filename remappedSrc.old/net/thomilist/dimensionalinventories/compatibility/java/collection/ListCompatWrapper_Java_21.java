package net.thomilist.dimensionalinventories.compatibility.java.collection;

import net.thomilist.dimensionalinventories.compatibility.LimitedCompatibility;

import java.util.List;

@LimitedCompatibility( target = "Java",
                       versions = ">=21" )
public class ListCompatWrapper_Java_21
    extends ListCompatWrapper_Java_17
{
    @Override
    public <T> T getFirst( final List<T> list )
    {
        return list.getFirst();
    }

    @Override
    public <T> T getLast( final List<T> list )
    {
        return list.getLast();
    }

    @Override
    public <T> T removeFirst( final List<T> list )
    {
        return list.removeFirst();
    }

    @Override
    public <T> T removeLast( final List<T> list )
    {
        return list.removeLast();
    }
}
