package net.thomilist.dimensionalinventories.compatibility;

import net.minecraft.server.MinecraftServer;

public interface CompatWrapper
{
    default void onServerStarted( final MinecraftServer server )
    { }
}
