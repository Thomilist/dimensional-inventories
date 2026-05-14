package net.thomilist.dimensionalinventories.module.builtin.legacy.status;

import net.minecraft.server.level.ServerPlayer;
import net.thomilist.dimensionalinventories.lostandfound.LostAndFound;
import net.thomilist.dimensionalinventories.module.base.ModuleBase;
import net.thomilist.dimensionalinventories.module.base.player.StatefulPlayerModule;
import net.thomilist.dimensionalinventories.module.builtin.inventory.InventoryModuleState;
import net.thomilist.dimensionalinventories.module.builtin.inventory.InventorySection;
import net.thomilist.dimensionalinventories.module.builtin.legacy.ModuleHelper_SV1;
import net.thomilist.dimensionalinventories.module.builtin.pool.DimensionPool;
import net.thomilist.dimensionalinventories.module.builtin.status.StatusModule;
import net.thomilist.dimensionalinventories.module.builtin.status.StatusModuleState;
import net.thomilist.dimensionalinventories.module.version.StorageVersion;
import net.thomilist.dimensionalinventories.util.ListHelper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * @deprecated This is only intended for use during migration of old data of storage version 1. When loading or saving
 * new data, use {@link StatusModule} instead.
 */
@Deprecated
public final class StatusModule_SV1
    extends ModuleBase
    implements StatefulPlayerModule<StatusModuleState>
{
    private static final String MODULE_ID = "status";
    private static final String DESCRIPTION = "Health, hunger, experience & score.";

    private static final StorageVersion[] STORAGE_VERSIONS = {
        StorageVersion.V1
    };

    private final StatusModuleState state = new StatusModuleState();

    public StatusModule_SV1( final String groupId )
    {
        super( StatusModule_SV1.STORAGE_VERSIONS, groupId, StatusModule_SV1.MODULE_ID, StatusModule_SV1.DESCRIPTION );
    }

    @Override
    public StatusModuleState newInstance( final ServerPlayer player )
    {
        return new StatusModuleState();
    }

    @Override
    public StatusModuleState state()
    {
        return this.state;
    }

    @Override
    public StatusModuleState defaultState()
    {
        return new StatusModuleState();
    }

    @Override
    public void load( final ServerPlayer player, final DimensionPool dimensionPool )
    {
        final Path saveFile = ModuleHelper_SV1.saveFile( dimensionPool, player );
        final List<String> lines;

        try
        {
            lines = Files.readAllLines( saveFile );
        }
        catch ( final IOException e )
        {
            LostAndFound.log( "Failed to read status data file", e );
            return;
        }

        int lineIndex = 0;

        // For counting lines to skip
        final InventoryModuleState inventoryModuleState = new InventoryModuleState();

        for ( final InventorySection label : InventorySection.list() )
        {
            lineIndex += inventoryModuleState.section( label ).size();
        }

        final StatusModuleState statusModuleState = new StatusModuleState();

        statusModuleState.experiencePoints = Integer.parseInt( ListHelper.getOrDefault( lines, lineIndex++, "0" ) );
        statusModuleState.score = Integer.parseInt( ListHelper.getOrDefault( lines, lineIndex++, "0" ) );
        statusModuleState.foodLevel = Integer.parseInt( ListHelper.getOrDefault( lines, lineIndex++, "20" ) );
        statusModuleState.saturationLevel = Float.parseFloat( ListHelper.getOrDefault( lines, lineIndex++, "5.0" ) );
        statusModuleState.exhaustion = Float.parseFloat( ListHelper.getOrDefault( lines, lineIndex++, "0.0" ) );
        statusModuleState.health = Float.parseFloat( ListHelper.getOrDefault( lines, lineIndex++, "20.0" ) );

        statusModuleState.applyToPlayer( player );
    }

    @Override
    public void save( final ServerPlayer player, final DimensionPool dimensionPool )
    {
        // Intentionally not implemented
        ModuleHelper_SV1.ThrowOnDeprecatedSave( StatusModule.class );
    }
}
