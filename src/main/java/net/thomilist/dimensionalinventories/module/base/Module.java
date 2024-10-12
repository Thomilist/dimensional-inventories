package net.thomilist.dimensionalinventories.module.base;

import net.thomilist.dimensionalinventories.lostandfound.LostAndFoundFormattable;
import net.thomilist.dimensionalinventories.module.version.StorageVersion;
import net.thomilist.dimensionalinventories.util.StringHelper;
import org.jetbrains.annotations.NotNull;

import java.util.SortedSet;

public interface Module
    extends Comparable<Module>, LostAndFoundFormattable
{
    String groupId();
    String moduleId();
    String description();

    SortedSet<StorageVersion> storageVersions();

    default int moduleVersion()
    {
        return 1;
    }

    default StorageVersion latestStorageVersion()
    {
        return storageVersions().last();
    }

    @Override
    default int compareTo(@NotNull Module other)
    {
        if (!this.groupId().equals(other.groupId()))
        {
            return this.groupId().compareTo(other.groupId());
        }
        else if (!this.moduleId().equals(other.moduleId()))
        {
            return this.moduleId().compareTo(other.moduleId());
        }
        else
        {
            return StorageVersion.compareSets(this.storageVersions(), other.storageVersions());
        }
    }

    @Override
    default String toLostAndFoundScopeString()
    {
        return StringHelper.joinScopes(this.groupId(), this.moduleId());
    }
}
