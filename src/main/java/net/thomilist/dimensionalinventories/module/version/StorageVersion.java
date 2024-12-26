package net.thomilist.dimensionalinventories.module.version;

import java.util.Collections;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

public enum StorageVersion
{
    V1( 1 ),
    V2( 2 );

    private static final SortedSet<StorageVersion> ALL = new TreeSet<>( List.of(
        StorageVersion.V1,
        StorageVersion.V2
    ) );

    public final int version;

    StorageVersion( final int version )
    {
        this.version = version;
    }

    public static StorageVersion latest()
    {
        return StorageVersion.all().last();
    }

    // In ascending order i.e. V1 first
    public static SortedSet<StorageVersion> all()
    {
        return StorageVersion.ALL;
    }

    public static SortedSet<StorageVersion> reversed()
    {
        final TreeSet<StorageVersion> reversedSet = new TreeSet<>( Collections.reverseOrder() );
        reversedSet.addAll( StorageVersion.all() );
        return reversedSet;
    }

    public static int compareSets( final SortedSet<StorageVersion> a, final SortedSet<StorageVersion> b )
    {
        if ( a.equals( b ) )
        {
            return 0;
        }
        else if ( a.isEmpty() && b.isEmpty() )
        {
            return 0;
        }
        else if ( a.isEmpty() )
        {
            return 1;
        }
        else if ( b.isEmpty() )
        {
            return -1;
        }

        // Look for most recent version that only one of the sets contains
        final SortedSet<StorageVersion> combined = new TreeSet<>( Collections.reverseOrder() );
        combined.addAll( a );
        combined.addAll( b );

        for ( final StorageVersion version : combined )
        {
            if ( a.contains( version ) && b.contains( version ) )
            {
                continue;
            }
            else if ( a.contains( version ) )
            {
                return -1;
            }
            else if ( b.contains( version ) )
            {
                return 1;
            }
        }

        return 0;
    }

    @Override
    public String toString()
    {
        return "v" + this.version;
    }
}
