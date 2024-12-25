package net.thomilist.dimensionalinventories.module;

import net.thomilist.dimensionalinventories.DimensionalInventories;
import net.thomilist.dimensionalinventories.exception.ModuleNotRegisteredException;
import net.thomilist.dimensionalinventories.module.base.Module;
import net.thomilist.dimensionalinventories.module.version.StorageVersion;
import net.thomilist.dimensionalinventories.util.StringHelper;

import java.util.HashMap;
import java.util.SortedSet;
import java.util.TreeSet;

public final class ModuleRegistry<T extends Module>
{
    private final HashMap<StorageVersion, SortedSet<T>> modules = new HashMap<>();
    private final Class<T> moduleType;

    public ModuleRegistry(Class<T> moduleType)
    {
        this.moduleType = moduleType;
    }

    private void register(T module)
    {
        for (StorageVersion storageVersion : module.storageVersions())
        {
            modules.putIfAbsent(storageVersion, new TreeSet<>());

            if (!modules.get(storageVersion).add(module))
            {
                DimensionalInventories.LOGGER.warn(
                    "Failed to register module: {} has already been registered",
                    StringHelper.joinAndWrapScopes(module.groupId(), module.moduleId())
                );

                continue;
            }

            DimensionalInventories.LOGGER.info(
                "Registered {} module {}",
                module.category(),
                StringHelper.joinAndWrapScopes(storageVersion.toString(), module.groupId(), module.moduleId())
            );
        }
    }

    public void register(ModuleGroup moduleGroup)
        throws InvalidIdentifierException
    {
        if (!ModuleRegistry.isValidId(moduleGroup.groupId()))
        {
            throw new InvalidIdentifierException(
                "'%s' is not a valid module group ID"
                    .formatted(moduleGroup.groupId())
            );
        }

        for (Module module : moduleGroup.modules)
        {
            if (moduleType.isInstance(module))
            {
                register(moduleType.cast(module));
            }
        }
    }

    public boolean has(StorageVersion storageVersion)
    {
        return modules.containsKey(storageVersion);
    }

    public SortedSet<T> get(StorageVersion storageVersion)
    {
        return modules.getOrDefault(storageVersion, new TreeSet<>());
    }

    public <M extends T> M get(Class<M> moduleType)
        throws ModuleNotRegisteredException
    {
        for (var moduleSet : modules.values())
        {
            for (var module : moduleSet)
            {
                if (moduleType.isInstance(module))
                {
                    return moduleType.cast(module);
                }
            }
        }

        throw new ModuleNotRegisteredException(moduleType);
    }

    public static boolean isValidId(String groupId)
    {
        return groupId.matches("^[a-z]+[a-z0-9_-]*[a-z0-9]*$");
    }
}
