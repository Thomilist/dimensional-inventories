package net.thomilist.dimensionalinventories.gametest;

import com.mojang.authlib.GameProfile;
import net.fabricmc.fabric.api.entity.FakePlayer;
import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.ParrotEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.test.TestContext;
import net.minecraft.text.Text;
import net.thomilist.dimensionalinventories.gametest.mixin.ParrotAccessor;
import net.thomilist.dimensionalinventories.gametest.util.BasicModSetup;
import net.thomilist.dimensionalinventories.gametest.util.NbtUtils;
import net.thomilist.dimensionalinventories.mixin.PlayerEntityAccessor;

import java.util.UUID;

public class ShoulderEntityModuleTests
    extends DimensionalInventoriesGameTest
{
    // Shoulder entities (i.e. parrots) should be swapped on dimension pool transition.
    // Test with a single parrot
    @GameTest
    public void transitionSwapsSingleShoulderEntity( final TestContext context )
    {
        this.begin();

        final BasicModSetup setup = BasicModSetup.withDefaultModules();
        final FakePlayer player = FakePlayer.get(
            context.getWorld(),
            new GameProfile( UUID.randomUUID(), "OneParrot" )
        );

        player.setOnGround( true );
        ((PlayerEntityAccessor) player).invokeDropShoulderEntities();

        final ParrotEntity parrot = new ParrotEntity( EntityType.PARROT, context.getWorld() );
        ((ParrotAccessor) parrot).invokeSetVariant( ParrotEntity.Variant.RED_BLUE );
        final NbtCompound parrotNbt = parrot.writeNbt( new NbtCompound() );
        player.addShoulderEntity( parrotNbt );
        final NbtCompound parrotBefore = player.getShoulderEntityLeft();

        setup.instance.transitionHandler.handlePlayerDimensionChange(
            player,
            BasicModSetup.ORIGIN_DIMENSION,
            BasicModSetup.DESTINATION_DIMENSION
        );

        context.assertTrue(
            NbtUtils.isEffectivelyEmpty( player.getShoulderEntityLeft() ),
            Text.of( "Left shoulder empty after transition" )
        );

        context.assertTrue(
            NbtUtils.isEffectivelyEmpty( player.getShoulderEntityRight() ),
            Text.of( "Right shoulder empty after transition" )
        );

        setup.instance.transitionHandler.handlePlayerDimensionChange(
            player,
            BasicModSetup.DESTINATION_DIMENSION,
            BasicModSetup.ORIGIN_DIMENSION
        );

        context.assertTrue(
            NbtUtils.areEffectivelyEqual( parrotBefore, player.getShoulderEntityLeft() ),
            Text.of( "Left parrot restored after return transition" )
        );

        context.assertTrue(
            NbtUtils.isEffectivelyEmpty( player.getShoulderEntityRight() ),
            Text.of( "Right shoulder empty after return transition" )
        );

        context.complete();
        this.end();
    }

    // Shoulder entities (i.e. parrots) should be swapped on dimension pool transition.
    // Test with two parrots
    @GameTest
    public void transitionSwapsBothShoulderEntities( final TestContext context )
    {
        this.begin();

        final BasicModSetup setup = BasicModSetup.withDefaultModules();
        final FakePlayer player = FakePlayer.get(
            context.getWorld(),
            new GameProfile( UUID.randomUUID(), "TwoParrots" )
        );

        player.setOnGround( true );
        ((PlayerEntityAccessor) player).invokeDropShoulderEntities();

        final ParrotEntity leftParrot = new ParrotEntity( EntityType.PARROT, context.getWorld() );
        final ParrotEntity rightParrot = new ParrotEntity( EntityType.PARROT, context.getWorld() );

        ((ParrotAccessor) leftParrot).invokeSetVariant( ParrotEntity.Variant.RED_BLUE );
        ((ParrotAccessor) rightParrot).invokeSetVariant( ParrotEntity.Variant.GREEN );

        final NbtCompound leftParrotNbt = leftParrot.writeNbt( new NbtCompound() );
        final NbtCompound rightParrotNbt = rightParrot.writeNbt( new NbtCompound() );

        player.addShoulderEntity( leftParrotNbt );
        player.addShoulderEntity( rightParrotNbt );

        final NbtCompound leftParrotBefore = player.getShoulderEntityLeft();
        final NbtCompound rightParrotBefore = player.getShoulderEntityRight();

        setup.instance.transitionHandler.handlePlayerDimensionChange(
            player,
            BasicModSetup.ORIGIN_DIMENSION,
            BasicModSetup.DESTINATION_DIMENSION
        );

        context.assertTrue(
            NbtUtils.isEffectivelyEmpty( player.getShoulderEntityLeft() ),
            Text.of( "Left shoulder empty after transition" )
        );

        context.assertTrue(
            NbtUtils.isEffectivelyEmpty( player.getShoulderEntityRight() ),
            Text.of( "Right shoulder empty after transition" )
        );

        setup.instance.transitionHandler.handlePlayerDimensionChange(
            player,
            BasicModSetup.DESTINATION_DIMENSION,
            BasicModSetup.ORIGIN_DIMENSION
        );

        context.assertTrue(
            NbtUtils.areEffectivelyEqual( leftParrotBefore, player.getShoulderEntityLeft() ),
            Text.of( "Left parrot restored after return transition" )
        );

        context.assertTrue(
            NbtUtils.areEffectivelyEqual( rightParrotBefore, player.getShoulderEntityRight() ),
            Text.of( "Right parrot restored after return transition" )
        );

        context.complete();
        this.end();
    }
}
