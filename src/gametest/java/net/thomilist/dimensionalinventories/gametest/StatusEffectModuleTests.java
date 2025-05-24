package net.thomilist.dimensionalinventories.gametest;

import net.fabricmc.fabric.api.entity.FakePlayer;
import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.test.TestContext;
import net.minecraft.text.Text;
import net.thomilist.dimensionalinventories.gametest.util.BasicModSetup;

public class StatusEffectModuleTests
    extends DimensionalInventoriesGameTest
{
    // Status effects should be swapped on dimension pool transition.
    // Test with all registered status effects
    @GameTest
    public void transitionSwapsStatusEffects( final TestContext context )
    {
        this.begin();

        final BasicModSetup setup = BasicModSetup.withDefaultModules();
        final FakePlayer player = FakePlayer.get( context.getWorld() );

        for ( final StatusEffect effect : Registries.STATUS_EFFECT )
        {
            final RegistryEntry<StatusEffect> effectEntry = Registries.STATUS_EFFECT.getEntry( effect );
            final StatusEffectInstance effectInstance = new StatusEffectInstance( effectEntry );
            DimensionalInventoriesGameTest.LOGGER.debug(
                "transitionSwapsStatusEffects: {}",
                effect.getName().getString()
            );

            player.addStatusEffect( effectInstance );

            setup.instance.transitionHandler.handlePlayerDimensionChange(
                player,
                BasicModSetup.ORIGIN_DIMENSION,
                BasicModSetup.DESTINATION_DIMENSION
            );

            context.assertTrue(
                player.getStatusEffects().isEmpty(),
                Text.of( "Player has no status effects after transition" )
            );

            setup.instance.transitionHandler.handlePlayerDimensionChange(
                player,
                BasicModSetup.DESTINATION_DIMENSION,
                BasicModSetup.ORIGIN_DIMENSION
            );

            context.assertTrue(
                player.hasStatusEffect( effectEntry ),
                Text.of( "Player regained status effect after return transition" )
            );

            player.clearStatusEffects();
        }

        context.complete();
        this.end();
    }
}
