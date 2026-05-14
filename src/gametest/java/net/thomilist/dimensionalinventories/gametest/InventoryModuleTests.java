package net.thomilist.dimensionalinventories.gametest;

import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
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
    public void transitionSwapsPlayerItems( final GameTestHelper context )
    {
        final BasicModSetup setup = BasicModSetup.withDefaultModules();
        final DummyServerPlayerEntity player = new DummyServerPlayerEntity( context.getLevel() );

        for ( final Item item : BuiltInRegistries.ITEM )
        {
            if ( item.equals( Items.AIR ) )
            {
                continue;
            }

            final ItemStack itemStack = new ItemStack( item, item.getDefaultMaxStackSize() );
            DimensionalInventoriesGameTest.LOGGER.debug( "transitionSwapsPlayerItems: {}", itemStack );

            player.addItem( itemStack.copy() );

            context.assertEntityProperty(
                player,
                p -> ItemStack.isSameItem( itemStack, p.getInventory().getItem( 0 ) ),
                Component.nullToEmpty( "Inventory contents non-empty before first transition" )
            );

            setup.instance.transitionHandler.handlePlayerDimensionChange(
                player,
                BasicModSetup.ORIGIN_DIMENSION,
                BasicModSetup.DESTINATION_DIMENSION
            );

            context.assertEntityProperty(
                player,
                p -> ItemStack.isSameItem( ItemStack.EMPTY, p.getInventory().getItem( 0 ) ),
                Component.nullToEmpty( "Inventory is empty after first transition" )
            );

            setup.instance.transitionHandler.handlePlayerDimensionChange(
                player,
                BasicModSetup.DESTINATION_DIMENSION,
                BasicModSetup.ORIGIN_DIMENSION
            );

            context.assertEntityProperty(
                player,
                p -> ItemStack.isSameItem( itemStack, p.getInventory().getItem( 0 ) ),
                Component.nullToEmpty( "Inventory contents restored after return transition" )
            );

            player.getInventory().clearContent();
        }

        context.succeed();
    }

    // Do not swap player inventory on unconfigured transition
    @GameTest( maxTicks = DimensionalInventoriesGameTest.MAX_TICKS )
    public void unconfiguredTransitionDoesNotSwapPlayerItems( final GameTestHelper context )
    {
        final BasicModSetup setup = BasicModSetup.withDefaultModules();
        final DummyServerPlayerEntity player = new DummyServerPlayerEntity( context.getLevel() );
        final ItemStack itemStack = new ItemStack( Items.STONE, Items.STONE.getDefaultMaxStackSize() );

        player.addItem( itemStack.copy() );

        setup.instance.transitionHandler.handlePlayerDimensionChange(
            player,
            BasicModSetup.ORIGIN_DIMENSION,
            BasicModSetup.UNCONFIGURED_DIMENSION
        );

        context.assertEntityProperty(
            player,
            p -> ItemStack.isSameItem( itemStack, p.getInventory().getItem( 0 ) ),
            Component.nullToEmpty( "Inventory contents unaffected after unconfigured transition" )
        );

        context.succeed();
    }

    // Ensure all inventory slots are supported (main, offhand, armour, ender chest)
    @GameTest( maxTicks = DimensionalInventoriesGameTest.MAX_TICKS )
    public void transitionHandlesEveryInventorySlot( final GameTestHelper context )
    {
        final BasicModSetup setup = BasicModSetup.withDefaultModules();
        final DummyServerPlayerEntity player = new DummyServerPlayerEntity( context.getLevel() );
        final ItemStack itemStack = new ItemStack( Items.STONE, Items.STONE.getDefaultMaxStackSize() );

        final NonNullList<ItemStack> itemStacksMain = NonNullList.createWithCapacity( 36 );
        final NonNullList<ItemStack> itemStacksEnderChest = NonNullList.createWithCapacity( 27 );

        ItemStackListHelper.fillWithCopies( itemStacksMain, itemStack, 36 );
        ItemStackListHelper.fillWithCopies( itemStacksEnderChest, itemStack, 27 );

        Compat.PLAYER_INVENTORY.setMain( player.getInventory(), itemStacksMain );
        Compat.PLAYER_INVENTORY.setOffHand(
            player.getInventory(),
            NonNullList.of( ItemStack.EMPTY, itemStack.copy() )
        );
        Compat.SIMPLE_INVENTORY.setHeldStacks( player.getEnderChestInventory(), itemStacksEnderChest );

        final ItemStack helmet = new ItemStack( Items.DIAMOND_HELMET );
        final ItemStack chestPlate = new ItemStack( Items.DIAMOND_CHESTPLATE );
        final ItemStack leggings = new ItemStack( Items.DIAMOND_LEGGINGS );
        final ItemStack boots = new ItemStack( Items.DIAMOND_BOOTS );

        Compat.PLAYER_INVENTORY.setArmor(
            player.getInventory(),
            NonNullList.of( ItemStack.EMPTY, boots, leggings, chestPlate, helmet )
        );

        for ( int i = 0; i < Compat.PLAYER_INVENTORY.getMain( player.getInventory() ).size(); ++i )
        {
            context.assertTrue(
                ItemStack.isSameItem( itemStack, Compat.PLAYER_INVENTORY.getMain( player.getInventory() ).get( i ) ),
                Component.nullToEmpty( "Main inventory filled before first transition" )
            );
        }

        for ( int i = 0; i < Compat.PLAYER_INVENTORY.getOffHand( player.getInventory() ).size(); ++i )
        {
            context.assertTrue(
                ItemStack.isSameItem(
                    itemStack,
                    Compat.PLAYER_INVENTORY.getOffHand( player.getInventory() ).get( i )
                ),
                Component.nullToEmpty( "Offhand inventory filled before first transition" )
            );
        }

        for ( int i = 0; i < Compat.SIMPLE_INVENTORY.getHeldStacks( player.getEnderChestInventory() ).size(); ++i )
        {
            context.assertTrue(
                ItemStack.isSameItem(
                    itemStack,
                    Compat.SIMPLE_INVENTORY
                        .getHeldStacks( player.getEnderChestInventory() )
                        .get( i )
                ),
                Component.nullToEmpty( "Ender chest inventory filled before first transition" )
            );
        }

        context.assertTrue(
            ItemStack.isSameItem( helmet, player.getItemBySlot( EquipmentSlot.HEAD ) ),
            Component.nullToEmpty( "Head slot filled before first transition" )
        );

        context.assertTrue(
            ItemStack.isSameItem( chestPlate, player.getItemBySlot( EquipmentSlot.CHEST ) ),
            Component.nullToEmpty( "Chest slot filled before first transition" )
        );

        context.assertTrue(
            ItemStack.isSameItem( leggings, player.getItemBySlot( EquipmentSlot.LEGS ) ),
            Component.nullToEmpty( "Legs slot filled before first transition" )
        );

        context.assertTrue(
            ItemStack.isSameItem( boots, player.getItemBySlot( EquipmentSlot.FEET ) ),
            Component.nullToEmpty( "Feet slot filled before first transition" )
        );

        setup.instance.transitionHandler.handlePlayerDimensionChange(
            player,
            BasicModSetup.ORIGIN_DIMENSION,
            BasicModSetup.DESTINATION_DIMENSION
        );

        for ( int i = 0; i < Compat.PLAYER_INVENTORY.getMain( player.getInventory() ).size(); i++ )
        {
            context.assertTrue(
                ItemStack.isSameItem(
                    ItemStack.EMPTY,
                    Compat.PLAYER_INVENTORY.getMain( player.getInventory() ).get( i )
                ),
                Component.nullToEmpty( "Main inventory empty after first transition" )
            );
        }

        for ( int i = 0; i < Compat.PLAYER_INVENTORY.getOffHand( player.getInventory() ).size(); i++ )
        {
            context.assertTrue(
                ItemStack.isSameItem(
                    ItemStack.EMPTY,
                    Compat.PLAYER_INVENTORY.getOffHand( player.getInventory() ).get( i )
                ),
                Component.nullToEmpty( "Offhand inventory empty after first transition" )
            );
        }

        for ( int i = 0; i < Compat.SIMPLE_INVENTORY.getHeldStacks( player.getEnderChestInventory() ).size(); i++ )
        {
            context.assertTrue(
                ItemStack.isSameItem(
                    ItemStack.EMPTY,
                    Compat.SIMPLE_INVENTORY
                        .getHeldStacks( player.getEnderChestInventory() )
                        .get( i )
                ), Component.nullToEmpty( "Ender chest inventory empty after first transition" )
            );
        }

        context.assertTrue(
            ItemStack.isSameItem( ItemStack.EMPTY, player.getItemBySlot( EquipmentSlot.HEAD ) ),
            Component.nullToEmpty( "Head slot empty after first transition" )
        );

        context.assertTrue(
            ItemStack.isSameItem( ItemStack.EMPTY, player.getItemBySlot( EquipmentSlot.CHEST ) ),
            Component.nullToEmpty( "Chest slot empty after first transition" )
        );

        context.assertTrue(
            ItemStack.isSameItem( ItemStack.EMPTY, player.getItemBySlot( EquipmentSlot.LEGS ) ),
            Component.nullToEmpty( "Legs slot empty after first transition" )
        );

        context.assertTrue(
            ItemStack.isSameItem( ItemStack.EMPTY, player.getItemBySlot( EquipmentSlot.FEET ) ),
            Component.nullToEmpty( "Feet slot empty after first transition" )
        );

        setup.instance.transitionHandler.handlePlayerDimensionChange(
            player,
            BasicModSetup.DESTINATION_DIMENSION,
            BasicModSetup.ORIGIN_DIMENSION
        );

        for ( int i = 0; i < Compat.PLAYER_INVENTORY.getMain( player.getInventory() ).size(); ++i )
        {
            context.assertTrue(
                ItemStack.isSameItem( itemStack, Compat.PLAYER_INVENTORY.getMain( player.getInventory() ).get( i ) ),
                Component.nullToEmpty( "Main inventory restored after return transition" )
            );
        }

        for ( int i = 0; i < Compat.PLAYER_INVENTORY.getOffHand( player.getInventory() ).size(); ++i )
        {
            context.assertTrue(
                ItemStack.isSameItem(
                    itemStack,
                    Compat.PLAYER_INVENTORY.getOffHand( player.getInventory() ).get( i )
                ),
                Component.nullToEmpty( "Offhand inventory restored after return transition" )
            );
        }

        for ( int i = 0; i < Compat.SIMPLE_INVENTORY.getHeldStacks( player.getEnderChestInventory() ).size(); ++i )
        {
            context.assertTrue(
                ItemStack.isSameItem(
                    itemStack,
                    Compat.SIMPLE_INVENTORY
                        .getHeldStacks( player.getEnderChestInventory() )
                        .get( i )
                ),
                Component.nullToEmpty( "Ender chest inventory restored after return transition" )
            );
        }

        context.assertTrue(
            ItemStack.isSameItem( helmet, player.getItemBySlot( EquipmentSlot.HEAD ) ),
            Component.nullToEmpty( "Head slot restored after return transition" )
        );

        context.assertTrue(
            ItemStack.isSameItem( chestPlate, player.getItemBySlot( EquipmentSlot.CHEST ) ),
            Component.nullToEmpty( "Chest slot restored after return transition" )
        );

        context.assertTrue(
            ItemStack.isSameItem( leggings, player.getItemBySlot( EquipmentSlot.LEGS ) ),
            Component.nullToEmpty( "Legs slot restored after return transition" )
        );

        context.assertTrue(
            ItemStack.isSameItem( boots, player.getItemBySlot( EquipmentSlot.FEET ) ),
            Component.nullToEmpty( "Feet slot restored after return transition" )
        );

        context.succeed();
    }
}
