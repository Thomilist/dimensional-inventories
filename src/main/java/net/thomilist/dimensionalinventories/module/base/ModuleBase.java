package net.thomilist.dimensionalinventories.module.base;

import net.thomilist.dimensionalinventories.exception.ModuleConstructionException;
import net.thomilist.dimensionalinventories.module.version.StorageVersion;

import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

public class ModuleBase
    implements Module
{
    private final String groupId;
    private final String moduleId;
    private final String description;
    private final SortedSet<StorageVersion> storageVersions = new TreeSet<>();

    protected ModuleBase(
        StorageVersion[] storageVersions,
        String groupId,
        String moduleId,
        String description)
    {
        this.groupId = groupId;
        this.moduleId = moduleId;
        this.description = description;
        this.storageVersions.addAll(List.of(storageVersions));
    }

    public static <T extends Module> T createDerived(
        Class<T> moduleType,
        StorageVersion[] storageVersions,
        String groupId,
        String moduleId,
        String description)
        throws ModuleConstructionException
    {
        try
        {
            return moduleType
                .getConstructor(StorageVersion[].class, String.class, String.class, String.class)
                .newInstance(storageVersions, groupId, moduleId, description);
        }
        catch (Exception e)
        {
            throw new ModuleConstructionException(moduleType, groupId, moduleId, e);
        }
    }

    @Override
    public String groupId()
    {
        return this.groupId;
    }

    @Override
    public String moduleId()
    {
        return this.moduleId;
    }

    @Override
    public String description()
    {
        return this.description;
    }

    @Override
    public SortedSet<StorageVersion> storageVersions()
    {
        return storageVersions;
    }
}
