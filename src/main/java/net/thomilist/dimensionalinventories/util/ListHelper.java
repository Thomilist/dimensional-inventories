package net.thomilist.dimensionalinventories.util;

import java.util.List;

public final class ListHelper
{
    public static <T> T getOrDefault( final List<T> list, final int index, final T fallback )
    {
        if ( index >= list.size() )
        {
            return fallback;
        }

        return list.get( index );
    }
}
