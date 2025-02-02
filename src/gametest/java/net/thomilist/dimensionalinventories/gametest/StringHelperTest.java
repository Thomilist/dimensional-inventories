package net.thomilist.dimensionalinventories.gametest;

import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.thomilist.dimensionalinventories.gametest.util.assertion.AssertionUtils;
import net.thomilist.dimensionalinventories.util.StringHelper;

public class StringHelperTest
{
    @GameTest( templateName = FabricGameTest.EMPTY_STRUCTURE )
    public void joinScopes( final TestContext context )
    {
        final String[] scopes = { "one", "two", "three", "four", "five" };
        final String joinedScopes = StringHelper.joinScopes( scopes );

        AssertionUtils.assertEquals( context, joinedScopes, "one :: two :: three :: four :: five", "joined scopes" );

        context.complete();
    }

    @GameTest( templateName = FabricGameTest.EMPTY_STRUCTURE )
    public void joinAndWrapScopes( final TestContext context )
    {
        final String[] scopes = { "one", "two", "three", "four", "five" };
        final String joinedScopes = StringHelper.joinAndWrapScopes( scopes );

        AssertionUtils.assertEquals(
            context,
            joinedScopes,
            "[ one :: two :: three :: four :: five ]",
            "joined and wrapped scopes"
        );

        context.complete();
    }
}
