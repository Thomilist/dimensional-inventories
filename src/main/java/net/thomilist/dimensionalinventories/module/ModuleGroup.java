package net.thomilist.dimensionalinventories.module;

import net.minecraft.util.InvalidIdentifierException;
import net.thomilist.dimensionalinventories.DimensionalInventories;
import net.thomilist.dimensionalinventories.exception.InvalidModuleException;
import net.thomilist.dimensionalinventories.exception.ModuleConstructionException;
import net.thomilist.dimensionalinventories.module.base.Module;
import net.thomilist.dimensionalinventories.module.base.ModuleBase;
import net.thomilist.dimensionalinventories.module.base.config.ConfigModule;
import net.thomilist.dimensionalinventories.module.base.player.PlayerModule;
import net.thomilist.dimensionalinventories.util.StringHelper;

import java.util.SortedSet;
import java.util.TreeSet;

public abstract class ModuleGroup
{
    final private String groupId;
    final SortedSet<Module> modules = new TreeSet<>();

    protected ModuleGroup(String groupId)
    {
        this.groupId = groupId;
    }

    public String groupId()
    {
        return this.groupId;
    }

    @SafeVarargs
    protected final void register(final Class<? extends Module>... moduleTypes)
        throws InvalidIdentifierException, InvalidModuleException, ModuleConstructionException
    {
        for (Class<? extends Module> moduleType : moduleTypes)
        {
            final Module module = ModuleBase.createDerived(moduleType, this.groupId);
            this.register(module);
        }
    }

    private void register(final Module... modules)
        throws InvalidIdentifierException, InvalidModuleException
    {
        for (final Module module : modules)
        {
            if (!ModuleRegistry.isValidId(module.moduleId()))
            {
                throw new InvalidIdentifierException(
                    "'%s' is not a valid module ID"
                        .formatted(module.moduleId())
                );
            }

            if (!((module instanceof ConfigModule) || (module instanceof PlayerModule)))
            {
                throw new InvalidModuleException(module.getClass(), this.groupId, module.moduleId());
            }

            if (!this.modules.add(module))
            {
                DimensionalInventories.LOGGER.warn(
                    "Failed to add module: {} has already been registered",
                    StringHelper.joinAndWrapScopes(module.groupId(), module.moduleId())
                );
            }
        }
    }
}
