package net.thomilist.dimensionalinventories.compatibility.minecraft.inventory;

import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.thomilist.dimensionalinventories.compatibility.LimitedCompatibility;
import net.thomilist.dimensionalinventories.util.ItemStackListHelper;

@LimitedCompatibility( target = "Minecraft",
                       versions = ">=1.21.5" )
public class PlayerInventoryCompatWrapper_Minecraft_1_21_5
    implements PlayerInventoryCompatWrapper
{
    @Override
    public NonNullList<ItemStack> getArmor( final Inventory playerInventory )
    {
        return NonNullList.of(
            ItemStack.EMPTY,
            playerInventory.player.getItemBySlot( EquipmentSlot.FEET ),
            playerInventory.player.getItemBySlot( EquipmentSlot.LEGS ),
            playerInventory.player.getItemBySlot( EquipmentSlot.CHEST ),
            playerInventory.player.getItemBySlot( EquipmentSlot.HEAD )
        );
    }

    @Override
    public void setArmor( final Inventory playerInventory, final NonNullList<ItemStack> itemStacks )
    {
        playerInventory.player.setItemSlot( EquipmentSlot.FEET, itemStacks.get( 0 ) );
        playerInventory.player.setItemSlot( EquipmentSlot.LEGS, itemStacks.get( 1 ) );
        playerInventory.player.setItemSlot( EquipmentSlot.CHEST, itemStacks.get( 2 ) );
        playerInventory.player.setItemSlot( EquipmentSlot.HEAD, itemStacks.get( 3 ) );
    }

    @Override
    public NonNullList<ItemStack> getMain( final Inventory playerInventory )
    {
        return playerInventory.getNonEquipmentItems();
    }

    @Override
    public void setMain( final Inventory playerInventory, final NonNullList<ItemStack> itemStacks )
    {
        ItemStackListHelper.assignItemStacks( itemStacks, playerInventory.getNonEquipmentItems() );
    }

    @Override
    public NonNullList<ItemStack> getOffHand( final Inventory playerInventory )
    {
        return NonNullList.of( ItemStack.EMPTY, playerInventory.player.getItemBySlot( EquipmentSlot.OFFHAND ) );
    }

    @Override
    public void setOffHand( final Inventory playerInventory, final NonNullList<ItemStack> itemStacks )
    {
        playerInventory.player.setItemSlot( EquipmentSlot.OFFHAND, itemStacks.getFirst() );
    }
}
