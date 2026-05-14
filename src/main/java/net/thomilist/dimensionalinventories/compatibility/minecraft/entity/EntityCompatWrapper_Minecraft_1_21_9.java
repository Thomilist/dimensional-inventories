package net.thomilist.dimensionalinventories.compatibility.minecraft.entity;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

public class EntityCompatWrapper_Minecraft_1_21_9
    implements EntityCompatWrapper
{
    @Override
    public Level getWorld( final Entity entity )
    {
        return entity.level();
    }

    @Override
    public ServerLevel getWorld( final ServerPlayer player )
    {
        return player.level();
    }
}
