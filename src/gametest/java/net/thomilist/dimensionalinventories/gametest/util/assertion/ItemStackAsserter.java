package net.thomilist.dimensionalinventories.gametest.util.assertion;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.test.TestContext;

public class ItemStackAsserter
{
    private final TestContext context;
    private final Registry<Enchantment> enchantmentRegistry;

    public ItemStackAsserter( final TestContext context )
    {
        this.context = context;
        this.enchantmentRegistry = context.getWorld().getRegistryManager().getOrThrow( RegistryKeys.ENCHANTMENT );
    }

    public void assertItemsEqual( final ItemStack itemStack, final ItemStack expectedItemStack, final String name )
    {
        AssertionUtils.assertEquals( this.context, itemStack, expectedItemStack, name, ItemStack::areItemsEqual );
    }

    public void assertEmpty( final ItemStack itemStack, final String name )
    {
        this.assertItemsEqual( itemStack, ItemStack.EMPTY, name );
    }

    public void assertItemType( final ItemStack itemStack, final Item expectedItem )
    {
        this.context.assertTrue(
            itemStack.isOf( expectedItem ),
            "Expected item type to be %s, but was %s".formatted(
                expectedItem,
                itemStack.getItem()
            )
        );
    }

    public void assertCount( final ItemStack itemStack, final int expectedCount )
    {
        AssertionUtils.assertEquals(
            this.context,
            itemStack.getCount(),
            expectedCount,
            "%s count".formatted( itemStack.getItem() )
        );
    }

    public void assertDamage( final ItemStack itemStack, final int expectedDamage )
    {
        AssertionUtils.assertEquals(
            this.context,
            itemStack.getDamage(),
            expectedDamage,
            "%s damage".formatted( itemStack.getItem() )
        );
    }

    public void assertName( final ItemStack itemStack, final String expectedName )
    {
        AssertionUtils.assertEquals(
            this.context,
            itemStack.getName(),
            expectedName,
            "%s item name".formatted( itemStack.getItem() )
        );
    }

    public void assertEnchantment( final ItemStack itemStack,
                                   final RegistryKey<Enchantment> expectedEnchantment,
                                   final int expectedEnchantmentLevel )
    {
        AssertionUtils.assertEquals(
            this.context,
            EnchantmentHelper
                .getEnchantments( itemStack )
                .getLevel( this.enchantmentRegistry.getOrThrow( expectedEnchantment ) ),
            expectedEnchantmentLevel,
            "%s level".formatted( expectedEnchantment )
        );
    }
}
