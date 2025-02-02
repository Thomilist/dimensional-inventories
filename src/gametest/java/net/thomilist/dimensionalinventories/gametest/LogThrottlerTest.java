package net.thomilist.dimensionalinventories.gametest;

import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.thomilist.dimensionalinventories.gametest.util.assertion.AssertionUtils;
import net.thomilist.dimensionalinventories.util.LogThrottler;

public class LogThrottlerTest
{
    @GameTest( templateName = FabricGameTest.EMPTY_STRUCTURE )
    public void throttleLog( final TestContext context )
    {
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
    }
}
