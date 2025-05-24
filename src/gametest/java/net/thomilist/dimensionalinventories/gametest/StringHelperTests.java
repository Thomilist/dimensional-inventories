package net.thomilist.dimensionalinventories.gametest;

import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.test.TestContext;
import net.thomilist.dimensionalinventories.gametest.util.assertion.AssertionUtils;
import net.thomilist.dimensionalinventories.util.StringHelper;

public class StringHelperTests
    extends DimensionalInventoriesGameTest
{
    @GameTest
    public void joinScopes( final TestContext context )
    {
        this.begin();

        final String[] scopes = { "one", "two", "three", "four", "five" };
        final String joinedScopes = StringHelper.joinScopes( scopes );

        AssertionUtils.assertEquals( context, joinedScopes, "one :: two :: three :: four :: five", "joined scopes" );

        context.complete();
        this.end();
    }

    @GameTest
    public void joinAndWrapScopes( final TestContext context )
    {
        this.begin();

        final String[] scopes = { "one", "two", "three", "four", "five" };
        final String joinedScopes = StringHelper.joinAndWrapScopes( scopes );

        AssertionUtils.assertEquals(
            context,
            joinedScopes,
            "[ one :: two :: three :: four :: five ]",
            "joined and wrapped scopes"
        );

        context.complete();
        this.end();
    }
}
