package net.thomilist.dimensionalinventories.gametest.util.assertion;

import net.minecraft.core.NonNullList;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class InventoryAsserter
{
    private final ItemStackAsserter itemStackAsserter;
    private final InventoryWrapper inventory;

    public InventoryAsserter( final GameTestHelper context, final Container inventory )
    {
        this.itemStackAsserter = new ItemStackAsserter( context );
        this.inventory = new InventoryWrapper( inventory );
    }

    public InventoryAsserter( final GameTestHelper context, final NonNullList<ItemStack> itemStacks )
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
                                   final ResourceKey<Enchantment> expectedEnchantment,
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
        @Nullable Container inventory;
        @Nullable NonNullList<ItemStack> itemStacks;

        public InventoryWrapper( @NotNull final Container inventory )
        {
            this.inventory = inventory;
            this.itemStacks = null;
        }

        public InventoryWrapper( @NotNull final NonNullList<ItemStack> itemStacks )
        {
            this.inventory = null;
            this.itemStacks = itemStacks;
        }

        public ItemStack getStack( final int index )
        {
            if ( this.inventory != null )
            {
                return this.inventory.getItem( index );
            }

            if ( this.itemStacks != null )
            {
                return this.itemStacks.get( index );
            }

            throw new IllegalStateException();
        }
    }
}
