package net.thomilist.dimensionalinventories.gametest;

import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.thomilist.dimensionalinventories.util.LogThrottler;

public class LogThrottlerTest
{
    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void throttleLog(TestContext context)
    {
        LogThrottler logThrottler = new LogThrottler(10);
        int count = 0;

        for (int i = 0; i < 1000; i++)
        {
            if (logThrottler.get())
            {
                count++;
            }
        }

        context.assertTrue(
            count == 100,
            "Logs are not throttled correctly"
        );

        context.complete();
    }
}
