package net.thomilist.dimensionalinventories.compatibility.minecraft.inventory;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import net.thomilist.dimensionalinventories.compatibility.LimitedCompatibility;
import net.thomilist.dimensionalinventories.util.ItemStackListHelper;

@LimitedCompatibility( target = "Minecraft",
                       versions = ">=1.21.5" )
public class PlayerInventoryCompatWrapper_Minecraft_1_21_5
    implements PlayerInventoryCompatWrapper
{
    @Override
    public DefaultedList<ItemStack> getArmor( final PlayerInventory playerInventory )
    {
        return DefaultedList.copyOf(
            ItemStack.EMPTY,
            playerInventory.player.getEquippedStack( EquipmentSlot.FEET ),
            playerInventory.player.getEquippedStack( EquipmentSlot.LEGS ),
            playerInventory.player.getEquippedStack( EquipmentSlot.CHEST ),
            playerInventory.player.getEquippedStack( EquipmentSlot.HEAD )
        );
    }

    @Override
    public void setArmor( final PlayerInventory playerInventory, final DefaultedList<ItemStack> itemStacks )
    {
        playerInventory.player.equipStack( EquipmentSlot.FEET, itemStacks.get( 0 ) );
        playerInventory.player.equipStack( EquipmentSlot.LEGS, itemStacks.get( 1 ) );
        playerInventory.player.equipStack( EquipmentSlot.CHEST, itemStacks.get( 2 ) );
        playerInventory.player.equipStack( EquipmentSlot.HEAD, itemStacks.get( 3 ) );
    }

    @Override
    public DefaultedList<ItemStack> getMain( final PlayerInventory playerInventory )
    {
        return playerInventory.getMainStacks();
    }

    @Override
    public void setMain( final PlayerInventory playerInventory, final DefaultedList<ItemStack> itemStacks )
    {
        ItemStackListHelper.assignItemStacks( itemStacks, playerInventory.getMainStacks() );
    }

    @Override
    public DefaultedList<ItemStack> getOffHand( final PlayerInventory playerInventory )
    {
        return DefaultedList.copyOf( ItemStack.EMPTY, playerInventory.player.getEquippedStack( EquipmentSlot.OFFHAND ) );
    }

    @Override
    public void setOffHand( final PlayerInventory playerInventory, final DefaultedList<ItemStack> itemStacks )
    {
        playerInventory.player.equipStack( EquipmentSlot.OFFHAND, itemStacks.getFirst() );
    }
}
