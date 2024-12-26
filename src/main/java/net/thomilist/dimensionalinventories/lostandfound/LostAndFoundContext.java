package net.thomilist.dimensionalinventories.lostandfound;

import net.minecraft.server.network.ServerPlayerEntity;
import net.thomilist.dimensionalinventories.DimensionalInventories;
import net.thomilist.dimensionalinventories.module.base.Module;
import net.thomilist.dimensionalinventories.module.builtin.pool.DimensionPool;
import net.thomilist.dimensionalinventories.util.SavePaths;
import net.thomilist.dimensionalinventories.util.StringHelper;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;

public class LostAndFoundContext
    implements AutoCloseable
{
    private final ArrayList<LostAndFoundScope> scopes = new ArrayList<>();

    private LostAndFoundContext()
    { }

    public static LostAndFoundContext create( final Object... scopes )
    {
        final LostAndFoundContext context = new LostAndFoundContext();

        for ( final Object layer : scopes )
        {
            context.push( layer );
        }

        return context;
    }

    public LostAndFoundScope push( final Object... layers )
    {
        final LostAndFoundScope wrappedLayer = new LostAndFoundScope( this, layers );
        this.scopes.add( wrappedLayer );
        return wrappedLayer;
    }

    @Override
    public String toString()
    {
        return StringHelper.joinAndWrapScopes( this.scopes.stream().map( LostAndFoundScope::toString ).toList() );
    }

    @SuppressWarnings( "resource" )
    public void pop()
    {
        if ( !this.scopes.isEmpty() )
        {
            scopes.remove(scopes.size() - 1);
        }
    }

    public boolean isEmpty()
    {
        return this.scopes.isEmpty();
    }

    public LostAndFoundScope head()
    {
        return scopes.isEmpty() ? null : scopes.get(scopes.size() - 1);
    }

    public Collection<LostAndFoundScope> scopes()
    {
        return this.scopes;
    }

    public Path outputDirectory()
    {
        final List<Class<?>> specialTypes = List.of( DimensionPool.class, ServerPlayerEntity.class, Module.class );
        final List<Object> specialObjects = new ArrayList<>();

        for ( final Object layer : this.layers() )
        {
            if ( specialObjects.size() >= specialTypes.size() )
            {
                break;
            }

            if ( specialTypes.get( specialObjects.size() ).isInstance( layer ) )
            {
                specialObjects.add( layer );
            }
        }

        return switch ( specialObjects.size() )
        {
            case 1 -> SavePaths.lostAndFoundDirectory(
                DimensionalInventories.INSTANCE.storageVersion,
                (DimensionPool) specialObjects.get( 0 )
            );
            case 2 -> SavePaths.lostAndFoundDirectory(
                DimensionalInventories.INSTANCE.storageVersion,
                (DimensionPool) specialObjects.get( 0 ),
                (ServerPlayerEntity) specialObjects.get( 1 )
            );
            case 3 -> SavePaths.lostAndFoundDirectory(
                DimensionalInventories.INSTANCE.storageVersion,
                (DimensionPool) specialObjects.get( 0 ),
                (ServerPlayerEntity) specialObjects.get( 1 ),
                (Module) specialObjects.get( 2 )
            );
            default -> SavePaths.lostAndFoundDirectory( DimensionalInventories.INSTANCE.storageVersion );
        };
    }

    public Collection<Object> layers()
    {
        return this.scopes.stream().flatMap( scope -> scope.layers().stream() ).toList();
    }

    @Override
    public void close()
    {
        this.scopes.clear();
    }

    public ServerPlayerEntity getPlayer()
        throws NoSuchElementException
    {
        for ( final Object layer : this.layers() )
        {
            if ( layer instanceof ServerPlayerEntity )
            {
                return (ServerPlayerEntity) layer;
            }
        }

        throw new NoSuchElementException( "No player in this lost+found context" );
    }
}
