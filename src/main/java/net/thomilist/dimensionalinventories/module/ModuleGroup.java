package net.thomilist.dimensionalinventories.module;

import net.minecraft.util.InvalidIdentifierException;
import net.thomilist.dimensionalinventories.DimensionalInventoriesMod;
import net.thomilist.dimensionalinventories.exception.InvalidModuleException;
import net.thomilist.dimensionalinventories.exception.ModuleConstructionException;
import net.thomilist.dimensionalinventories.module.base.Module;
import net.thomilist.dimensionalinventories.module.base.ModuleBase;
import net.thomilist.dimensionalinventories.module.base.config.ConfigModule;
import net.thomilist.dimensionalinventories.module.base.player.PlayerModule;
import net.thomilist.dimensionalinventories.module.version.StorageVersion;
import net.thomilist.dimensionalinventories.util.LogHelper;

import java.util.SortedSet;
import java.util.TreeSet;

public class ModuleGroup
{
    final private String groupId;
    final SortedSet<Module> modules = new TreeSet<>();

    protected ModuleGroup(String groupId)
    {
        this.groupId = groupId;
    }

    public static ModuleGroup create(String groupId)
        throws InvalidIdentifierException
    {
        if (!ModuleRegistry.isValidId(groupId))
        {
            throw new InvalidIdentifierException("'" + groupId + "' is not a valid group ID");
        }

        return new ModuleGroup(groupId);
    }

    public String groupId()
    {
        return this.groupId;
    }

    protected void add(Module module)
    {
        if (!modules.add(module))
        {
            DimensionalInventoriesMod.LOGGER.warn("Failed to add module: {} has already been registered",
                LogHelper.joinAndWrapScopes(module.groupId(), module.moduleId()));
        }
    }

    public <T extends Module> ModuleGroup add(
        Class<T> moduleType,
        StorageVersion[] storageVersions,
        String moduleId,
        String description)
        throws InvalidIdentifierException, InvalidModuleException, ModuleConstructionException
    {
        if (!ModuleRegistry.isValidId(moduleId))
        {
            throw new InvalidIdentifierException("'" + moduleId + "' is not a valid module ID");
        }

        Module module = ModuleBase.createDerived(moduleType, storageVersions, groupId, moduleId, description);

        if (module instanceof ConfigModule || module instanceof PlayerModule)
        {
            add(module);
        }
        else
        {
            throw new InvalidModuleException(moduleType, groupId, moduleId);
        }

        return this;
    }
}
