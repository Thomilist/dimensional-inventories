package net.thomilist.dimensionalinventories.gametest.util.assertion;

import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.entity.player.Player;
import net.thomilist.dimensionalinventories.mixin.FoodDataAccessor;

public class StatusAsserter
{
    private final GameTestHelper context;
    private final Player player;

    public StatusAsserter( final GameTestHelper context, final Player player )
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
            this.player.getFoodData().getFoodLevel(),
            expectedFoodLevel,
            "food level"
        );
    }

    public void assertSaturationLevel( final float expectedSaturationLevel )
    {
        AssertionUtils.assertEquals(
            this.context,
            this.player.getFoodData().getSaturationLevel(),
            expectedSaturationLevel,
            "saturation level"
        );
    }

    public void assertExhaustion( final float expectedExhaustion )
    {
        AssertionUtils.assertEquals(
            this.context,
            ((FoodDataAccessor) this.player.getFoodData()).getExhaustionLevel(),
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
