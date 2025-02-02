package net.thomilist.dimensionalinventories.gametest.util.assertion;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKey;
import net.minecraft.test.TestContext;
import net.minecraft.util.collection.DefaultedList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class InventoryAsserter
{
    private final ItemStackAsserter itemStackAsserter;
    private final InventoryWrapper inventory;

    public InventoryAsserter( final TestContext context, final Inventory inventory )
    {
        this.itemStackAsserter = new ItemStackAsserter( context );
        this.inventory = new InventoryWrapper( inventory );
    }

    public InventoryAsserter( final TestContext context, final DefaultedList<ItemStack> itemStacks )
    {
        this.itemStackAsserter = new ItemStackAsserter( context );
        this.inventory = new InventoryWrapper( itemStacks );
    }

    public void assertItemAt( final int index, final ItemStack expectedItemStack )
    {
        this.itemStackAsserter.assertItemsEqual(
            this.inventory.getStack( index ),
            expectedItemStack,
            "%s slot %d".formatted( this.inventory, index )
        );
    }

    public void assertEmptyAt( final int index )
    {
        this.itemStackAsserter.assertEmpty(
            this.inventory.getStack( index ),
            "%s slot %d".formatted( this.inventory, index )
        );
    }

    public void assertItemTypeAt( final int index, final Item item )
    {
        this.itemStackAsserter.assertItemType( this.inventory.getStack( index ), item );
    }

    public void assertCountAt( final int index, final int expectedCount )
    {
        this.itemStackAsserter.assertCount( this.inventory.getStack( index ), expectedCount );
    }

    public void assertDamage( final int index, final int expectedDamage )
    {
        this.itemStackAsserter.assertDamage( this.inventory.getStack( index ), expectedDamage );
    }

    public void assertName( final int index, final String expectedName )
    {
        this.itemStackAsserter.assertName( this.inventory.getStack( index ), expectedName );
    }

    public void assertEnchantment( final int index,
                                   final Enchantment expectedEnchantment,
                                   final int expectedEnchantmentLevel )
    {
        this.itemStackAsserter.assertEnchantment(
            this.inventory.getStack( index ),
            expectedEnchantment,
            expectedEnchantmentLevel
        );
    }

    private static class InventoryWrapper
    {
        @Nullable Inventory inventory;
        @Nullable DefaultedList<ItemStack> itemStacks;

        public InventoryWrapper( @NotNull final Inventory inventory )
        {
            this.inventory = inventory;
            this.itemStacks = null;
        }

        public InventoryWrapper( @NotNull final DefaultedList<ItemStack> itemStacks )
        {
            this.inventory = null;
            this.itemStacks = itemStacks;
        }

        public ItemStack getStack( final int index )
        {
            if ( this.inventory != null )
            {
                return this.inventory.getStack( index );
            }

            if ( this.itemStacks != null )
            {
                return this.itemStacks.get( index );
            }

            throw new IllegalStateException();
        }
    }
}
