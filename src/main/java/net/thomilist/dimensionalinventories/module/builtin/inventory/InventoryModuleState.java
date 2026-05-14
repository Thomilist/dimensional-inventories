package net.thomilist.dimensionalinventories.module.builtin.inventory;

import net.minecraft.core.NonNullList;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.PlayerEnderChestContainer;
import net.minecraft.world.item.ItemStack;
import net.thomilist.dimensionalinventories.compatibility.Compat;
import net.thomilist.dimensionalinventories.module.base.player.PlayerModuleState;
import net.thomilist.dimensionalinventories.util.ItemStackListHelper;

import java.lang.reflect.Type;

public class InventoryModuleState
    implements PlayerModuleState
{
    private static final int ARMOR_SIZE = 4;
    private static final int MAIN_SIZE = Inventory.INVENTORY_SIZE;
    private static final int OFF_HAND_SIZE = 1;
    private static final int ENDER_CHEST_SIZE = new PlayerEnderChestContainer().getContainerSize();

    public final NonNullList<ItemStack> armor = NonNullList.withSize(
        InventoryModuleState.ARMOR_SIZE,
        ItemStack.EMPTY
    );
    public final NonNullList<ItemStack> main = NonNullList.withSize(
        InventoryModuleState.MAIN_SIZE,
        ItemStack.EMPTY
    );
    public final NonNullList<ItemStack> offHand = NonNullList.withSize(
        InventoryModuleState.OFF_HAND_SIZE,
        ItemStack.EMPTY
    );
    public final NonNullList<ItemStack> enderChest = NonNullList.withSize(
        InventoryModuleState.ENDER_CHEST_SIZE,
        ItemStack.EMPTY
    );

    public InventoryModuleState()
    { }

    public InventoryModuleState( final ServerPlayer player )
    {
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
