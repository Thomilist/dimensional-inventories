package net.thomilist.dimensionalinventories.lostandfound;

import net.minecraft.server.network.ServerPlayerEntity;
import net.thomilist.dimensionalinventories.DimensionalInventoriesMod;
import net.thomilist.dimensionalinventories.module.base.Module;
import net.thomilist.dimensionalinventories.module.builtin.pool.DimensionPool;
import net.thomilist.dimensionalinventories.util.LogHelper;
import net.thomilist.dimensionalinventories.util.SavePaths;

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

    public static LostAndFoundContext create(Object... scopes)
    {
        LostAndFoundContext context = new LostAndFoundContext();

        for (Object layer : scopes)
        {
            context.push(layer);
        }

        return context;
    }

    @Override
    public String toString()
    {
        return LogHelper.joinAndWrapScopes(scopes.stream().map(LostAndFoundScope::toString).toList());
    }

    public LostAndFoundScope push(Object... layers)
    {
        LostAndFoundScope wrappedLayer = new LostAndFoundScope(this, layers);
        this.scopes.add(wrappedLayer);
        return wrappedLayer;
    }

    public void pop()
    {
        if (!scopes.isEmpty())
        {
            scopes.removeLast();
        }
    }

    public boolean isEmpty()
    {
        return scopes.isEmpty();
    }


    public LostAndFoundScope head()
    {
        return scopes.isEmpty() ? null : scopes.getLast();
    }

    public Collection<LostAndFoundScope> scopes()
    {
        return scopes;
    }

    public Collection<Object> layers()
    {
        return scopes.stream().flatMap(scope -> scope.layers().stream()).toList();
    }

    public Path outputDirectory()
    {
        final List<Class<?>> specialTypes = List.of(DimensionPool.class, ServerPlayerEntity.class, Module.class);
        final List<Object> specialObjects = new ArrayList<>();

        for (var layer : layers())
        {
            if (specialObjects.size() >= specialTypes.size())
            {
                break;
            }

            if (specialTypes.get(specialObjects.size()).isInstance(layer))
            {
                specialObjects.add(layer);
            }
        }

        return switch (specialObjects.size())
        {
            case 1 -> SavePaths.lostAndFoundDirectory(
                DimensionalInventoriesMod.STORAGE_VERSION,
                (DimensionPool) specialObjects.get(0)
            );
            case 2 -> SavePaths.lostAndFoundDirectory(
                DimensionalInventoriesMod.STORAGE_VERSION,
                (DimensionPool) specialObjects.get(0),
                (ServerPlayerEntity) specialObjects.get(1)
            );
            case 3 -> SavePaths.lostAndFoundDirectory(
                DimensionalInventoriesMod.STORAGE_VERSION,
                (DimensionPool) specialObjects.get(0),
                (ServerPlayerEntity) specialObjects.get(1),
                (Module) specialObjects.get(2)
            );
            default -> SavePaths.lostAndFoundDirectory(DimensionalInventoriesMod.STORAGE_VERSION);
        };
    }

    @Override
    public void close()
    {
        scopes.clear();
    }

    public ServerPlayerEntity getPlayer() throws NoSuchElementException
    {
        for (var layer : layers())
        {
            if (layer instanceof ServerPlayerEntity)
            {
                return (ServerPlayerEntity) layer;
            }
        }

        throw new NoSuchElementException("No player in this lost+found context");
    }
}
