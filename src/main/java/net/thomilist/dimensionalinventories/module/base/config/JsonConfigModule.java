package net.thomilist.dimensionalinventories.module.base.config;

import net.thomilist.dimensionalinventories.module.base.JsonModule;

import java.nio.file.Path;

public interface JsonConfigModule<T extends ConfigModuleState>
    extends StatefulConfigModule<T>, JsonModule<T>
{
    void loadFromOther( T other );

    default Path saveFile()
    {
        return this.saveDirectory().resolve( this.saveFileName() );
    }

    @Override
    default void load()
    {
        final Path saveFile = this.saveFile();
        final T config = this.load( saveFile );
        this.loadFromOther( config );
    }

    @Override
    default void save()
    {
        final Path saveFile = this.saveFile();
        this.save( saveFile, this.state() );
    }
}
