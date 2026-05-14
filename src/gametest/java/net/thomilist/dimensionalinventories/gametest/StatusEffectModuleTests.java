package net.thomilist.dimensionalinventories.gametest;

import net.fabricmc.fabric.api.entity.FakePlayer;
import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.thomilist.dimensionalinventories.gametest.util.BasicModSetup;

public class StatusEffectModuleTests
    extends DimensionalInventoriesGameTest
{
    // Status effects should be swapped on dimension pool transition.
    // Test with all registered status effects
    @GameTest( maxTicks = DimensionalInventoriesGameTest.MAX_TICKS )
    public void transitionSwapsStatusEffects( final GameTestHelper context )
    {
        final BasicModSetup setup = BasicModSetup.withDefaultModules();
        final FakePlayer player = FakePlayer.get( context.getLevel() );

        for ( final MobEffect effect : BuiltInRegistries.MOB_EFFECT )
        {
            final Holder<MobEffect> effectEntry = BuiltInRegistries.MOB_EFFECT.wrapAsHolder( effect );
            final MobEffectInstance effectInstance = new MobEffectInstance( effectEntry );
            DimensionalInventoriesGameTest.LOGGER.debug(
                "transitionSwapsStatusEffects: {}",
                effect.getDisplayName().getString()
            );

            player.addEffect( effectInstance );

            setup.instance.transitionHandler.handlePlayerDimensionChange(
                player,
                BasicModSetup.ORIGIN_DIMENSION,
                BasicModSetup.DESTINATION_DIMENSION
            );

            context.assertTrue(
                player.getActiveEffects().isEmpty(),
                Component.nullToEmpty( "Player has no status effects after transition" )
            );

            setup.instance.transitionHandler.handlePlayerDimensionChange(
                player,
                BasicModSetup.DESTINATION_DIMENSION,
                BasicModSetup.ORIGIN_DIMENSION
            );

            context.assertTrue(
                player.hasEffect( effectEntry ),
                Component.nullToEmpty( "Player regained status effect after return transition" )
            );

            player.removeAllEffects();
        }

        context.succeed();
    }
}
