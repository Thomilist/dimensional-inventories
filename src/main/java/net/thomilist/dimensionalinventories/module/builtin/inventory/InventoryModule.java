package net.thomilist.dimensionalinventories.module.builtin.inventory;

import com.google.gson.Gson;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.thomilist.dimensionalinventories.module.base.JsonModule;
import net.thomilist.dimensionalinventories.module.base.ModuleBase;
import net.thomilist.dimensionalinventories.module.version.StorageVersion;
import net.thomilist.dimensionalinventories.module.base.player.JsonPlayerModule;

public final class InventoryModule
    extends ModuleBase
    implements JsonPlayerModule<InventoryModuleState>
{
    private static final String MODULE_ID = "inventory";
    private static final String DESCRIPTION =
        "Items in inventory, hotbar, offhand & armour slots.";

    private static final StorageVersion[] STORAGE_VERSIONS =
    {
        StorageVersion.V2
    };

    private static final Gson GSON = JsonModule.GSON_BUILDER
        .registerTypeAdapter(ItemStack.class, new ItemStackSerializerPair())
        .registerTypeAdapter(ItemStackListSerializerPair.TYPE, new ItemStackListSerializerPair())
        .registerTypeAdapter(InventoryModuleState.class, new InventoryModuleStateSerializerPair())
        .create();

    private final InventoryModuleState state = new InventoryModuleState();

    public InventoryModule(String groupId)
    {
        super(
            InventoryModule.STORAGE_VERSIONS,
            groupId,
            InventoryModule.MODULE_ID,
            InventoryModule.DESCRIPTION
        );
    }

    @Override
    public InventoryModuleState newInstance(ServerPlayerEntity player)
    {
        return new InventoryModuleState(player);
    }

    @Override
    public InventoryModuleState state()
    {
        return InventoryModule.STATE;
    }

    @Override
    public InventoryModuleState defaultState()
    {
        return new InventoryModuleState();
    }

    @Override
    public Gson gson()
    {
        return InventoryModule.GSON;
    }
}
