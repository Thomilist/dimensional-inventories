package net.thomilist.dimensionalinventories.gametest.util.assertion;

import net.minecraft.test.TestContext;

import java.util.Objects;
import java.util.function.BiFunction;

public final class AssertionUtils
{
    public static <N> void assertEquals( final TestContext context, final N value, final N expected, final String name )
    {
        AssertionUtils.assertEquals( context, value, expected, name, Objects::equals );
    }

    public static <N> void assertEquals( final TestContext context,
                                         final N value,
                                         final N expected,
                                         final String name,
                                         final BiFunction<N, N, Boolean> equalityComparer )
    {
        context.assertTrue(
            equalityComparer.apply( value, expected ),
            "Expected %s to be %s, but was %s".formatted( name, expected, value )
        );
    }
}
