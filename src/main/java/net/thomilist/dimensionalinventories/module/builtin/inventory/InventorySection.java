package net.thomilist.dimensionalinventories.module.builtin.inventory;

import java.util.List;

public enum InventorySection
{
    ARMOR ("armor"),
    MAIN ("main"),
    OFF_HAND ("offHand"),
    ENDER_CHEST ("enderChest");

    public final String label;

    InventorySection(String label)
    {
        this.label = label;
    }

    public String toString()
    {
        return this.label;
    }

    public static List<InventorySection> list()
    {
        return List.of(
            InventorySection.ARMOR,
            InventorySection.MAIN,
            InventorySection.OFF_HAND,
            InventorySection.ENDER_CHEST
        );
    }
}
