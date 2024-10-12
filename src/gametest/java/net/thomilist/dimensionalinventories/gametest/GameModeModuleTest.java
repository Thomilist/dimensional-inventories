package net.thomilist.dimensionalinventories.gametest;

import net.fabricmc.fabric.api.entity.FakePlayer;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.world.GameMode;
import net.thomilist.dimensionalinventories.gametest.util.BasicModSetup;

public class GameModeModuleTest
{
    // When a player crosses dimension pools, their gamemode should be changed
    // according to dimension pool settings
    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void transitionSwitchesGameMode(TestContext context)
    {
        var setup = new BasicModSetup();
        var player = FakePlayer.get(context.getWorld());

        var originPool = setup.dimensionPoolConfig.state().poolWithId(BasicModSetup.ORIGIN_DIMENSION_POOL_ID).orElseThrow();
        var destinationPool = setup.dimensionPoolConfig.state().poolWithId(BasicModSetup.DESTINATION_DIMENSION_POOL_ID).orElseThrow();

        originPool.setGameMode(GameMode.SPECTATOR);
        destinationPool.setGameMode(GameMode.CREATIVE);

        setup.instance.transitionHandler.handlePlayerDimensionChange(
            player,
            BasicModSetup.ORIGIN_DIMENSION,
            BasicModSetup.DESTINATION_DIMENSION
        );

        context.testEntity(
            player,
            ServerPlayerEntity::isCreative,
            "Game mode is creative after first transition"
        );

        setup.instance.transitionHandler.handlePlayerDimensionChange(
            player,
            BasicModSetup.DESTINATION_DIMENSION,
            BasicModSetup.ORIGIN_DIMENSION
        );

        context.testEntity(
            player,
            ServerPlayerEntity::isSpectator,
            "Game mode is spectator after return transition"
        );

        context.complete();
    }
}
