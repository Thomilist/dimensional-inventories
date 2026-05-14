package net.thomilist.dimensionalinventories.module.base.player;

import net.minecraft.server.level.ServerPlayer;
import net.thomilist.dimensionalinventories.module.base.ModuleState;

public interface PlayerModuleState
    extends ModuleState
{
    void applyToPlayer( ServerPlayer player );

    void loadFromPlayer( ServerPlayer player );
}
