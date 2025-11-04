package net.thomilist.dimensionalinventories.module.builtin.gamemode;

import net.minecraft.server.network.ServerPlayerEntity;
import net.thomilist.dimensionalinventories.module.base.ModuleBase;
import net.thomilist.dimensionalinventories.module.base.player.PlayerModule;
import net.thomilist.dimensionalinventories.module.builtin.pool.DimensionPool;
import net.thomilist.dimensionalinventories.module.version.StorageVersion;

public final class GameModeModule
    extends ModuleBase
    implements PlayerModule
{
    private static final String MODULE_ID = "gamemode";
    private static final String DESCRIPTION = "Apply dimension pool game mode setting.";

    private static final StorageVersion[] STORAGE_VERSIONS = {
        StorageVersion.V1, StorageVersion.V2
    };

    public GameModeModule( final String groupId )
    {
        super( GameModeModule.STORAGE_VERSIONS, groupId, GameModeModule.MODULE_ID, GameModeModule.DESCRIPTION );
    }

    @Override
    public void load( final ServerPlayerEntity player, final DimensionPool dimensionPool )
    {
        player.changeGameMode( dimensionPool.getGameMode() );
    }

    @Override
    public void save( final ServerPlayerEntity player, final DimensionPool dimensionPool )
    {
        // Intentionally empty; the game mode state belongs to the dimension pool, not the player
    }
}
