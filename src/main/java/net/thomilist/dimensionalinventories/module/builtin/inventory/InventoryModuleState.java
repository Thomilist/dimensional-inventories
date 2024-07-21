package net.thomilist.dimensionalinventories.module.builtin.inventory;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.EnderChestInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.collection.DefaultedList;
import net.thomilist.dimensionalinventories.module.base.player.PlayerModuleState;
import net.thomilist.dimensionalinventories.util.ItemStackListHelper;

import java.lang.reflect.Type;

public class InventoryModuleState
    implements PlayerModuleState
{
    private static final int ARMOR_SIZE = PlayerInventory.ARMOR_SLOTS.length;
    private static final int MAIN_SIZE = PlayerInventory.MAIN_SIZE;
    private static final int OFF_HAND_SIZE = 1;
    private static final int ENDER_CHEST_SIZE = new EnderChestInventory().size();

    public final DefaultedList<ItemStack> armor = DefaultedList.ofSize(InventoryModuleState.ARMOR_SIZE, ItemStack.EMPTY);
    public final DefaultedList<ItemStack> main = DefaultedList.ofSize(InventoryModuleState.MAIN_SIZE, ItemStack.EMPTY);
    public final DefaultedList<ItemStack> offHand = DefaultedList.ofSize(InventoryModuleState.OFF_HAND_SIZE, ItemStack.EMPTY);
    public final DefaultedList<ItemStack> enderChest = DefaultedList.ofSize(InventoryModuleState.ENDER_CHEST_SIZE, ItemStack.EMPTY);

    public InventoryModuleState()
    { }

    public InventoryModuleState(ServerPlayerEntity player)
    {
        loadFromPlayer(player);
    }

    @Override
    public Type type()
    {
        return InventoryModuleState.class;
    }

    @Override
    public void applyToPlayer(ServerPlayerEntity player)
    {
        ItemStackListHelper.assignItemStacks(armor, player.getInventory().armor);
        ItemStackListHelper.assignItemStacks(main, player.getInventory().main);
        ItemStackListHelper.assignItemStacks(offHand, player.getInventory().offHand);
        ItemStackListHelper.assignItemStacks(enderChest, player.getEnderChestInventory().heldStacks);
    }

    @Override
    public void loadFromPlayer(ServerPlayerEntity player)
    {
        ItemStackListHelper.assignItemStacks(player.getInventory().armor, armor);
        ItemStackListHelper.assignItemStacks(player.getInventory().main, main);
        ItemStackListHelper.assignItemStacks(player.getInventory().offHand, offHand);
        ItemStackListHelper.assignItemStacks(player.getEnderChestInventory().heldStacks, enderChest);
    }

    public DefaultedList<ItemStack> section(InventorySection label)
    {
        return switch (label)
        {
            case ARMOR -> armor;
            case MAIN -> main;
            case OFF_HAND -> offHand;
            case ENDER_CHEST -> enderChest;
        };
    }
}
