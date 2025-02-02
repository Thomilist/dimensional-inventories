package net.thomilist.dimensionalinventories.gametest.util.assertion;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.test.TestContext;
import net.thomilist.dimensionalinventories.mixin.HungerManagerAccessor;

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
        this.context.assertEquals(
            this.player.totalExperience,
            expectedTotalExperience,
            "total experience"
        );
    }

    public void assertScore( final int expectedScore )
    {
        this.context.assertEquals(
            this.player.getScore(),
            expectedScore,
            "score"
        );
    }

    public void assertFoodLevel( final int expectedFoodLevel )
    {
        this.context.assertEquals(
            this.player.getHungerManager().getFoodLevel(),
            expectedFoodLevel,
            "food level"
        );
    }

    public void assertSaturationLevel( final float expectedSaturationLevel )
    {
        this.context.assertEquals(
            this.player.getHungerManager().getSaturationLevel(),
            expectedSaturationLevel,
            "saturation level"
        );
    }

    public void assertExhaustion( final float expectedExhaustion )
    {
        this.context.assertEquals(
            ((HungerManagerAccessor) this.player.getHungerManager()).getExhaustion(),
            expectedExhaustion,
            "exhaustion"
        );
    }

    public void assertHealth( final float expectedHealth )
    {
        this.context.assertEquals(
            this.player.getHealth(),
            expectedHealth,
            "health"
        );
    }
}
