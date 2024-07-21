package net.thomilist.dimensionalinventories.module.builtin.legacy.inventory;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.collection.DefaultedList;
import net.thomilist.dimensionalinventories.lostandfound.LostAndFound;
import net.thomilist.dimensionalinventories.module.base.ModuleBase;
import net.thomilist.dimensionalinventories.module.builtin.legacy.ModuleHelper_SV1;
import net.thomilist.dimensionalinventories.module.version.StorageVersion;
import net.thomilist.dimensionalinventories.module.base.player.StatefulPlayerModule;
import net.thomilist.dimensionalinventories.module.builtin.inventory.InventoryModuleState;
import net.thomilist.dimensionalinventories.module.builtin.inventory.InventorySection;
import net.thomilist.dimensionalinventories.module.builtin.pool.DimensionPool;
import net.thomilist.dimensionalinventories.util.ItemStackListHelper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Deprecated
public class InventoryModule_SV1
    extends ModuleBase
    implements StatefulPlayerModule<InventoryModuleState>
{
    static final InventoryModuleState STATE = new InventoryModuleState();

    public InventoryModule_SV1(
        StorageVersion[] storageVersions,
        String groupId,
        String moduleId,
        String description)
    {
        super(storageVersions, groupId, moduleId, description);
    }

    @Override
    public InventoryModuleState newInstance(ServerPlayerEntity player)
    {
        return new InventoryModuleState();
    }

    @Override
    public InventoryModuleState state()
    {
        return InventoryModule_SV1.STATE;
    }

    @Override
    public InventoryModuleState defaultState()
    {
        return new InventoryModuleState();
    }

    @Override
    public void load(ServerPlayerEntity player, DimensionPool dimensionPool)
    {
        final Path saveFile = ModuleHelper_SV1.saveFile(dimensionPool, player);
        List<String> lines;

        try
        {
            lines = Files.readAllLines(saveFile);
        }
        catch (IOException e)
        {
            LostAndFound.log("Failed to read inventory data file", e);
            return;
        }

        int lineIndex = 0;

        final InventoryModuleState inventoryModuleState = new InventoryModuleState();

        for (InventorySection label : InventorySection.list())
        {
            try (var LAF = LostAndFound.push(label))
            {
                DefaultedList<ItemStack> items =
                    DefaultedList.ofSize(inventoryModuleState.section(label).size(), ItemStack.EMPTY);

                for (int i = 0; i < items.size(); i++)
                {
                    NbtCompound nbt;

                    try
                    {
                        nbt = NbtHelper.fromNbtProviderString(lines.get(lineIndex++));
                    }
                    catch (CommandSyntaxException e)
                    {
                        LostAndFound.log("Failed to parse NBT string for " + label + "[" + i + "]", e);
                        return;
                    }

                    items.set(i, ItemStack.fromNbt(nbt));
                }

                if (!items.isEmpty())
                {
                    ItemStackListHelper.assignItemStacks(items, inventoryModuleState.section(label));
                }
            }
        }

        inventoryModuleState.applyToPlayer(player);
    }

    @Override
    public void save(ServerPlayerEntity player, DimensionPool dimensionPool)
    {
        // Intentionally not implemented
    }
}
