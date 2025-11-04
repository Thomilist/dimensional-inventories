package net.thomilist.dimensionalinventories.extension.builtin;

import net.thomilist.dimensionalinventories.DimensionalInventories;
import net.thomilist.dimensionalinventories.extension.DimensionalInventoriesExtension;
import net.thomilist.dimensionalinventories.module.builtin.MainModuleGroup;

public final class DimensionalInventoriesExtensionMain
    extends DimensionalInventoriesExtension
{
    public DimensionalInventoriesExtensionMain()
    {
        super( DimensionalInventories.PROPERTIES.id(), new MainModuleGroup() );
    }
}
