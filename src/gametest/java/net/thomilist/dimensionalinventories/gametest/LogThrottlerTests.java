package net.thomilist.dimensionalinventories.gametest;

import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.test.TestContext;
import net.thomilist.dimensionalinventories.gametest.util.assertion.AssertionUtils;
import net.thomilist.dimensionalinventories.util.LogThrottler;

public class LogThrottlerTests
    extends DimensionalInventoriesGameTest
{
    @GameTest
    public void throttleLog( final TestContext context )
    {
        this.begin();

        final LogThrottler logThrottler = new LogThrottler( 10 );
        int count = 0;

        for ( int i = 0; i < 1000; i++ )
        {
            if ( logThrottler.get() )
            {
                count++;
            }
        }

        AssertionUtils.assertEquals( context, count, 100, "number of allowed logs" );

        context.complete();
        this.end();
    }
}
