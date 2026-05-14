package net.thomilist.dimensionalinventories.gametest.util.assertion;

import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;

import java.util.function.BiFunction;

public final class AssertionUtils
{
    public static <N> void assertEquals( final GameTestHelper context, final N value, final N expected, final String name )
    {
        context.assertValueEqual( expected, value, Component.nullToEmpty( name ) );
    }

    public static <N> void assertEquals( final GameTestHelper context,
                                         final N value,
                                         final N expected,
                                         final String name,
                                         final BiFunction<N, N, Boolean> equalityComparer )
    {
        context.assertTrue(
            equalityComparer.apply( value, expected ),
            Component.nullToEmpty( "Expected %s to be %s, but was %s".formatted( name, expected, value ) )
        );
    }
}
