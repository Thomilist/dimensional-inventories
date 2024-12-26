package net.thomilist.dimensionalinventories.util;

import java.util.Collection;
import java.util.List;

public final class StringHelper
{
    public static final String SCOPE_DELIMITER = " :: ";

    private StringHelper()
    { }

    public static String joinScopes( final String... scopes )
    {
        return String.join( StringHelper.SCOPE_DELIMITER, scopes );
    }

    public static String joinScopes( final Collection<String> scopes )
    {
        return StringHelper.joinScopes( scopes.toArray( String[]::new ) );
    }

    public static String joinAndWrapScopes( final String... scopes )
    {
        return String.join( " ", "[", StringHelper.joinScopes( scopes ), "]" );
    }

    public static String joinAndWrapScopes( final Collection<String> scopes )
    {
        return StringHelper.joinAndWrapScopes( scopes.toArray( String[]::new ) );
    }

    public static String toPascalCase( final String text )
    {
        final char[] characters = text.toLowerCase().toCharArray();
        final StringBuilder builder = new StringBuilder();
        boolean capitalizeNext = true;

        for ( final char c : characters )
        {
            if ( !Character.isLetterOrDigit( c ) )
            {
                capitalizeNext = true;
            }
            else if ( capitalizeNext )
            {
                builder.append( Character.toTitleCase( c ) );
                capitalizeNext = false;
            }
            else
            {
                builder.append( c );
            }
        }

        return builder.toString();
    }

    public static String joinLastDifferent( final String mainDelimiter,
                                            final String lastDelimiter,
                                            final String... strings )
    {
        return StringHelper.joinLastDifferent( mainDelimiter, lastDelimiter, List.of( strings ) );
    }

    public static String joinLastDifferent( final String mainDelimiter,
                                            final String lastDelimiter,
                                            final List<String> strings )
    {
        return switch ( strings.size() )
        {
            case 0 -> "";
            case 1 -> strings.getFirst();
            default -> String.join(
                lastDelimiter,
                String.join( mainDelimiter, strings.subList( 0, strings.size() - 1 ) ),
                strings.getLast()
            );
        };
    }
}
