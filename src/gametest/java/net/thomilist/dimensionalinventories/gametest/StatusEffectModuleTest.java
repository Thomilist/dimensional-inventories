package net.thomilist.dimensionalinventories.gametest;

import net.fabricmc.fabric.api.entity.FakePlayer;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.registry.Registries;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.thomilist.dimensionalinventories.gametest.util.BasicModSetup;

public class StatusEffectModuleTest
{
    // Status effects should be swapped on dimension pool transition.
    // Test with all registered status effects
    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void transitionSwapsStatusEffects(TestContext context)
    {
        var setup = new BasicModSetup();
        var player = FakePlayer.get(context.getWorld());

        for (var effect : Registries.STATUS_EFFECT)
        {
            var effectEntry = Registries.STATUS_EFFECT.getEntry(effect);
            var effectInstance = new StatusEffectInstance(effectEntry);
            DimensionalInventoriesGameTest.LOGGER.debug(
                "transitionSwapsStatusEffects: {}", effect.getName().getString());

            player.addStatusEffect(effectInstance);

            setup.instance.transitionHandler.handlePlayerDimensionChange(
                player,
                BasicModSetup.ORIGIN_DIMENSION,
                BasicModSetup.DESTINATION_DIMENSION
            );

            context.assertTrue(player.getStatusEffects().isEmpty(),
                "Player has no status effects after transition");

            setup.instance.transitionHandler.handlePlayerDimensionChange(
                player,
                BasicModSetup.DESTINATION_DIMENSION,
                BasicModSetup.ORIGIN_DIMENSION
            );

            context.assertTrue(player.hasStatusEffect(effectEntry),
                "Player regained status effect after return transition");

            player.clearStatusEffects();
        }

        context.complete();
    }
}
