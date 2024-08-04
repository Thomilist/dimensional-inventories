package net.thomilist.dimensionalinventories.gametest;

import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;

public class DimensionalInventoriesGameTest
{
    @GameTest(templateName= FabricGameTest.EMPTY_STRUCTURE)
    public void alwaysPasses(TestContext context)
    {
        context.assertTrue(true, "This test always passes :)");
        context.complete();
    }

    @GameTest(templateName= FabricGameTest.EMPTY_STRUCTURE)
    public void alwaysFails(TestContext context)
    {
        context.assertTrue(false, "This test always fails :(");
        context.complete();
    }

    @GameTest(templateName= FabricGameTest.EMPTY_STRUCTURE, required = false)
    public void alwaysFailsNotRequired(TestContext context)
    {
        context.assertTrue(false, "This test always fails, but it's not required ¯\\_(ツ)_/¯");
        context.complete();
    }
}
