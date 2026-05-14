package net.thomilist.dimensionalinventories.module;

import net.minecraft.IdentifierException;
import net.thomilist.dimensionalinventories.DimensionalInventories;
import net.thomilist.dimensionalinventories.exception.ModuleNotRegisteredException;
import net.thomilist.dimensionalinventories.module.base.Module;
import net.thomilist.dimensionalinventories.module.version.StorageVersion;

import java.util.HashMap;
import java.util.SortedSet;
import java.util.TreeSet;

public final class ModuleRegistry<T extends Module>
{
    private final HashMap<StorageVersion, SortedSet<T>> modules = new HashMap<>();
    private final Class<T> moduleType;

    public ModuleRegistry( final Class<T> moduleType )
    {
        this.moduleType = moduleType;
    }

    public static boolean isValidId( final String groupId )
    {
        return groupId.matches( "^[a-z]+[a-z0-9_-]*[a-z0-9]*$" );
    }

    private void register( final T module )
    {
        for ( final StorageVersion storageVersion : module.storageVersions() )
        {
            this.modules.putIfAbsent( storageVersion, new TreeSet<>() );

            if ( this.modules
                .get( storageVersion )
                .removeIf( existingModule -> existingModule
                    .getClass()
                    .getCanonicalName()
                    .equals( module.getClass().getCanonicalName() ) ) )
            {
                DimensionalInventories.LOGGER.warn(
                    "Module {} replaces already-registered module of the same type ({})",
                    module.toFormatted(),
                    module.getClass()
                );
            }

            if ( !this.modules.get( storageVersion ).add( module ) )
            {
                DimensionalInventories.LOGGER.warn(
                    "Failed to register module: {} has already been registered",
                    module.toFormatted()
                );

                continue;
            }

            if ( module.latestStorageVersion() == StorageVersion.latest() )
            {
                module.registerCommands();
            }

            DimensionalInventories.LOGGER.info(
                "Registered {}",
                module.toFormatted()
            );
        }
    }

    public void register( final ModuleGroup moduleGroup )
        throws IdentifierException
    {
        if ( !ModuleRegistry.isValidId( moduleGroup.groupId() ) )
        {
            throw new IdentifierException( "'%s' is not a valid module group ID".formatted( moduleGroup.groupId() ) );
        }

        for ( final Module module : moduleGroup.modules )
        {
            if ( this.moduleType.isInstance( module ) )
            {
                this.register( this.moduleType.cast( module ) );
            }
        }
    }

    public boolean has( final StorageVersion storageVersion )
    {
        return this.modules.containsKey( storageVersion );
    }

    public SortedSet<T> get( final StorageVersion storageVersion )
    {
        return this.modules.getOrDefault( storageVersion, new TreeSet<>() );
    }

    public <M extends T> M get( final Class<M> moduleType )
        throws ModuleNotRegisteredException
    {
        for ( final SortedSet<T> moduleSet : this.modules.values() )
        {
            for ( final T module : moduleSet )
            {
                if ( moduleType.isInstance( module ) )
                {
                    return moduleType.cast( module );
                }
            }
        }

        throw new ModuleNotRegisteredException( moduleType );
    }
}
