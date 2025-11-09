package net.thomilist.dimensionalinventories.compatibility.minecraft.entity;

import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;

public class EntityCompatWrapper_Minecraft_1_21_9
    implements EntityCompatWrapper
{
    @Override
    public World getWorld( final Entity entity )
    {
        return entity.getEntityWorld();
    }

    @Override
    public ServerWorld getWorld( final ServerPlayerEntity player )
    {
        return player.getEntityWorld();
    }
}
