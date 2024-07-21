package net.thomilist.dimensionalinventories.module.builtin.gamemode;

import net.minecraft.server.network.ServerPlayerEntity;
import net.thomilist.dimensionalinventories.module.base.ModuleBase;
import net.thomilist.dimensionalinventories.module.version.StorageVersion;
import net.thomilist.dimensionalinventories.module.base.player.PlayerModule;
import net.thomilist.dimensionalinventories.module.builtin.pool.DimensionPool;

public class GameModeModule
    extends ModuleBase
    implements PlayerModule
{
    public GameModeModule(
        StorageVersion[] storageVersions,
        String groupId,
        String moduleId,
        String description)
    {
        super(storageVersions, groupId, moduleId, description);
    }

    @Override
    public void load(ServerPlayerEntity player, DimensionPool dimensionPool)
    {
        player.changeGameMode(dimensionPool.getGameMode());
    }

    @Override
    public void save(ServerPlayerEntity player, DimensionPool dimensionPool)
    {
        // Intentionally empty; the game mode state belongs to the dimension pool, not the player
    }
}
