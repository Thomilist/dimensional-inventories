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
        Compat.PLAYER_INVENTORY.setArmor( player.getInventory(), this.armor );
        Compat.PLAYER_INVENTORY.setMain( player.getInventory(), this.main );
        Compat.PLAYER_INVENTORY.setOffHand( player.getInventory(), this.offHand );
        Compat.SIMPLE_INVENTORY.setHeldStacks( player.getEnderChestInventory(), this.enderChest );
    }

    @Override
    public void loadFromPlayer( final ServerPlayerEntity player )
    {
        ItemStackListHelper.assignItemStacks( Compat.PLAYER_INVENTORY.getArmor( player.getInventory() ), this.armor );
        ItemStackListHelper.assignItemStacks( Compat.PLAYER_INVENTORY.getMain( player.getInventory() ), this.main );
        ItemStackListHelper.assignItemStacks(
            Compat.PLAYER_INVENTORY.getOffHand( player.getInventory() ),
            this.offHand
        );
        ItemStackListHelper.assignItemStacks(
            Compat.SIMPLE_INVENTORY.getHeldStacks( player.getEnderChestInventory() ),
            this.enderChest
        );
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
