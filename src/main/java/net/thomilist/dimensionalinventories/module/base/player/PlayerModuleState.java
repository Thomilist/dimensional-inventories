package net.thomilist.dimensionalinventories.module.base.player;

import net.minecraft.server.network.ServerPlayerEntity;
import net.thomilist.dimensionalinventories.module.base.ModuleState;

public interface PlayerModuleState
    extends ModuleState
{
    void applyToPlayer(ServerPlayerEntity player);
    void loadFromPlayer(ServerPlayerEntity player);
}
