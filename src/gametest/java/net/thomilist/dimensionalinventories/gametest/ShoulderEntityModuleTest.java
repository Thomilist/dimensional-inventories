package net.thomilist.dimensionalinventories.gametest;

import com.mojang.authlib.GameProfile;
import net.fabricmc.fabric.api.entity.FakePlayer;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.ParrotEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.thomilist.dimensionalinventories.gametest.util.BasicModSetup;
import net.thomilist.dimensionalinventories.gametest.util.NbtUtils;
import net.thomilist.dimensionalinventories.mixin.PlayerEntityAccessor;

import java.util.UUID;

public class ShoulderEntityModuleTest
{
    // Shoulder entities (i.e. parrots) should be swapped on dimension pool transition.
    // Test with a single parrot
    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void transitionSwapsSingleShoulderEntity(TestContext context)
    {
        var setup = new BasicModSetup();
        var player = FakePlayer.get(context.getWorld(), new GameProfile(UUID.randomUUID(), "OneParrot"));

        player.setOnGround(true);
        ((PlayerEntityAccessor) player).invokeDropShoulderEntities();

        var parrot = new ParrotEntity(EntityType.PARROT, context.getWorld());
        parrot.setVariant(ParrotEntity.Variant.RED_BLUE);
        var parrotNbt = parrot.writeNbt(new NbtCompound());
        player.addShoulderEntity(parrotNbt);
        var parrotNbtString = NbtHelper.toNbtProviderString(player.getShoulderEntityLeft());

        setup.instance.transitionHandler.handlePlayerDimensionChange(
            player,
            BasicModSetup.ORIGIN_DIMENSION,
            BasicModSetup.DESTINATION_DIMENSION
        );

        context.assertTrue(NbtUtils.isEmpty(player.getShoulderEntityLeft()),
            "Left shoulder empty after transition");
        context.assertTrue(NbtUtils.isEmpty(player.getShoulderEntityRight()),
            "Right shoulder empty after transition");

        setup.instance.transitionHandler.handlePlayerDimensionChange(
            player,
            BasicModSetup.DESTINATION_DIMENSION,
            BasicModSetup.ORIGIN_DIMENSION
        );

        context.assertEquals(NbtHelper.toNbtProviderString(player.getShoulderEntityLeft()), parrotNbtString,
            "Left parrot restored after return transition");
        context.assertTrue(NbtUtils.isEmpty(player.getShoulderEntityRight()),
            "Right shoulder empty after return transition");

        context.complete();
    }

    // Shoulder entities (i.e. parrots) should be swapped on dimension pool transition.
    // Test with two parrots
    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void transitionSwapsBothShoulderEntities(TestContext context)
    {
        var setup = new BasicModSetup();
        var player = FakePlayer.get(context.getWorld(), new GameProfile(UUID.randomUUID(), "TwoParrots"));

        player.setOnGround(true);
        ((PlayerEntityAccessor) player).invokeDropShoulderEntities();

        var leftParrot = new ParrotEntity(EntityType.PARROT, context.getWorld());
        var rightParrot = new ParrotEntity(EntityType.PARROT, context.getWorld());

        leftParrot.setVariant(ParrotEntity.Variant.RED_BLUE);
        rightParrot.setVariant(ParrotEntity.Variant.GREEN);

        var leftParrotNbt = leftParrot.writeNbt(new NbtCompound());
        var rightParrotNbt = rightParrot.writeNbt(new NbtCompound());

        player.addShoulderEntity(leftParrotNbt);
        player.addShoulderEntity(rightParrotNbt);

        var leftParrotNbtString = NbtHelper.toNbtProviderString(player.getShoulderEntityLeft());
        var rightParrotNbtString = NbtHelper.toNbtProviderString(player.getShoulderEntityRight());

        setup.instance.transitionHandler.handlePlayerDimensionChange(
            player,
            BasicModSetup.ORIGIN_DIMENSION,
            BasicModSetup.DESTINATION_DIMENSION
        );

        context.assertTrue(NbtUtils.isEmpty(player.getShoulderEntityLeft()),
            "Left shoulder empty after transition");
        context.assertTrue(NbtUtils.isEmpty(player.getShoulderEntityRight()),
            "Right shoulder empty after transition");

        setup.instance.transitionHandler.handlePlayerDimensionChange(
            player,
            BasicModSetup.DESTINATION_DIMENSION,
            BasicModSetup.ORIGIN_DIMENSION
        );

        context.assertEquals(NbtHelper.toNbtProviderString(player.getShoulderEntityLeft()), leftParrotNbtString,
            "Left parrot restored after return transition");
        context.assertEquals(NbtHelper.toNbtProviderString(player.getShoulderEntityRight()), rightParrotNbtString,
            "Right parrot restored after return transition");

        context.complete();
    }
}
