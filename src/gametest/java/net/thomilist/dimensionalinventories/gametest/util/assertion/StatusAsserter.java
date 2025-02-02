package net.thomilist.dimensionalinventories.gametest.util.assertion;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.test.TestContext;

public class StatusAsserter
{
    private final TestContext context;
    private final PlayerEntity player;

    public StatusAsserter( final TestContext context, final PlayerEntity player )
    {
        this.context = context;
        this.player = player;
    }

    public void assertTotalExperience( final int expectedTotalExperience )
    {
        AssertionUtils.assertEquals(
            this.context,
            this.player.totalExperience,
            expectedTotalExperience,
            "total experience"
        );
    }

    public void assertScore( final int expectedScore )
    {
        AssertionUtils.assertEquals(
            this.context,
            this.player.getScore(),
            expectedScore,
            "score"
        );
    }

    public void assertFoodLevel( final int expectedFoodLevel )
    {
        AssertionUtils.assertEquals(
            this.context,
            this.player.getHungerManager().getFoodLevel(),
            expectedFoodLevel,
            "food level"
        );
    }

    public void assertSaturationLevel( final float expectedSaturationLevel )
    {
        AssertionUtils.assertEquals(
            this.context,
            this.player.getHungerManager().getSaturationLevel(),
            expectedSaturationLevel,
            "saturation level"
        );
    }

    public void assertExhaustion( final float expectedExhaustion )
    {
        AssertionUtils.assertEquals(
            this.context,
            this.player.getHungerManager().getExhaustion(),
            expectedExhaustion,
            "exhaustion"
        );
    }

    public void assertHealth( final float expectedHealth )
    {
        AssertionUtils.assertEquals(
            this.context,
            this.player.getHealth(),
            expectedHealth,
            "health"
        );
    }
}
