package net.thomilist.dimensionalinventories.gametest;

import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.thomilist.dimensionalinventories.gametest.util.BasicModSetup;
import net.thomilist.dimensionalinventories.util.DummyServerPlayerEntity;

public class InventoryModuleTest
{
    // Swap player inventory on dimension pool transition (kinda the whole point of the mod).
    // Tests with every registered item
    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void transitionSwapsPlayerItems(TestContext context)
    {
        var setup = new BasicModSetup();
        var player = new DummyServerPlayerEntity(context.getWorld());

        for (var item : Registries.ITEM)
        {
            if (item.equals(Items.AIR))
            {
                continue;
            }

            var itemStack = new ItemStack(item, item.getMaxCount());
            DimensionalInventoriesGameTest.LOGGER.debug("transitionSwapsPlayerItems: {}", itemStack);

            player.giveItemStack(itemStack.copy());

            setup.instance.transitionHandler.handlePlayerDimensionChange(
                player,
                BasicModSetup.ORIGIN_DIMENSION,
                BasicModSetup.DESTINATION_DIMENSION
            );

            context.testEntity(
                player,
                (p -> ItemStack.areItemsEqual(ItemStack.EMPTY, p.getInventory().getStack(0))),
                "Inventory is empty after first transition"
            );

            setup.instance.transitionHandler.handlePlayerDimensionChange(
                player,
                BasicModSetup.DESTINATION_DIMENSION,
                BasicModSetup.ORIGIN_DIMENSION
            );

            context.testEntity(
                player,
                (p -> ItemStack.areItemsEqual(itemStack, p.getInventory().getStack(0))),
                "Inventory contents restored after return transition"
            );

            player.getInventory().clear();
        }

        context.complete();
    }

    // Do not swap player inventory on unconfigured transition
    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void unconfiguredTransitionDoesNotSwapPlayerItems(TestContext context)
    {
        var setup = new BasicModSetup();
        var player = new DummyServerPlayerEntity(context.getWorld());
        var itemStack = new ItemStack(Items.STONE, Items.STONE.getMaxCount());

        player.giveItemStack(itemStack.copy());

        setup.instance.transitionHandler.handlePlayerDimensionChange(
            player,
            BasicModSetup.ORIGIN_DIMENSION,
            BasicModSetup.UNCONFIGURED_DIMENSION
        );

        context.testEntity(
            player,
            (p -> ItemStack.areItemsEqual(itemStack, p.getInventory().getStack(0))),
            "Inventory contents unaffected after unconfigured transition"
        );

        context.complete();
    }

    // Ensure all inventory slots are supported (main, offhand, armour, ender chest)
    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void transitionHandlesEveryInventorySlot(TestContext context)
    {
        var setup = new BasicModSetup();
        var player = new DummyServerPlayerEntity(context.getWorld());
        var itemStack = new ItemStack(Items.STONE, Items.STONE.getMaxCount());

        player.getInventory().main.replaceAll(ignored -> itemStack.copy());
        player.getInventory().offHand.replaceAll(ignored -> itemStack.copy());
        player.getEnderChestInventory().heldStacks.replaceAll(ignored -> itemStack.copy());

        var helmet = new ItemStack(Items.DIAMOND_HELMET);
        var chestPlate = new ItemStack(Items.DIAMOND_CHESTPLATE);
        var leggings = new ItemStack(Items.DIAMOND_LEGGINGS);
        var boots = new ItemStack(Items.DIAMOND_BOOTS);

        player.equipStack(EquipmentSlot.HEAD, helmet.copy());
        player.equipStack(EquipmentSlot.CHEST, chestPlate.copy());
        player.equipStack(EquipmentSlot.LEGS, leggings.copy());
        player.equipStack(EquipmentSlot.FEET, boots.copy());

        setup.instance.transitionHandler.handlePlayerDimensionChange(
            player,
            BasicModSetup.ORIGIN_DIMENSION,
            BasicModSetup.DESTINATION_DIMENSION
        );

        for (int i = 0; i < player.getInventory().main.size(); i++)
        {
            context.assertTrue(
                ItemStack.areItemsEqual(ItemStack.EMPTY, player.getInventory().main.get(i)),
                "Main inventory empty after first transition"
            );
        }

        for (int i = 0; i < player.getInventory().offHand.size(); i++)
        {
            context.assertTrue(
                ItemStack.areItemsEqual(ItemStack.EMPTY, player.getInventory().offHand.get(i)),
                "Offhand inventory empty after first transition"
            );
        }

        for (int i = 0; i < player.getEnderChestInventory().heldStacks.size(); i++)
        {
            context.assertTrue(
                ItemStack.areItemsEqual(ItemStack.EMPTY, player.getEnderChestInventory().heldStacks.get(i)),
                "Ender chest inventory empty after first transition"
            );
        }

        context.assertTrue(
            ItemStack.areItemsEqual(ItemStack.EMPTY, player.getEquippedStack(EquipmentSlot.HEAD)),
            "Head slot empty after first transition"
        );

        context.assertTrue(
            ItemStack.areItemsEqual(ItemStack.EMPTY, player.getEquippedStack(EquipmentSlot.CHEST)),
            "Chest slot empty after first transition"
        );

        context.assertTrue(
            ItemStack.areItemsEqual(ItemStack.EMPTY, player.getEquippedStack(EquipmentSlot.LEGS)),
            "Legs slot empty after first transition"
        );

        context.assertTrue(
            ItemStack.areItemsEqual(ItemStack.EMPTY, player.getEquippedStack(EquipmentSlot.FEET)),
            "Feet slot empty after first transition"
        );

        setup.instance.transitionHandler.handlePlayerDimensionChange(
            player,
            BasicModSetup.DESTINATION_DIMENSION,
            BasicModSetup.ORIGIN_DIMENSION
        );

        for (int i = 0; i < player.getInventory().main.size(); i++)
        {
            context.assertTrue(
                ItemStack.areItemsEqual(itemStack, player.getInventory().main.get(i)),
                "Main inventory restored after return transition"
            );
        }

        for (int i = 0; i < player.getInventory().offHand.size(); i++)
        {
            context.assertTrue(
                ItemStack.areItemsEqual(itemStack, player.getInventory().offHand.get(i)),
                "Offhand inventory restored after return transition"
            );
        }

        for (int i = 0; i < player.getEnderChestInventory().heldStacks.size(); i++)
        {
            context.assertTrue(
                ItemStack.areItemsEqual(itemStack, player.getEnderChestInventory().heldStacks.get(i)),
                "Ender chest inventory restored after return transition"
            );
        }

        context.assertTrue(
            ItemStack.areItemsEqual(helmet, player.getEquippedStack(EquipmentSlot.HEAD)),
            "Head slot restored after return transition"
        );

        context.assertTrue(
            ItemStack.areItemsEqual(chestPlate, player.getEquippedStack(EquipmentSlot.CHEST)),
            "Chest slot restored after return transition"
        );

        context.assertTrue(
            ItemStack.areItemsEqual(leggings, player.getEquippedStack(EquipmentSlot.LEGS)),
            "Legs slot restored after return transition"
        );

        context.assertTrue(
            ItemStack.areItemsEqual(boots, player.getEquippedStack(EquipmentSlot.FEET)),
            "Feet slot restored after return transition"
        );

        context.complete();
    }
}
