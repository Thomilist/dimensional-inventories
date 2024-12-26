package net.thomilist.dimensionalinventories.module.builtin;

import net.thomilist.dimensionalinventories.module.ModuleGroup;
import net.thomilist.dimensionalinventories.module.builtin.gamemode.GameModeModule;
import net.thomilist.dimensionalinventories.module.builtin.inventory.InventoryModule;
import net.thomilist.dimensionalinventories.module.builtin.legacy.inventory.InventoryModule_SV1;
import net.thomilist.dimensionalinventories.module.builtin.legacy.pool.DimensionPoolConfigModule_SV1;
import net.thomilist.dimensionalinventories.module.builtin.legacy.status.StatusModule_SV1;
import net.thomilist.dimensionalinventories.module.builtin.pool.DimensionPoolConfigModule;
import net.thomilist.dimensionalinventories.module.builtin.shoulderentity.ShoulderEntityModule;
import net.thomilist.dimensionalinventories.module.builtin.status.StatusModule;

public final class MainModuleGroup
    extends ModuleGroup
{
    private static final String GROUP_ID = "main";

    public MainModuleGroup()
    {
        super( MainModuleGroup.GROUP_ID );

        this.RegisterLatestModules();
        this.RegisterLegacyModules();
    }

    private void RegisterLatestModules()
    {
        this.register(
            DimensionPoolConfigModule.class,
            GameModeModule.class,
            InventoryModule.class,
            StatusModule.class,
            ShoulderEntityModule.class
        );
    }

    @SuppressWarnings( "deprecation" )
    private void RegisterLegacyModules()
    {
        this.register( DimensionPoolConfigModule_SV1.class, InventoryModule_SV1.class, StatusModule_SV1.class );
    }
}
