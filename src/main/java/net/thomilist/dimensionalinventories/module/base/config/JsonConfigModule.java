package net.thomilist.dimensionalinventories.module.base.config;

import net.thomilist.dimensionalinventories.module.base.JsonModule;

import java.nio.file.Path;

public interface JsonConfigModule<T extends ConfigModuleState>
    extends StatefulConfigModule<T>, JsonModule<T>
{
    void loadFromOther(T other);

    default Path saveFile()
    {
        return saveDirectory()
            .resolve(saveFileName());
    }

    @Override
    default void load()
    {
        final Path saveFile = saveFile();
        final T config = load(saveFile);
        loadFromOther(config);
    }

    @Override
    default void save()
    {
        final Path saveFile = saveFile();
        save(saveFile, state());
    }
}
