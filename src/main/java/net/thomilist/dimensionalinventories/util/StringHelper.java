package net.thomilist.dimensionalinventories.util;

import java.util.Collection;
import java.util.List;

public class StringHelper
{
    public static final String NAMESPACE_DELIMITER = " :: ";

    public static String joinScopes(String... scopes)
    {
        return String.join(StringHelper.NAMESPACE_DELIMITER, scopes);
    }

    public static String joinScopes(Collection<String> scopes)
    {
        return joinScopes(scopes.toArray(String[]::new));
    }

    public static String joinAndWrapScopes(String... scopes)
    {
        return String.join(" ", "[", joinScopes(scopes), "]");
    }

    public static String joinAndWrapScopes(Collection<String> scopes)
    {
        return joinAndWrapScopes(scopes.toArray(String[]::new));
    }

    public static String toPascalCase(String text)
    {
        char[] characters = text.toLowerCase().toCharArray();
        StringBuilder builder = new StringBuilder();
        boolean capitalizeNext = true;

        for (char c : characters)
        {
            if (!Character.isLetterOrDigit(c))
            {
                capitalizeNext = true;
            }
            else if (capitalizeNext)
            {
                builder.append(Character.toTitleCase(c));
                capitalizeNext = false;
            }
            else
            {
                builder.append(c);
            }
        }

        return builder.toString();
    }

    public static String joinLastDifferent(String mainDelimiter, String lastDelimiter, String... strings)
    {
        return joinLastDifferent(mainDelimiter, lastDelimiter, List.of(strings));
    }

    public static String joinLastDifferent(String mainDelimiter, String lastDelimiter, List<String> strings)
    {
        return switch (strings.size())
        {
            case 0 -> "";
            case 1 -> strings.get(0);
            default -> String.join(
                lastDelimiter,
                String.join(mainDelimiter, strings.subList(0, strings.size() - 1)),
                strings.get(strings.size() - 1)
            );
        };
    }
}
