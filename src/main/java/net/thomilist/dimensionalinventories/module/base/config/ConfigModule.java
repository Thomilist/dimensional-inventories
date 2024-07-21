package net.thomilist.dimensionalinventories.module.base.config;

import net.thomilist.dimensionalinventories.lostandfound.LostAndFound;
import net.thomilist.dimensionalinventories.module.base.Module;

public interface ConfigModule
    extends Module
{
    void load();
    void save();

    default void loadWithContext()
    {
        try (var LAF = LostAndFound.push(this, "load"))
        {
            load();
        }
    }

    default void saveWithContext()
    {
        try (var LAF = LostAndFound.push(this, "save"))
        {
            save();
        }
    }
}
