package net.thomilist.dimensionalinventories.gametest;

import com.mojang.authlib.GameProfile;
import net.fabricmc.fabric.api.entity.FakePlayer;
import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.parrot.Parrot;
import net.thomilist.dimensionalinventories.compatibility.Compat;
import net.thomilist.dimensionalinventories.gametest.mixin.ParrotAccessor;
import net.thomilist.dimensionalinventories.gametest.util.BasicModSetup;
import net.thomilist.dimensionalinventories.mixin.ServerPlayerAccessor;
import net.thomilist.dimensionalinventories.module.base.player.PlayerModule;
import net.thomilist.dimensionalinventories.module.builtin.pool.DimensionPool;
import net.thomilist.dimensionalinventories.module.builtin.shoulderentity.ShoulderEntityModule;
import net.thomilist.dimensionalinventories.util.DummyServerPlayerEntity;
import net.thomilist.dimensionalinventories.util.NbtUtils;

import java.util.UUID;

public class ShoulderEntityModuleTests
    extends DimensionalInventoriesGameTest
{
    // Shoulder entities (i.e. parrots) should be swapped on dimension pool transition.
    // Test with a single parrot
    @GameTest( maxTicks = DimensionalInventoriesGameTest.MAX_TICKS )
    public void transitionSwapsSingleShoulderEntity( final GameTestHelper context )
    {
        final BasicModSetup setup = BasicModSetup.withDefaultModules();
        final FakePlayer player = FakePlayer.get(
            context.getLevel(),
            new GameProfile( UUID.randomUUID(), "OneParrot" )
        );

        player.setOnGround( true );
        ((ServerPlayerAccessor) player).invokeRemoveEntitiesOnShoulder();

        final Parrot parrot = new Parrot( EntityType.PARROT, context.getLevel() );
        ((ParrotAccessor) parrot).invokeSetVariant( Parrot.Variant.RED_BLUE );
        final CompoundTag parrotNbt = Compat.NBT.fromEntity( parrot );
        ((ServerPlayerAccessor) player).invokeSetShoulderEntityLeft( parrotNbt );
        final CompoundTag parrotBefore = player.getShoulderEntityLeft();

        setup.instance.transitionHandler.handlePlayerDimensionChange(
            player,
            BasicModSetup.ORIGIN_DIMENSION,
            BasicModSetup.DESTINATION_DIMENSION
        );

        context.assertTrue(
            NbtUtils.isEffectivelyEmpty( player.getShoulderEntityLeft() ),
            Component.nullToEmpty( "Left shoulder empty after transition" )
        );

        context.assertTrue(
            NbtUtils.isEffectivelyEmpty( player.getShoulderEntityRight() ),
            Component.nullToEmpty( "Right shoulder empty after transition" )
        );

        setup.instance.transitionHandler.handlePlayerDimensionChange(
            player,
            BasicModSetup.DESTINATION_DIMENSION,
            BasicModSetup.ORIGIN_DIMENSION
        );

        context.assertTrue(
            NbtUtils.areEffectivelyEqual( parrotBefore, player.getShoulderEntityLeft() ),
            Component.nullToEmpty( "Left parrot restored after return transition" )
        );

        context.assertTrue(
            NbtUtils.isEffectivelyEmpty( player.getShoulderEntityRight() ),
            Component.nullToEmpty( "Right shoulder empty after return transition" )
        );

        context.succeed();
    }

    // Shoulder entities (i.e. parrots) should be swapped on dimension pool transition.
    // Test with two parrots
    @GameTest( maxTicks = DimensionalInventoriesGameTest.MAX_TICKS )
    public void transitionSwapsBothShoulderEntities( final GameTestHelper context )
    {
        final BasicModSetup setup = BasicModSetup.withDefaultModules();
        final FakePlayer player = FakePlayer.get(
            context.getLevel(),
            new GameProfile( UUID.randomUUID(), "TwoParrots" )
        );

        player.setOnGround( true );
        ((ServerPlayerAccessor) player).invokeRemoveEntitiesOnShoulder();

        final Parrot leftParrot = new Parrot( EntityType.PARROT, context.getLevel() );
        final Parrot rightParrot = new Parrot( EntityType.PARROT, context.getLevel() );

        ((ParrotAccessor) leftParrot).invokeSetVariant( Parrot.Variant.RED_BLUE );
        ((ParrotAccessor) rightParrot).invokeSetVariant( Parrot.Variant.GREEN );

        final CompoundTag leftParrotNbt = Compat.NBT.fromEntity( leftParrot );
        final CompoundTag rightParrotNbt = Compat.NBT.fromEntity( rightParrot );

        ((ServerPlayerAccessor) player).invokeSetShoulderEntityLeft( leftParrotNbt );
        ((ServerPlayerAccessor) player).invokeSetShoulderEntityRight( rightParrotNbt );

        final CompoundTag leftParrotBefore = player.getShoulderEntityLeft();
        final CompoundTag rightParrotBefore = player.getShoulderEntityRight();

        setup.instance.transitionHandler.handlePlayerDimensionChange(
            player,
            BasicModSetup.ORIGIN_DIMENSION,
            BasicModSetup.DESTINATION_DIMENSION
        );

        context.assertTrue(
            NbtUtils.isEffectivelyEmpty( player.getShoulderEntityLeft() ),
            Component.nullToEmpty( "Left shoulder empty after transition" )
        );

        context.assertTrue(
            NbtUtils.isEffectivelyEmpty( player.getShoulderEntityRight() ),
            Component.nullToEmpty( "Right shoulder empty after transition" )
        );

        setup.instance.transitionHandler.handlePlayerDimensionChange(
            player,
            BasicModSetup.DESTINATION_DIMENSION,
            BasicModSetup.ORIGIN_DIMENSION
        );

        context.assertTrue(
            NbtUtils.areEffectivelyEqual( leftParrotBefore, player.getShoulderEntityLeft() ),
            Component.nullToEmpty( "Left parrot restored after return transition" )
        );

        context.assertTrue(
            NbtUtils.areEffectivelyEqual( rightParrotBefore, player.getShoulderEntityRight() ),
            Component.nullToEmpty( "Right parrot restored after return transition" )
        );

        context.succeed();
    }

    // Parrots changed a bit internally in 1.21.9, but old data should still load correctly.
    @GameTest( maxTicks = DimensionalInventoriesGameTest.MAX_TICKS )
    public void oldDataIsLoadedCorrectly( final GameTestHelper context )
    {
        final String saveDirectoryName = "dimensional-inventories";
        final String resourcePath = "samples/v2/module/shoulder-entity/mv1/" + saveDirectoryName;
        DimensionalInventoriesGameTest.initializeSampleData( context, resourcePath, saveDirectoryName );

        final DummyServerPlayerEntity player = new DummyServerPlayerEntity( context.getLevel(), "50870ba6-fadb-4ac4-8e7f-ba57a56dc5d5" );
        final PlayerModule module = new ShoulderEntityModule( "main" );
        final DimensionPool dimensionPool = new DimensionPool( "origin" );

        module.load( player, dimensionPool );

        context.assertValueEqual(
            Parrot.Variant.RED_BLUE,
            player.getShoulderParrotLeft().orElseThrow(),
            Component.nullToEmpty( "Left shoulder parrot variant loaded correctly." )
        );

        context.assertValueEqual(
            Parrot.Variant.GREEN,
            player.getShoulderParrotRight().orElseThrow(),
            Component.nullToEmpty( "Right shoulder parrot variant loaded correctly." )
        );

        module.save( player, dimensionPool );

        context.succeed();
    }
}
