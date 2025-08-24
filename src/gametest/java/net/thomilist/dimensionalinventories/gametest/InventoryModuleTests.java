package net.thomilist.dimensionalinventories.gametest;

import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.test.TestContext;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.thomilist.dimensionalinventories.compatibility.Compat;
import net.thomilist.dimensionalinventories.gametest.util.BasicModSetup;
import net.thomilist.dimensionalinventories.util.DummyServerPlayerEntity;
import net.thomilist.dimensionalinventories.util.ItemStackListHelper;

public class InventoryModuleTests
    extends DimensionalInventoriesGameTest
{
    // Swap player inventory on dimension pool transition (kinda the whole point of the mod).
    // Tests with every registered item
    @GameTest( maxTicks = DimensionalInventoriesGameTest.MAX_TICKS )
    public void transitionSwapsPlayerItems( final TestContext context )
    {
        final BasicModSetup setup = BasicModSetup.withDefaultModules();
        final DummyServerPlayerEntity player = new DummyServerPlayerEntity( context.getWorld() );

        for ( final Item item : Registries.ITEM )
        {
            if ( item.equals( Items.AIR ) )
            {
                continue;
            }

            final ItemStack itemStack = new ItemStack( item, item.getMaxCount() );
            DimensionalInventoriesGameTest.LOGGER.debug( "transitionSwapsPlayerItems: {}", itemStack );

            player.giveItemStack( itemStack.copy() );

            context.testEntity(
                player,
                p -> ItemStack.areItemsEqual( itemStack, p.getInventory().getStack( 0 ) ),
                Text.of( "Inventory contents non-empty before first transition" )
            );

            setup.instance.transitionHandler.handlePlayerDimensionChange(
                player,
                BasicModSetup.ORIGIN_DIMENSION,
                BasicModSetup.DESTINATION_DIMENSION
            );

            context.testEntity(
                player,
                p -> ItemStack.areItemsEqual( ItemStack.EMPTY, p.getInventory().getStack( 0 ) ),
                Text.of( "Inventory is empty after first transition" )
            );

            setup.instance.transitionHandler.handlePlayerDimensionChange(
                player,
                BasicModSetup.DESTINATION_DIMENSION,
                BasicModSetup.ORIGIN_DIMENSION
            );

            context.testEntity(
                player,
                p -> ItemStack.areItemsEqual( itemStack, p.getInventory().getStack( 0 ) ),
                Text.of( "Inventory contents restored after return transition" )
            );

            player.getInventory().clear();
        }

        context.complete();
    }

    // Do not swap player inventory on unconfigured transition
    @GameTest( maxTicks = DimensionalInventoriesGameTest.MAX_TICKS )
    public void unconfiguredTransitionDoesNotSwapPlayerItems( final TestContext context )
    {
        final BasicModSetup setup = BasicModSetup.withDefaultModules();
        final DummyServerPlayerEntity player = new DummyServerPlayerEntity( context.getWorld() );
        final ItemStack itemStack = new ItemStack( Items.STONE, Items.STONE.getMaxCount() );

        player.giveItemStack( itemStack.copy() );

        setup.instance.transitionHandler.handlePlayerDimensionChange(
            player,
            BasicModSetup.ORIGIN_DIMENSION,
            BasicModSetup.UNCONFIGURED_DIMENSION
        );

        context.testEntity(
            player,
            p -> ItemStack.areItemsEqual( itemStack, p.getInventory().getStack( 0 ) ),
            Text.of( "Inventory contents unaffected after unconfigured transition" )
        );

        context.complete();
    }

    // Ensure all inventory slots are supported (main, offhand, armour, ender chest)
    @GameTest( maxTicks = DimensionalInventoriesGameTest.MAX_TICKS )
    public void transitionHandlesEveryInventorySlot( final TestContext context )
    {
        final BasicModSetup setup = BasicModSetup.withDefaultModules();
        final DummyServerPlayerEntity player = new DummyServerPlayerEntity( context.getWorld() );
        final ItemStack itemStack = new ItemStack( Items.STONE, Items.STONE.getMaxCount() );

        final DefaultedList<ItemStack> itemStacksMain = DefaultedList.ofSize( 36 );
        final DefaultedList<ItemStack> itemStacksEnderChest = DefaultedList.ofSize( 27 );

        ItemStackListHelper.fillWithCopies( itemStacksMain, itemStack, 36 );
        ItemStackListHelper.fillWithCopies( itemStacksEnderChest, itemStack, 27 );

        Compat.PLAYER_INVENTORY.setMain( player.getInventory(), itemStacksMain );
        Compat.PLAYER_INVENTORY.setOffHand(
            player.getInventory(),
            DefaultedList.copyOf( ItemStack.EMPTY, itemStack.copy() )
        );
        Compat.SIMPLE_INVENTORY.setHeldStacks( player.getEnderChestInventory(), itemStacksEnderChest );

        final ItemStack helmet = new ItemStack( Items.DIAMOND_HELMET );
        final ItemStack chestPlate = new ItemStack( Items.DIAMOND_CHESTPLATE );
        final ItemStack leggings = new ItemStack( Items.DIAMOND_LEGGINGS );
        final ItemStack boots = new ItemStack( Items.DIAMOND_BOOTS );

        Compat.PLAYER_INVENTORY.setArmor(
            player.getInventory(),
            DefaultedList.copyOf( ItemStack.EMPTY, boots, leggings, chestPlate, helmet )
        );

        for ( int i = 0; i < Compat.PLAYER_INVENTORY.getMain( player.getInventory() ).size(); ++i )
        {
            context.assertTrue(
                ItemStack.areItemsEqual( itemStack, Compat.PLAYER_INVENTORY.getMain( player.getInventory() ).get( i ) ),
                Text.of( "Main inventory filled before first transition" )
            );
        }

        for ( int i = 0; i < Compat.PLAYER_INVENTORY.getOffHand( player.getInventory() ).size(); ++i )
        {
            context.assertTrue(
                ItemStack.areItemsEqual(
                    itemStack,
                    Compat.PLAYER_INVENTORY.getOffHand( player.getInventory() ).get( i )
                ),
                Text.of( "Offhand inventory filled before first transition" )
            );
        }

        for ( int i = 0; i < Compat.SIMPLE_INVENTORY.getHeldStacks( player.getEnderChestInventory() ).size(); ++i )
        {
            context.assertTrue(
                ItemStack.areItemsEqual(
                    itemStack,
                    Compat.SIMPLE_INVENTORY
                        .getHeldStacks( player.getEnderChestInventory() )
                        .get( i )
                ),
                Text.of( "Ender chest inventory filled before first transition" )
            );
        }

        context.assertTrue(
            ItemStack.areItemsEqual( helmet, player.getEquippedStack( EquipmentSlot.HEAD ) ),
            Text.of( "Head slot filled before first transition" )
        );

        context.assertTrue(
            ItemStack.areItemsEqual( chestPlate, player.getEquippedStack( EquipmentSlot.CHEST ) ),
            Text.of( "Chest slot filled before first transition" )
        );

        context.assertTrue(
            ItemStack.areItemsEqual( leggings, player.getEquippedStack( EquipmentSlot.LEGS ) ),
            Text.of( "Legs slot filled before first transition" )
        );

        context.assertTrue(
            ItemStack.areItemsEqual( boots, player.getEquippedStack( EquipmentSlot.FEET ) ),
            Text.of( "Feet slot filled before first transition" )
        );

        setup.instance.transitionHandler.handlePlayerDimensionChange(
            player,
            BasicModSetup.ORIGIN_DIMENSION,
            BasicModSetup.DESTINATION_DIMENSION
        );

        for ( int i = 0; i < Compat.PLAYER_INVENTORY.getMain( player.getInventory() ).size(); i++ )
        {
            context.assertTrue(
                ItemStack.areItemsEqual(
                    ItemStack.EMPTY,
                    Compat.PLAYER_INVENTORY.getMain( player.getInventory() ).get( i )
                ),
                Text.of( "Main inventory empty after first transition" )
            );
        }

        for ( int i = 0; i < Compat.PLAYER_INVENTORY.getOffHand( player.getInventory() ).size(); i++ )
        {
            context.assertTrue(
                ItemStack.areItemsEqual(
                    ItemStack.EMPTY,
                    Compat.PLAYER_INVENTORY.getOffHand( player.getInventory() ).get( i )
                ),
                Text.of( "Offhand inventory empty after first transition" )
            );
        }

        for ( int i = 0; i < Compat.SIMPLE_INVENTORY.getHeldStacks( player.getEnderChestInventory() ).size(); i++ )
        {
            context.assertTrue(
                ItemStack.areItemsEqual(
                    ItemStack.EMPTY,
                    Compat.SIMPLE_INVENTORY
                        .getHeldStacks( player.getEnderChestInventory() )
                        .get( i )
                ), Text.of( "Ender chest inventory empty after first transition" )
            );
        }

        context.assertTrue(
            ItemStack.areItemsEqual( ItemStack.EMPTY, player.getEquippedStack( EquipmentSlot.HEAD ) ),
            Text.of( "Head slot empty after first transition" )
        );

        context.assertTrue(
            ItemStack.areItemsEqual( ItemStack.EMPTY, player.getEquippedStack( EquipmentSlot.CHEST ) ),
            Text.of( "Chest slot empty after first transition" )
        );

        context.assertTrue(
            ItemStack.areItemsEqual( ItemStack.EMPTY, player.getEquippedStack( EquipmentSlot.LEGS ) ),
            Text.of( "Legs slot empty after first transition" )
        );

        context.assertTrue(
            ItemStack.areItemsEqual( ItemStack.EMPTY, player.getEquippedStack( EquipmentSlot.FEET ) ),
            Text.of( "Feet slot empty after first transition" )
        );

        setup.instance.transitionHandler.handlePlayerDimensionChange(
            player,
            BasicModSetup.DESTINATION_DIMENSION,
            BasicModSetup.ORIGIN_DIMENSION
        );

        for ( int i = 0; i < Compat.PLAYER_INVENTORY.getMain( player.getInventory() ).size(); ++i )
        {
            context.assertTrue(
                ItemStack.areItemsEqual( itemStack, Compat.PLAYER_INVENTORY.getMain( player.getInventory() ).get( i ) ),
                Text.of( "Main inventory restored after return transition" )
            );
        }

        for ( int i = 0; i < Compat.PLAYER_INVENTORY.getOffHand( player.getInventory() ).size(); ++i )
        {
            context.assertTrue(
                ItemStack.areItemsEqual(
                    itemStack,
                    Compat.PLAYER_INVENTORY.getOffHand( player.getInventory() ).get( i )
                ),
                Text.of( "Offhand inventory restored after return transition" )
            );
        }

        for ( int i = 0; i < Compat.SIMPLE_INVENTORY.getHeldStacks( player.getEnderChestInventory() ).size(); ++i )
        {
            context.assertTrue(
                ItemStack.areItemsEqual(
                    itemStack,
                    Compat.SIMPLE_INVENTORY
                        .getHeldStacks( player.getEnderChestInventory() )
                        .get( i )
                ),
                Text.of( "Ender chest inventory restored after return transition" )
            );
        }

        context.assertTrue(
            ItemStack.areItemsEqual( helmet, player.getEquippedStack( EquipmentSlot.HEAD ) ),
            Text.of( "Head slot restored after return transition" )
        );

        context.assertTrue(
            ItemStack.areItemsEqual( chestPlate, player.getEquippedStack( EquipmentSlot.CHEST ) ),
            Text.of( "Chest slot restored after return transition" )
        );

        context.assertTrue(
            ItemStack.areItemsEqual( leggings, player.getEquippedStack( EquipmentSlot.LEGS ) ),
            Text.of( "Legs slot restored after return transition" )
        );

        context.assertTrue(
            ItemStack.areItemsEqual( boots, player.getEquippedStack( EquipmentSlot.FEET ) ),
            Text.of( "Feet slot restored after return transition" )
        );

        context.complete();
    }
}
