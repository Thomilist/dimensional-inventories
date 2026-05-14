package net.thomilist.dimensionalinventories.gametest;

import carpet.CarpetSettings;
import carpet.patches.EntityPlayerMPFake;
import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.storage.LevelData;
import net.minecraft.world.phys.Vec3;
import net.thomilist.dimensionalinventories.DimensionalInventories;
import net.thomilist.dimensionalinventories.compatibility.Compat;
import net.thomilist.dimensionalinventories.gametest.util.BasicModSetup;
import net.thomilist.dimensionalinventories.module.builtin.pool.DimensionPoolConfigModule;
import net.thomilist.dimensionalinventories.module.builtin.pool.DimensionPoolConfigModuleState;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class DimensionPoolChangeOnRespawnTest
    extends DimensionalInventoriesGameTest
{
    private static final @NotNull String FAKE_PLAYER_NAME = "Etho";

    /**
     * Tests that dimension pool transitions are exactly once, even when respawning across dimensions.
     * <p>
     * Double handling can cause data loss or duplication bugs like
     * <a href="https://github.com/Thomilist/dimensional-inventories/issues/25">dimensional-inventories#25</a>.
     * <p>
     * This test requires that a player is actually spawned into the world rather than simply operating on an arbitrary
     * player instance in memory. To do so, {@link EntityPlayerMPFake} from
     * <a href="https://github.com/gnembon/fabric-carpet">Carpet Mod</a> is used.
     *
     * @param context The test context
     */
    @GameTest( maxTicks = DimensionalInventoriesGameTest.MAX_TICKS,
               requiredSuccesses = 5 )
    public void transitionHandledExactlyOnce( final GameTestHelper context )
    {
        // Configure dimension pools (creative mode in both, so the player is allowed to fly)

        final DimensionPoolConfigModuleState dimensionPoolConfig = DimensionalInventories.INSTANCE.configModules
            .get( DimensionPoolConfigModule.class )
            .state();

        dimensionPoolConfig.createPool( BasicModSetup.ORIGIN_DIMENSION_POOL_ID, GameType.CREATIVE );
        dimensionPoolConfig.assignDimensionToPool(
            BasicModSetup.ORIGIN_DIMENSION,
            BasicModSetup.ORIGIN_DIMENSION_POOL_ID
        );

        dimensionPoolConfig.createPool( BasicModSetup.DESTINATION_DIMENSION_POOL_ID, GameType.CREATIVE );
        dimensionPoolConfig.assignDimensionToPool(
            BasicModSetup.DESTINATION_DIMENSION,
            BasicModSetup.DESTINATION_DIMENSION_POOL_ID
        );

        // Spawn fake player using Carpet Mod's implementation to ensure the player is actually present in the world

        CarpetSettings.allowSpawningOfflinePlayers = true;

        context.assertTrue(
            EntityPlayerMPFake.createFake(
                DimensionPoolChangeOnRespawnTest.FAKE_PLAYER_NAME,
                context.getLevel().getServer(),
                new Vec3( 0, 70, 0 ),
                0,
                0,
                context.getLevel().dimension(),
                GameType.CREATIVE,
                true
            ), Component.nullToEmpty( "fake player spawned successfully" )
        );

        final ServerPlayer originalPlayer = context
            .getLevel()
            .getServer()
            .getPlayerList()
            .getPlayerByName( DimensionPoolChangeOnRespawnTest.FAKE_PLAYER_NAME );

        assert originalPlayer != null;

        // Set the player's spawn point in the overworld

        final ServerPlayer.RespawnConfig spawnPoint = new ServerPlayer.RespawnConfig(
            new LevelData.RespawnData(
                new GlobalPos(
                    Level.OVERWORLD,
                    BlockPos.containing( 0, 70, 0 )
                ), 0, 0
            ),
            true
        );

        originalPlayer.setRespawnPosition( spawnPoint, true );

        // Ensure the player respawns immediately without player interaction

        context
            .getLevel()
            .getGameRules()
            .set( GameRules.IMMEDIATE_RESPAWN, true, context.getLevel().getServer() );

        // 0: player in the overworld; has 64 diamonds

        originalPlayer.teleportTo(
            context.getLevel().getServer().getLevel( Level.OVERWORLD ),
            0,
            70,
            0,
            Set.of(),
            0,
            0,
            true
        );

        originalPlayer.addItem( new ItemStack( Items.DIAMOND, 64 ) );

        DimensionalInventoriesGameTest.LOGGER.info(
            "0: Player {} is in {}",
            originalPlayer.getName().getString(),
            Compat.ENTITY.getWorld( originalPlayer ).dimension()
        );

        context.assertValueEqual(
            Level.OVERWORLD,
            Compat.ENTITY.getWorld( originalPlayer ).dimension(),
            Component.nullToEmpty( "initial dimension" )
        );

        context.assertTrue(
            originalPlayer
                .getInventory()
                .contains( itemStack -> itemStack.is( Items.DIAMOND ) && (itemStack.getCount() == 64) ),
            Component.nullToEmpty( "player initially has 64 diamonds in the overworld" )
        );

        // 1: player teleported to the nether (different dimension pool); no items

        originalPlayer.teleportTo(
            context.getLevel().getServer().getLevel( Level.NETHER ),
            0,
            130,
            0,
            Set.of(),
            0,
            0,
            true
        );

        DimensionalInventoriesGameTest.LOGGER.info(
            "1: Player {} is in {}",
            originalPlayer.getName().getString(),
            Compat.ENTITY.getWorld( originalPlayer ).dimension()
        );

        context.assertValueEqual(
            Level.NETHER,
            Compat.ENTITY.getWorld( originalPlayer ).dimension(),
            Component.nullToEmpty( "dimension after teleporting" )
        );

        context.assertFalse(
            originalPlayer.getInventory().contains( itemStack -> itemStack.is( Items.DIAMOND ) ),
            Component.nullToEmpty( "player has no items after teleporting to the nether" )
        );

        // 2: player killed; respawns in overworld; has 64 diamonds

        originalPlayer.kill( Compat.ENTITY.getWorld( originalPlayer ) );

        context
            .getLevel()
            .getServer()
            .getPlayerList()
            .respawn( originalPlayer, false, Entity.RemovalReason.KILLED );

        final ServerPlayer respawnedPlayer = context
            .getLevel()
            .getServer()
            .getPlayerList()
            .getPlayer( originalPlayer.getGameProfile().id() );

        assert respawnedPlayer != null;

        DimensionalInventoriesGameTest.LOGGER.info(
            "2: Player {} is in {}",
            respawnedPlayer.getName().getString(),
            Compat.ENTITY.getWorld( respawnedPlayer ).dimension()
        );

        context.assertValueEqual(
            Level.OVERWORLD,
            Compat.ENTITY.getWorld( respawnedPlayer ).dimension(),
            Component.nullToEmpty( "dimension after respawning" )
        );

        context.assertTrue(
            respawnedPlayer
                .getInventory()
                .contains( itemStack -> itemStack.is( Items.DIAMOND ) && (itemStack.getCount() == 64) ),
            Component.nullToEmpty( "player has 64 diamonds after respawning in the overworld" )
        );

        // 3: player teleported to the nether; no items

        respawnedPlayer.teleportTo(
            context.getLevel().getServer().getLevel( Level.NETHER ),
            0,
            130,
            0,
            Set.of(),
            0,
            0,
            true
        );

        DimensionalInventoriesGameTest.LOGGER.info(
            "3: Player {} is in {}",
            respawnedPlayer.getName().getString(),
            Compat.ENTITY.getWorld( respawnedPlayer ).dimension()
        );

        context.assertValueEqual(
            Level.NETHER,
            Compat.ENTITY.getWorld( respawnedPlayer ).dimension(),
            Component.nullToEmpty( "dimension after teleporting again" )
        );

        context.assertFalse(
            respawnedPlayer.getInventory().contains( itemStack -> itemStack.is( Items.DIAMOND ) ),
            Component.nullToEmpty( "player has no items when returning to the nether after respawning" )
        );

        // Done

        context.succeed();
    }
}
