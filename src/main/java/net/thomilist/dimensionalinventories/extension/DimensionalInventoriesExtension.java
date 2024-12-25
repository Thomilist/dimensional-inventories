package net.thomilist.dimensionalinventories.extension;

import net.fabricmc.api.ModInitializer;
import net.thomilist.dimensionalinventories.DimensionalInventories;
import net.thomilist.dimensionalinventories.lostandfound.LostAndFound;
import net.thomilist.dimensionalinventories.lostandfound.LostAndFoundContext;
import net.thomilist.dimensionalinventories.module.ModuleGroup;

public abstract class DimensionalInventoriesExtension
    implements ModInitializer
{
    private final String extensionId;
    private final ModuleGroup[] moduleGroups;

    protected DimensionalInventoriesExtension(final String extensionId, final ModuleGroup... moduleGroups)
    {
        this.extensionId = extensionId;
        this.moduleGroups = moduleGroups;
    }

    @Override
    public void onInitialize()
    {
        try (final LostAndFoundContext LAF = LostAndFound.init("init", "extension", this.extensionId))
        {
            for (final ModuleGroup moduleGroup : this.moduleGroups)
            {
                DimensionalInventories.INSTANCE.registerModules(moduleGroup);
            }
        }
    }
}
