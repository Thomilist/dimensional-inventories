package net.thomilist.dimensionalinventories.util;

import com.mojang.authlib.GameProfile;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkSide;
import net.minecraft.network.packet.c2s.common.SyncedClientOptions;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ConnectedClientData;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

import java.util.UUID;

// Intended to hold data during data migrations: load the old format to this, save this to the new format
public class DummyServerPlayerEntity
    extends ServerPlayerEntity
{
    private static final String DUMMY_NAME = "TempPlayer";

    private DummyServerPlayerEntity( final MinecraftServer server, final ServerWorld world, final GameProfile profile )
    {
        super( server, world, profile, SyncedClientOptions.createDefault() );

        // Set a non-null network handler to avoid NullPointerException in ServerPlayerEntity#changeGameMode
        this.networkHandler = new ServerPlayNetworkHandler(
            server,
            new ClientConnection( NetworkSide.CLIENTBOUND ),
            this,
            ConnectedClientData.createDefault( this.getGameProfile(), false )
        );
    }

    private DummyServerPlayerEntity( final ServerWorld world, final GameProfile profile )
    {
        this( world.getServer(), world, profile );
    }

    private DummyServerPlayerEntity( final MinecraftServer server, final GameProfile profile )
    {
        this( server, server.getOverworld(), profile );
    }

    public DummyServerPlayerEntity( final ServerWorld world, final UUID uuid )
    {
        this( world, new GameProfile( uuid, DummyServerPlayerEntity.DUMMY_NAME ) );
    }

    public DummyServerPlayerEntity( final MinecraftServer server, final UUID uuid )
    {
        this( server, new GameProfile( uuid, DummyServerPlayerEntity.DUMMY_NAME ) );
    }

    public DummyServerPlayerEntity( final ServerWorld world, final String uuid )
    {
        this( world, UUID.fromString( uuid ) );
    }

    public DummyServerPlayerEntity( final MinecraftServer server, final String uuid )
    {
        this( server, UUID.fromString( uuid ) );
    }

    public DummyServerPlayerEntity( final MinecraftServer server )
    {
        this( server, UUID.randomUUID() );
    }

    public DummyServerPlayerEntity( final ServerWorld world )
    {
        this( world, UUID.randomUUID() );
    }
}
