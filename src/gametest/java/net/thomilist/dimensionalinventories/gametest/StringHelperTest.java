package net.thomilist.dimensionalinventories.gametest;

import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.thomilist.dimensionalinventories.util.StringHelper;

public class StringHelperTest
{
    @GameTest( templateName = FabricGameTest.EMPTY_STRUCTURE )
    public void joinScopes( final TestContext context )
    {
        final String[] scopes = { "one", "two", "three", "four", "five" };
        final String joinedScopes = StringHelper.joinScopes( scopes );

        context.assertTrue( joinedScopes.equals( "one :: two :: three :: four :: five" ), "Scopes joined incorrectly" );

        context.complete();
    }

    @GameTest( templateName = FabricGameTest.EMPTY_STRUCTURE )
    public void joinAndWrapScopes( final TestContext context )
    {
        final String[] scopes = { "one", "two", "three", "four", "five" };
        final String joinedScopes = StringHelper.joinAndWrapScopes( scopes );

        context.assertTrue(
            joinedScopes.equals( "[ one :: two :: three :: four :: five ]" ),
            "Scopes joined or wrapped incorrectly"
        );

        context.complete();
    }
}
