package net.thomilist.dimensionalinventories.compatibility.java.collection;

import net.thomilist.dimensionalinventories.compatibility.CompatWrapper;

import java.util.List;
import java.util.NoSuchElementException;

public interface ListCompatWrapper
    extends CompatWrapper
{
    /**
     * Gets the first element of a list.
     *
     * @param list the list to get the first element from
     * @param <E>  the type of elements in the list
     *
     * @return the retrieved element
     *
     * @throws NoSuchElementException if the list is empty
     */
    default <E> E getFirst( final List<E> list )
    {
        if ( list.isEmpty() )
        {
            throw new NoSuchElementException();
        }

        return list.get( 0 );
    }

    /**
     * Gets the last element of a list.
     *
     * @param list the list to get the last element from
     * @param <E>  the type of elements in the list
     *
     * @return the retrieved element
     *
     * @throws NoSuchElementException if the list is empty
     */
    default <E> E getLast( final List<E> list )
    {
        if ( list.isEmpty() )
        {
            throw new NoSuchElementException();
        }

        return list.get( list.size() - 1 );
    }

    /**
     * Removes and returns the first element of a list.
     *
     * @param list the list to remove the first element from
     * @param <E>  the type of elements in the list
     *
     * @return the removed element
     *
     * @throws NoSuchElementException if the list is empty
     */
    default <E> E removeFirst( final List<E> list )
    {
        if ( list.isEmpty() )
        {
            throw new NoSuchElementException();
        }

        return list.remove( 0 );
    }

    /**
     * Removes and returns the last element of a list.
     *
     * @param list the list to remove the last element from
     * @param <E>  the type of elements in the list
     *
     * @return the removed element
     *
     * @throws NoSuchElementException if the list is empty
     */
    default <E> E removeLast( final List<E> list )
    {
        if ( list.isEmpty() )
        {
            throw new NoSuchElementException();
        }

        return list.remove( list.size() - 1 );
    }
}
