package net.thomilist.dimensionalinventories.gametest.util.assertion;

import net.minecraft.test.TestContext;
import net.minecraft.text.Text;

import java.util.function.BiFunction;

public final class AssertionUtils
{
    public static <N> void assertEquals( final TestContext context, final N value, final N expected, final String name )
    {
        context.assertEquals( value, expected, Text.of( name ) );
    }

    public static <N> void assertEquals( final TestContext context,
                                         final N value,
                                         final N expected,
                                         final String name,
                                         final BiFunction<N, N, Boolean> equalityComparer )
    {
        context.assertTrue(
            equalityComparer.apply( value, expected ),
            Text.of( "Expected %s to be %s, but was %s".formatted( name, expected, value ) )
        );
    }
}
