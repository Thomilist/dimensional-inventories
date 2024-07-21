package net.thomilist.dimensionalinventories.util;

import java.util.Collection;

public class LogHelper
{
    public static final String NAMESPACE_DELIMITER = " :: ";

    public static String joinScopes(String... scopes)
    {
        return String.join(LogHelper.NAMESPACE_DELIMITER, scopes);
    }

    public static String joinScopes(Collection<String> scopes)
    {
        return joinScopes(scopes.toArray(String[]::new));
    }

    public static String joinAndWrapScopes(String... scopes)
    {
        return String.join("", "[ ", joinScopes(scopes), " ]");
    }

    public static String joinAndWrapScopes(Collection<String> scopes)
    {
        return joinAndWrapScopes(scopes.toArray(String[]::new));
    }
}
