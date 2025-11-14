package net.thomilist.dimensionalinventories.gametest;

import com.mojang.authlib.GameProfile;
import net.fabricmc.fabric.api.entity.FakePlayer;
import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.ParrotEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.test.TestContext;
import net.minecraft.text.Text;
import net.thomilist.dimensionalinventories.compatibility.Compat;
import net.thomilist.dimensionalinventories.gametest.mixin.ParrotAccessor;
import net.thomilist.dimensionalinventories.gametest.util.BasicModSetup;
import net.thomilist.dimensionalinventories.gametest.util.NbtUtils;
import net.thomilist.dimensionalinventories.mixin.ServerPlayerEntityAccessor;
import net.thomilist.dimensionalinventories.module.base.player.PlayerModule;
import net.thomilist.dimensionalinventories.module.builtin.pool.DimensionPool;
import net.thomilist.dimensionalinventories.module.builtin.shoulderentity.ShoulderEntityModule;
import net.thomilist.dimensionalinventories.util.DummyServerPlayerEntity;

import java.util.UUID;

public class ShoulderEntityModuleTests
    extends DimensionalInventoriesGameTest
{
    // Shoulder entities (i.e. parrots) should be swapped on dimension pool transition.
    // Test with a single parrot
    @GameTest( maxTicks = DimensionalInventoriesGameTest.MAX_TICKS )
    public void transitionSwapsSingleShoulderEntity( final TestContext context )
    {
        final BasicModSetup setup = BasicModSetup.withDefaultModules();
        final FakePlayer player = FakePlayer.get(
            context.getWorld(),
            new GameProfile( UUID.randomUUID(), "OneParrot" )
        );

        player.setOnGround( true );
        ((ServerPlayerEntityAccessor) player).invokeDropShoulderEntities();

        final ParrotEntity parrot = new ParrotEntity( EntityType.PARROT, context.getWorld() );
        ((ParrotAccessor) parrot).invokeSetVariant( ParrotEntity.Variant.RED_BLUE );
        final NbtCompound parrotNbt = Compat.NBT.fromEntity( parrot );
        ((ServerPlayerEntityAccessor) player).invokeSetLeftShoulderNbt( parrotNbt );
        final NbtCompound parrotBefore = player.getLeftShoulderNbt();

        setup.instance.transitionHandler.handlePlayerDimensionChange(
            player,
            BasicModSetup.ORIGIN_DIMENSION,
            BasicModSetup.DESTINATION_DIMENSION
        );

        context.assertTrue(
            NbtUtils.isEffectivelyEmpty( player.getLeftShoulderNbt() ),
            Text.of( "Left shoulder empty after transition" )
        );

        context.assertTrue(
            NbtUtils.isEffectivelyEmpty( player.getRightShoulderNbt() ),
            Text.of( "Right shoulder empty after transition" )
        );

        setup.instance.transitionHandler.handlePlayerDimensionChange(
            player,
            BasicModSetup.DESTINATION_DIMENSION,
            BasicModSetup.ORIGIN_DIMENSION
        );

        context.assertTrue(
            NbtUtils.areEffectivelyEqual( parrotBefore, player.getLeftShoulderNbt() ),
            Text.of( "Left parrot restored after return transition" )
        );

        context.assertTrue(
            NbtUtils.isEffectivelyEmpty( player.getRightShoulderNbt() ),
            Text.of( "Right shoulder empty after return transition" )
        );

        context.complete();
    }

    // Shoulder entities (i.e. parrots) should be swapped on dimension pool transition.
    // Test with two parrots
    @GameTest( maxTicks = DimensionalInventoriesGameTest.MAX_TICKS )
    public void transitionSwapsBothShoulderEntities( final TestContext context )
    {
        final BasicModSetup setup = BasicModSetup.withDefaultModules();
        final FakePlayer player = FakePlayer.get(
            context.getWorld(),
            new GameProfile( UUID.randomUUID(), "TwoParrots" )
        );

        player.setOnGround( true );
        ((ServerPlayerEntityAccessor) player).invokeDropShoulderEntities();

        final ParrotEntity leftParrot = new ParrotEntity( EntityType.PARROT, context.getWorld() );
        final ParrotEntity rightParrot = new ParrotEntity( EntityType.PARROT, context.getWorld() );

        ((ParrotAccessor) leftParrot).invokeSetVariant( ParrotEntity.Variant.RED_BLUE );
        ((ParrotAccessor) rightParrot).invokeSetVariant( ParrotEntity.Variant.GREEN );

        final NbtCompound leftParrotNbt = Compat.NBT.fromEntity( leftParrot );
        final NbtCompound rightParrotNbt = Compat.NBT.fromEntity( rightParrot );

        ((ServerPlayerEntityAccessor) player).invokeSetLeftShoulderNbt( leftParrotNbt );
        ((ServerPlayerEntityAccessor) player).invokeSetRightShoulderNbt( rightParrotNbt );

        final NbtCompound leftParrotBefore = player.getLeftShoulderNbt();
        final NbtCompound rightParrotBefore = player.getRightShoulderNbt();

        setup.instance.transitionHandler.handlePlayerDimensionChange(
            player,
            BasicModSetup.ORIGIN_DIMENSION,
            BasicModSetup.DESTINATION_DIMENSION
        );

        context.assertTrue(
            NbtUtils.isEffectivelyEmpty( player.getLeftShoulderNbt() ),
            Text.of( "Left shoulder empty after transition" )
        );

        context.assertTrue(
            NbtUtils.isEffectivelyEmpty( player.getRightShoulderNbt() ),
            Text.of( "Right shoulder empty after transition" )
        );

        setup.instance.transitionHandler.handlePlayerDimensionChange(
            player,
            BasicModSetup.DESTINATION_DIMENSION,
            BasicModSetup.ORIGIN_DIMENSION
        );

        context.assertTrue(
            NbtUtils.areEffectivelyEqual( leftParrotBefore, player.getLeftShoulderNbt() ),
            Text.of( "Left parrot restored after return transition" )
        );

        context.assertTrue(
            NbtUtils.areEffectivelyEqual( rightParrotBefore, player.getRightShoulderNbt() ),
            Text.of( "Right parrot restored after return transition" )
        );

        context.complete();
    }

    // Parrots changed a bit internally in 1.21.9, but old data should still load correctly.
    @GameTest( maxTicks = DimensionalInventoriesGameTest.MAX_TICKS )
    public void oldDataIsLoadedCorrectly( final TestContext context )
    {
        final String saveDirectoryName = "dimensional-inventories";
        final String resourcePath = "samples/v2/module/shoulder-entity/mv1/" + saveDirectoryName;
        DimensionalInventoriesGameTest.initializeSampleData( context, resourcePath, saveDirectoryName );

        final DummyServerPlayerEntity player = new DummyServerPlayerEntity( context.getWorld(), "50870ba6-fadb-4ac4-8e7f-ba57a56dc5d5" );
        final PlayerModule module = new ShoulderEntityModule( "main" );
        final DimensionPool dimensionPool = new DimensionPool( "origin" );

        module.load( player, dimensionPool );

        context.assertEquals(
            ParrotEntity.Variant.RED_BLUE,
            player.getLeftShoulderParrotVariant().orElseThrow(),
            Text.of( "Left shoulder parrot variant loaded correctly." )
        );

        context.assertEquals(
            ParrotEntity.Variant.GREEN,
            player.getRightShoulderParrotVariant().orElseThrow(),
            Text.of( "Right shoulder parrot variant loaded correctly." )
        );

        module.save( player, dimensionPool );

        context.complete();
    }
}
