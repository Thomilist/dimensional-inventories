package net.thomilist.dimensionalinventories.util;

import com.mojang.authlib.GameProfile;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

import java.util.UUID;

// Intended to hold data during data migrations: load the old format to this, save this to the new format
public class DummyServerPlayerEntity
    extends ServerPlayerEntity
{
    private static final String DUMMY_NAME = "TempPlayer";

    private DummyServerPlayerEntity( final ServerWorld world, final GameProfile profile )
    {
        super(world.getServer(), world, profile);
    }

    private DummyServerPlayerEntity( final MinecraftServer server, final GameProfile profile )
    {
        super(server, server.getOverworld(), profile);
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
