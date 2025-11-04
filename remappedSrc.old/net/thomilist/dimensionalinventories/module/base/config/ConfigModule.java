package net.thomilist.dimensionalinventories.module.base.config;

import net.thomilist.dimensionalinventories.lostandfound.LostAndFound;
import net.thomilist.dimensionalinventories.lostandfound.LostAndFoundScope;
import net.thomilist.dimensionalinventories.module.base.Module;

public interface ConfigModule
    extends Module
{
    void load();

    void save();

    @Override
    default String category()
    {
        return "config";
    }

    @Override
    default String toLostAndFoundScopeString()
    {
        return Module.super.toLostAndFoundScopeString() + " (config module)";
    }

    default void loadWithContext()
    {
        try ( final LostAndFoundScope LAF = LostAndFound.push( this, "load" ) )
        {
            this.load();
        }
    }

    default void saveWithContext()
    {
        try ( final LostAndFoundScope LAF = LostAndFound.push( this, "save" ) )
        {
            this.save();
        }
    }
}
