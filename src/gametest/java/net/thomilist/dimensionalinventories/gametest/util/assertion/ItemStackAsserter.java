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
        this.enchantmentRegistry = context.getWorld().getRegistryManager().get( RegistryKeys.ENCHANTMENT );
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
                                   final RegistryKey<Enchantment> expectedEnchantment,
                                   final int expectedEnchantmentLevel )
    {
        this.context.assertEquals(
            EnchantmentHelper
                .getEnchantments( itemStack )
                .getLevel( this.enchantmentRegistry.getEntry( expectedEnchantment ).orElseThrow() ),
            expectedEnchantmentLevel,
            "%s level".formatted( expectedEnchantment )
        );
    }
}
