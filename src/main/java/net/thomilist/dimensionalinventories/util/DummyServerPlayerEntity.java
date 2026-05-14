package net.thomilist.dimensionalinventories.util;

import com.mojang.authlib.GameProfile;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ClientInformation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.server.network.ServerGamePacketListenerImpl;

import java.util.UUID;

// Intended to hold data during data migrations: load the old format to this, save this to the new format
public class DummyServerPlayerEntity
    extends ServerPlayer
{
    private static final String DUMMY_NAME = "TempPlayer";

    private DummyServerPlayerEntity( final MinecraftServer server, final ServerLevel world, final GameProfile profile )
    {
        super( server, world, profile, ClientInformation.createDefault() );

        // Set a non-null network handler to avoid NullPointerException in ServerPlayerEntity#changeGameMode
        this.connection = new ServerGamePacketListenerImpl(
            server,
            new net.minecraft.network.Connection( PacketFlow.CLIENTBOUND ),
            this,
            CommonListenerCookie.createInitial( this.getGameProfile(), false )
        );
    }

    private DummyServerPlayerEntity( final ServerLevel world, final GameProfile profile )
    {
        this( world.getServer(), world, profile );
    }

    private DummyServerPlayerEntity( final MinecraftServer server, final GameProfile profile )
    {
        this( server, server.overworld(), profile );
    }

    public DummyServerPlayerEntity( final ServerLevel world, final UUID uuid )
    {
        this( world, new GameProfile( uuid, DummyServerPlayerEntity.DUMMY_NAME ) );
    }

    public DummyServerPlayerEntity( final MinecraftServer server, final UUID uuid )
    {
        this( server, new GameProfile( uuid, DummyServerPlayerEntity.DUMMY_NAME ) );
    }

    public DummyServerPlayerEntity( final ServerLevel world, final String uuid )
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

    public DummyServerPlayerEntity( final ServerLevel world )
    {
        this( world, UUID.randomUUID() );
    }
}
