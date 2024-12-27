package net.thomilist.dimensionalinventories.compatibility.java.collection;

import net.thomilist.dimensionalinventories.compatibility.CompatWrapper;

import java.util.Collections;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.Supplier;

public interface SortedSetCompatWrapper
    extends CompatWrapper
{
    // Not quite equivalent to Java 21's SequencedCollection.reversed(): This creates a new set, whereas
    // SequencedCollection.reversed() returns a reversed view into the original set.
    default <C extends SortedSet<E>, E> C reversed( final C set, final Supplier<C> reversedSetSupplier )
    {
        final C reversedSet = reversedSetSupplier.get();
        reversedSet.addAll( set );
        return reversedSet;
    }

    default <E> SortedSet<E> reversed( final SortedSet<E> set )
    {
        return this.reversed( set, () -> new TreeSet<>( Collections.reverseOrder() ) );
    }
}
