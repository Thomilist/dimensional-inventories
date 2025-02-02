package net.thomilist.dimensionalinventories.gametest.util.assertion;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.test.TestContext;

public class ItemStackAsserter
{
    private final TestContext context;

    public ItemStackAsserter( final TestContext context )
    {
        this.context = context;
    }

    public void assertItemType( final ItemStack itemStack, final Item expectedItem )
    {
        this.context.assertTrue(
            itemStack.isOf( expectedItem ),
            "Expected item type %s; found %s".formatted( expectedItem, itemStack.getItem() )
        );
    }

    public void assertCount( final ItemStack itemStack, final int expectedCount )
    {
        this.context.assertEquals( itemStack.getCount(), expectedCount, "%s count".formatted( itemStack.getItem() ) );
    }

    public void assertDamage( final ItemStack itemStack, final int expectedDamage )
    {
        this.context.assertEquals(
            itemStack.getDamage(),
            expectedDamage,
            "%s damage".formatted( itemStack.getItem() )
        );
    }

    public void assertName( final ItemStack itemStack, final String expectedName )
    {
        this.context.assertEquals(
            itemStack.getName(),
            expectedName,
            "%s custom item name".formatted( itemStack.getItem() )
        );
    }

    public void assertEnchantment( final ItemStack itemStack,
                                   final Enchantment expectedEnchantment,
                                   final int expectedEnchantmentLevel )
    {
        this.context.assertEquals(
            EnchantmentHelper.getLevel( expectedEnchantment, itemStack ),
            expectedEnchantmentLevel,
            "%s level".formatted( expectedEnchantment )
        );
    }
}
