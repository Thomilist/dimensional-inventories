package net.thomilist.dimensionalinventories.gametest;

import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;

public class DimensionalInventoriesGameTest
{
    @GameTest(templateName= FabricGameTest.EMPTY_STRUCTURE)
    public void alwaysPasses(TestContext context)
    {
        context.assertTrue(true, "Always passes is true");
        context.complete();
    }

    @GameTest(templateName= FabricGameTest.EMPTY_STRUCTURE)
    public void alwaysFails(TestContext context)
    {
        context.assertTrue(false, "Always fails is true");
        context.complete();
    }
}
