package net.thomilist.dimensionalinventories.util;

import com.mojang.authlib.GameProfile;
import net.minecraft.network.packet.c2s.common.SyncedClientOptions;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

import java.util.UUID;

// Intended to hold data during data migrations: load old format to this, save this to new format
public class DummyServerPlayerEntity extends ServerPlayerEntity
{
    private static final String DUMMY_NAME = "TempPlayer";

    private DummyServerPlayerEntity(ServerWorld world, GameProfile profile)
    {
        super(world.getServer(), world, profile, SyncedClientOptions.createDefault());
    }

    private DummyServerPlayerEntity(MinecraftServer server, GameProfile profile)
    {
        super(server, server.getOverworld(), profile, SyncedClientOptions.createDefault());
    }

    public DummyServerPlayerEntity(ServerWorld world, UUID uuid)
    {
        this(world, new GameProfile(uuid, DUMMY_NAME));
    }

    public DummyServerPlayerEntity(MinecraftServer server, UUID uuid)
    {
        this(server, new GameProfile(uuid, DUMMY_NAME));
    }

    public DummyServerPlayerEntity(ServerWorld world, String uuid)
    {
        this(world, UUID.fromString(uuid));
    }

    public DummyServerPlayerEntity(MinecraftServer server, String uuid)
    {
        this(server, UUID.fromString(uuid));
    }
}
