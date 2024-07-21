package net.thomilist.dimensionalinventories.module.builtin.inventory;

import com.google.gson.Gson;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.thomilist.dimensionalinventories.module.base.JsonModule;
import net.thomilist.dimensionalinventories.module.base.ModuleBase;
import net.thomilist.dimensionalinventories.module.version.StorageVersion;
import net.thomilist.dimensionalinventories.module.base.player.JsonPlayerModule;

public class InventoryModule
    extends ModuleBase
    implements JsonPlayerModule<InventoryModuleState>
{
    static final InventoryModuleState STATE = new InventoryModuleState();

    private static final Gson GSON = JsonModule.GSON_BUILDER
        .registerTypeAdapter(ItemStack.class, new ItemStackSerializerPair())
        .registerTypeAdapter(ItemStackListSerializerPair.TYPE, new ItemStackListSerializerPair())
        .registerTypeAdapter(InventoryModuleState.class, new InventoryModuleStateSerializerPair())
        .create();

    public InventoryModule(
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
