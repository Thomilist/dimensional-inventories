package net.thomilist.dimensionalinventories.gametest.util.assertion;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.test.TestContext;

public class InventoryAsserter
{
    private final ItemStackAsserter itemStackAsserter;
    private final Inventory inventory;

    public InventoryAsserter( final TestContext context, final Inventory inventory )
    {
        this.itemStackAsserter = new ItemStackAsserter( context );
        this.inventory = inventory;
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
}
