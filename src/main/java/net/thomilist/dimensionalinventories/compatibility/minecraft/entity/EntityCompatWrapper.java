package net.thomilist.dimensionalinventories.compatibility.minecraft.entity;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.thomilist.dimensionalinventories.compatibility.CompatWrapper;

public interface EntityCompatWrapper
    extends CompatWrapper
{
    Level getWorld( Entity entity );

    ServerLevel getWorld( ServerPlayer player );
}
