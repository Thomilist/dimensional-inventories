package net.thomilist.dimensionalinventories.module.base.config;

import net.thomilist.dimensionalinventories.module.base.StatefulModule;
import net.thomilist.dimensionalinventories.util.SavePaths;

import java.nio.file.Path;

public interface StatefulConfigModule<T extends ConfigModuleState>
    extends ConfigModule, StatefulModule<T>
{
    T newInstance();

    default Path saveDirectory()
    {
        return SavePaths.configDirectory(latestStorageVersion(), groupId());
    }
}
