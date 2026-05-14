package net.thomilist.dimensionalinventories.gametest;

import net.fabricmc.fabric.api.entity.FakePlayer;
import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameType;
import net.thomilist.dimensionalinventories.gametest.util.BasicModSetup;
import net.thomilist.dimensionalinventories.module.builtin.pool.DimensionPool;

public class GameModeModuleTests
    extends DimensionalInventoriesGameTest
{
    // When a player crosses dimension pools, their gamemode should be changed
    // according to dimension pool settings
    @GameTest( maxTicks = DimensionalInventoriesGameTest.MAX_TICKS )
    public void transitionSwitchesGameMode( final GameTestHelper context )
    {
        final BasicModSetup setup = BasicModSetup.withDefaultModules();
        final FakePlayer player = FakePlayer.get( context.getLevel() );

        final DimensionPool originPool = setup.dimensionPoolConfig
            .state()
            .poolWithId( BasicModSetup.ORIGIN_DIMENSION_POOL_ID )
            .orElseThrow();
        final DimensionPool destinationPool = setup.dimensionPoolConfig
            .state()
            .poolWithId( BasicModSetup.DESTINATION_DIMENSION_POOL_ID )
            .orElseThrow();

        originPool.setGameMode( GameType.SPECTATOR );
        destinationPool.setGameMode( GameType.CREATIVE );

        setup.instance.transitionHandler.handlePlayerDimensionChange(
            player,
            BasicModSetup.ORIGIN_DIMENSION,
            BasicModSetup.DESTINATION_DIMENSION
        );

        context.assertEntityProperty(
            player,
            ServerPlayer::isCreative,
            Component.nullToEmpty( "Game mode is creative after first transition" )
        );

        setup.instance.transitionHandler.handlePlayerDimensionChange(
            player,
            BasicModSetup.DESTINATION_DIMENSION,
            BasicModSetup.ORIGIN_DIMENSION
        );

        context.assertEntityProperty(
            player,
            ServerPlayer::isSpectator,
            Component.nullToEmpty( "Game mode is spectator after return transition" )
        );

        context.succeed();
    }
}
