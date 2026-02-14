package net.thomilist.dimensionalinventories.gametest.util;

import com.mojang.authlib.GameProfile;
import net.minecraft.util.UserCache;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * A dummy implementation of the vanilla {@link UserCache}.
 * <p>
 * This requires the access widener {@code accessible class net/minecraft/util/UserCache$Entry}.
 */
public class DummyUserCache
    extends UserCache
{
    public DummyUserCache()
    {
        super( null, null );
    }

    @Override
    public void add( final GameProfile profile )
    { }

    @Override
    public Optional<GameProfile> findByName( final String name )
    {
        return Optional.empty();
    }

    @Override
    public void findByNameAsync( final String username, final Consumer<Optional<GameProfile>> consumer )
    { }

    @Override
    public Optional<GameProfile> getByUuid( final UUID uuid )
    {
        return Optional.empty();
    }

    @Override
    public List<UserCache.Entry> load()
    {
        return List.of();
    }

    @Override
    public void save()
    { }
}
