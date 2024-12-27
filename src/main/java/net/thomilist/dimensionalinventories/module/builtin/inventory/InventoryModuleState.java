package net.thomilist.dimensionalinventories.module.builtin.inventory;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.EnderChestInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.collection.DefaultedList;
import net.thomilist.dimensionalinventories.compatibility.Compat;
import net.thomilist.dimensionalinventories.module.base.player.PlayerModuleState;
import net.thomilist.dimensionalinventories.util.ItemStackListHelper;

import java.lang.reflect.Type;

public class InventoryModuleState
    implements PlayerModuleState
{
    private static final int ARMOR_SIZE = 4;
    private static final int MAIN_SIZE = PlayerInventory.MAIN_SIZE;
    private static final int OFF_HAND_SIZE = 1;
    private static final int ENDER_CHEST_SIZE = new EnderChestInventory().size();

    public final DefaultedList<ItemStack> armor = DefaultedList.ofSize(
        InventoryModuleState.ARMOR_SIZE,
        ItemStack.EMPTY
    );
    public final DefaultedList<ItemStack> main = DefaultedList.ofSize(
        InventoryModuleState.MAIN_SIZE,
        ItemStack.EMPTY
    );
    public final DefaultedList<ItemStack> offHand = DefaultedList.ofSize(
        InventoryModuleState.OFF_HAND_SIZE,
        ItemStack.EMPTY
    );
    public final DefaultedList<ItemStack> enderChest = DefaultedList.ofSize(
        InventoryModuleState.ENDER_CHEST_SIZE,
        ItemStack.EMPTY
    );

    public InventoryModuleState()
    { }

    public InventoryModuleState( final ServerPlayerEntity player )
    {
        this.loadFromPlayer( player );
    }

    @Override
    public Type type()
    {
        return InventoryModuleState.class;
    }

    @Override
    public void applyToPlayer( final ServerPlayerEntity player )
    {
        ItemStackListHelper.assignItemStacks( this.armor, player.getInventory().armor );
        ItemStackListHelper.assignItemStacks( this.main, player.getInventory().main );
        ItemStackListHelper.assignItemStacks( this.offHand, player.getInventory().offHand );
        ItemStackListHelper.assignItemStacks( this.enderChest, Compat.SIMPLE_INVENTORY.getHeldStacks(player.getEnderChestInventory()) );
    }

    @Override
    public void loadFromPlayer( final ServerPlayerEntity player )
    {
        ItemStackListHelper.assignItemStacks( player.getInventory().armor, this.armor );
        ItemStackListHelper.assignItemStacks( player.getInventory().main, this.main );
        ItemStackListHelper.assignItemStacks( player.getInventory().offHand, this.offHand );
        ItemStackListHelper.assignItemStacks( Compat.SIMPLE_INVENTORY.getHeldStacks(player.getEnderChestInventory()), this.enderChest );
    }

    public DefaultedList<ItemStack> section( final InventorySection label )
    {
        return switch ( label )
        {
            case ARMOR -> this.armor;
            case MAIN -> this.main;
            case OFF_HAND -> this.offHand;
            case ENDER_CHEST -> this.enderChest;
        };
    }
}
