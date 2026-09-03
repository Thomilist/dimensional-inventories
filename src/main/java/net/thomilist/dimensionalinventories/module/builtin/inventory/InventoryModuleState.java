package net.thomilist.dimensionalinventories.module.builtin.inventory;

import net.minecraft.core.NonNullList;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.thomilist.dimensionalinventories.compatibility.Compat;
import net.thomilist.dimensionalinventories.module.base.player.PlayerModuleState;
import net.thomilist.dimensionalinventories.util.ItemStackListHelper;

import java.lang.reflect.Type;

public class InventoryModuleState
    implements PlayerModuleState
{
    private static final int ARMOR_SIZE = 4;
    private static final int OFF_HAND_SIZE = 1;
    private static final int DEFAULT_ENDER_CHEST_SIZE = 27;

    public NonNullList<ItemStack> armor;
    public NonNullList<ItemStack> main;
    public NonNullList<ItemStack> offHand;
    public NonNullList<ItemStack> enderChest;

    private InventoryModuleState( final int armor_size,
                                  final int main_size,
                                  final int off_hand_size,
                                  final int ender_chest_size )
    {
        this.armor = NonNullList.withSize( armor_size, ItemStack.EMPTY );
        this.main = NonNullList.withSize( main_size, ItemStack.EMPTY );
        this.offHand = NonNullList.withSize( off_hand_size, ItemStack.EMPTY );
        this.enderChest = NonNullList.withSize( ender_chest_size, ItemStack.EMPTY );
    }

    public InventoryModuleState()
    {
        this(
            InventoryModuleState.ARMOR_SIZE,
            Inventory.INVENTORY_SIZE,
            InventoryModuleState.OFF_HAND_SIZE,
            InventoryModuleState.DEFAULT_ENDER_CHEST_SIZE
        );
    }

    public InventoryModuleState( final ServerPlayer player )
    {
        this(
            InventoryModuleState.ARMOR_SIZE,
            Inventory.INVENTORY_SIZE,
            InventoryModuleState.OFF_HAND_SIZE,
            player.getEnderChestInventory().getContainerSize()
        );
        this.loadFromPlayer( player );
    }

    @Override
    public Type type()
    {
        return InventoryModuleState.class;
    }

    @Override
    public void applyToPlayer( final ServerPlayer player )
    {
        Compat.PLAYER_INVENTORY.setArmor( player.getInventory(), this.armor );
        Compat.PLAYER_INVENTORY.setMain( player.getInventory(), this.main );
        Compat.PLAYER_INVENTORY.setOffHand( player.getInventory(), this.offHand );
        Compat.SIMPLE_INVENTORY.setHeldStacks( player.getEnderChestInventory(), this.enderChest );
    }

    @Override
    public void loadFromPlayer( final ServerPlayer player )
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

    public NonNullList<ItemStack> section( final InventorySection label )
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
