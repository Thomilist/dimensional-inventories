package net.thomilist.dimensionalinventories.gametest.util.assertion;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

public class ItemStackAsserter
{
    private final GameTestHelper context;
    private final Registry<Enchantment> enchantmentRegistry;

    public ItemStackAsserter( final GameTestHelper context )
    {
        this.context = context;
        this.enchantmentRegistry = context.getLevel().registryAccess().lookupOrThrow( Registries.ENCHANTMENT );
    }

    public void assertItemsEqual( final ItemStack itemStack, final ItemStack expectedItemStack, final String name )
    {
        AssertionUtils.assertEquals( this.context, itemStack, expectedItemStack, name, ItemStack::matches );
    }

    public void assertEmpty( final ItemStack itemStack, final String name )
    {
        this.context.assertTrue(
            itemStack.isEmpty(),
            Component.nullToEmpty( "Expected %s to be empty, but was %s".formatted( name, itemStack ) )
        );
    }

    public void assertItemType( final ItemStack itemStack, final Item expectedItem )
    {
        this.context.assertTrue(
            itemStack.is( expectedItem ),
            Component.nullToEmpty( "Expected item type to be %s, but was %s".formatted(
                expectedItem,
                itemStack.getItem()
            ) )
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
            itemStack.getDamageValue(),
            expectedDamage,
            "%s damage".formatted( itemStack.getItem() )
        );
    }

    public void assertName( final ItemStack itemStack, final String expectedName )
    {
        AssertionUtils.assertEquals(
            this.context,
            itemStack.getHoverName().getString(),
            expectedName,
            "%s item name".formatted( itemStack.getItem() )
        );
    }

    public void assertEnchantment( final ItemStack itemStack,
                                   final ResourceKey<Enchantment> expectedEnchantment,
                                   final int expectedEnchantmentLevel )
    {
        AssertionUtils.assertEquals(
            this.context,
            EnchantmentHelper
                .getEnchantmentsForCrafting( itemStack )
                .getLevel( this.enchantmentRegistry.getOrThrow( expectedEnchantment ) ),
            expectedEnchantmentLevel,
            "%s level".formatted( expectedEnchantment )
        );
    }
}
