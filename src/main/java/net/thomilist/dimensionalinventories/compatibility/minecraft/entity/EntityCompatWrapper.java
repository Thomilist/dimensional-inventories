package net.thomilist.dimensionalinventories.compatibility.minecraft.entity;

import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import net.thomilist.dimensionalinventories.compatibility.CompatWrapper;

public interface EntityCompatWrapper
    extends CompatWrapper
{
    World getWorld( Entity entity );

    ServerWorld getWorld( ServerPlayerEntity player );
}
