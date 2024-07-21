package net.thomilist.dimensionalinventories.module.builtin.legacy.status;

import net.minecraft.server.network.ServerPlayerEntity;
import net.thomilist.dimensionalinventories.lostandfound.LostAndFound;
import net.thomilist.dimensionalinventories.module.base.ModuleBase;
import net.thomilist.dimensionalinventories.module.builtin.legacy.ModuleHelper_SV1;
import net.thomilist.dimensionalinventories.module.version.StorageVersion;
import net.thomilist.dimensionalinventories.module.base.player.StatefulPlayerModule;
import net.thomilist.dimensionalinventories.module.builtin.inventory.InventoryModuleState;
import net.thomilist.dimensionalinventories.module.builtin.inventory.InventorySection;
import net.thomilist.dimensionalinventories.module.builtin.status.StatusModuleState;
import net.thomilist.dimensionalinventories.module.builtin.pool.DimensionPool;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Deprecated
public class StatusModule_SV1
    extends ModuleBase
    implements StatefulPlayerModule<StatusModuleState>
{
    static final StatusModuleState STATE = new StatusModuleState();

    public StatusModule_SV1(
        StorageVersion[] storageVersions,
        String groupId,
        String moduleId,
        String description)
    {
        super(storageVersions, groupId, moduleId, description);
    }

    @Override
    public StatusModuleState newInstance(ServerPlayerEntity player)
    {
        return new StatusModuleState();
    }

    @Override
    public StatusModuleState state()
    {
        return StatusModule_SV1.STATE;
    }

    @Override
    public StatusModuleState defaultState()
    {
        return new StatusModuleState();
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
            LostAndFound.log("Failed to read status data file", e);
            return;
        }

        int lineIndex = 0;

        // For counting lines to skip
        final InventoryModuleState inventoryModuleState = new InventoryModuleState();

        for (InventorySection label : InventorySection.list())
        {
            lineIndex += inventoryModuleState.section(label).size();
        }

        final StatusModuleState statusModuleState = new StatusModuleState();

        statusModuleState.experiencePoints = Integer.parseInt(lines.get(lineIndex++));
        statusModuleState.score = Integer.parseInt(lines.get(lineIndex++));
        statusModuleState.foodLevel = Integer.parseInt(lines.get(lineIndex++));
        statusModuleState.saturationLevel = Float.parseFloat(lines.get(lineIndex++));
        statusModuleState.exhaustion = Float.parseFloat(lines.get(lineIndex++));
        statusModuleState.health = Float.parseFloat(lines.get(lineIndex++));

        statusModuleState.applyToPlayer(player);
    }

    @Override
    public void save(ServerPlayerEntity player, DimensionPool dimensionPool)
    {
        // Intentionally not implemented
    }
}
