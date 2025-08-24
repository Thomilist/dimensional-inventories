package net.thomilist.dimensionalinventories.gametest;

import carpet.CarpetSettings;
import carpet.patches.EntityPlayerMPFake;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.test.TestServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.thomilist.dimensionalinventories.DimensionalInventories;
import net.thomilist.dimensionalinventories.gametest.mixin.MinecraftServerMixin;
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
     * <a href="https://github.com/gnembon/fabric-carpet">Carpet Mod</a> is used. However, before spawning the player,
     * the Carpet Mod implementation first checks the server's user cache for a matching username. This does not work on
     * a vanilla {@link TestServer}, but this is patched with the {@link MinecraftServerMixin}.
     *
     * @param context The test context
     */
    @GameTest( templateName = FabricGameTest.EMPTY_STRUCTURE,
               batchId = Batches.MAIN )
    public void transitionHandledExactlyOnce( final TestContext context )
    {
        // Configure dimension pools (creative mode in both, so the player is allowed to fly)

        final DimensionPoolConfigModuleState dimensionPoolConfig = DimensionalInventories.INSTANCE.configModules
            .get( DimensionPoolConfigModule.class )
            .state();

        dimensionPoolConfig.createPool( BasicModSetup.ORIGIN_DIMENSION_POOL_ID, GameMode.CREATIVE );
        dimensionPoolConfig.assignDimensionToPool(
            BasicModSetup.ORIGIN_DIMENSION,
            BasicModSetup.ORIGIN_DIMENSION_POOL_ID
        );

        dimensionPoolConfig.createPool( BasicModSetup.DESTINATION_DIMENSION_POOL_ID, GameMode.CREATIVE );
        dimensionPoolConfig.assignDimensionToPool(
            BasicModSetup.DESTINATION_DIMENSION,
            BasicModSetup.DESTINATION_DIMENSION_POOL_ID
        );

        // Spawn fake player using Carpet Mod's implementation to ensure the player is actually present in the world

        CarpetSettings.allowSpawningOfflinePlayers = true;

        context.assertTrue(
            EntityPlayerMPFake.createFake(
                DimensionPoolChangeOnRespawnTest.FAKE_PLAYER_NAME,
                context.getWorld().getServer(),
                new Vec3d( 0, 70, 0 ),
                0,
                0,
                context.getWorld().getRegistryKey(),
                GameMode.CREATIVE,
                true
            ), "fake player spawned successfully"
        );

        final ServerPlayerEntity originalPlayer = context
            .getWorld()
            .getServer()
            .getPlayerManager()
            .getPlayer( DimensionPoolChangeOnRespawnTest.FAKE_PLAYER_NAME );

        assert originalPlayer != null;

        // Set the player's spawn point in the overworld

        originalPlayer.setSpawnPoint( World.OVERWORLD, BlockPos.ofFloored( 0, 70, 0 ), 0, true, false );

        // Ensure the player respawns immediately without player interaction

        context
            .getWorld()
            .getServer()
            .getGameRules()
            .get( GameRules.DO_IMMEDIATE_RESPAWN )
            .set( true, context.getWorld().getServer() );

        // 0: player in the overworld; has 64 diamonds

        originalPlayer.teleport( context.getWorld().getServer().getWorld( World.OVERWORLD ), 0, 70, 0, Set.of(), 0, 0 );

        originalPlayer.giveItemStack( new ItemStack( Items.DIAMOND, 64 ) );

        DimensionalInventoriesGameTest.LOGGER.info(
            "0: Player {} is in {}",
            originalPlayer.getName().getString(),
            originalPlayer.getWorld().getRegistryKey()
        );

        context.assertEquals( World.OVERWORLD, originalPlayer.getWorld().getRegistryKey(), "initial dimension" );

        context.assertTrue(
            originalPlayer
                .getInventory()
                .contains( itemStack -> itemStack.isOf( Items.DIAMOND ) && (itemStack.getCount() == 64) ),
            "player initially has 64 diamonds in the overworld"
        );

        // 1: player teleported to the nether (different dimension pool); no items

        originalPlayer.teleport( context.getWorld().getServer().getWorld( World.NETHER ), 0, 130, 0, Set.of(), 0, 0 );

        DimensionalInventoriesGameTest.LOGGER.info(
            "1: Player {} is in {}",
            originalPlayer.getName().getString(),
            originalPlayer.getWorld().getRegistryKey()
        );

        context.assertEquals( World.NETHER, originalPlayer.getWorld().getRegistryKey(), "dimension after teleporting" );

        context.assertFalse(
            originalPlayer.getInventory().contains( itemStack -> itemStack.isOf( Items.DIAMOND ) ),
            "player has no items after teleporting to the nether"
        );

        // 2: player killed; respawns in overworld; has 64 diamonds

        originalPlayer.kill();

        context
            .getWorld()
            .getServer()
            .getPlayerManager()
            .respawnPlayer( originalPlayer, false, Entity.RemovalReason.KILLED );

        final ServerPlayerEntity respawnedPlayer = context
            .getWorld()
            .getServer()
            .getPlayerManager()
            .getPlayer( originalPlayer.getGameProfile().getId() );

        assert respawnedPlayer != null;

        DimensionalInventoriesGameTest.LOGGER.info(
            "2: Player {} is in {}",
            respawnedPlayer.getName().getString(),
            respawnedPlayer.getWorld().getRegistryKey()
        );

        context.assertEquals(
            World.OVERWORLD,
            respawnedPlayer.getWorld().getRegistryKey(),
            "dimension after respawning"
        );

        context.assertTrue(
            respawnedPlayer
                .getInventory()
                .contains( itemStack -> itemStack.isOf( Items.DIAMOND ) && (itemStack.getCount() == 64) ),
            "player has 64 diamonds after respawning in the overworld"
        );

        // 3: player teleported to the nether; no items

        respawnedPlayer.teleport( context.getWorld().getServer().getWorld( World.NETHER ), 0, 130, 0, Set.of(), 0, 0 );

        DimensionalInventoriesGameTest.LOGGER.info(
            "3: Player {} is in {}",
            respawnedPlayer.getName().getString(),
            respawnedPlayer.getWorld().getRegistryKey()
        );

        context.assertEquals(
            World.NETHER,
            respawnedPlayer.getWorld().getRegistryKey(),
            "dimension after teleporting again"
        );

        context.assertFalse(
            respawnedPlayer.getInventory().contains( itemStack -> itemStack.isOf( Items.DIAMOND ) ),
            "player has no items when returning to the nether after respawning"
        );

        // Done

        context.complete();
    }
}
