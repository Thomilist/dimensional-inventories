package net.thomilist.dimensionalinventories.module.builtin.legacy.inventory;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.collection.DefaultedList;
import net.thomilist.dimensionalinventories.lostandfound.LostAndFound;
import net.thomilist.dimensionalinventories.lostandfound.LostAndFoundScope;
import net.thomilist.dimensionalinventories.module.base.ModuleBase;
import net.thomilist.dimensionalinventories.module.base.player.StatefulPlayerModule;
import net.thomilist.dimensionalinventories.module.builtin.inventory.InventoryModule;
import net.thomilist.dimensionalinventories.module.builtin.inventory.InventoryModuleState;
import net.thomilist.dimensionalinventories.module.builtin.inventory.InventorySection;
import net.thomilist.dimensionalinventories.module.builtin.legacy.ModuleHelper_SV1;
import net.thomilist.dimensionalinventories.module.builtin.pool.DimensionPool;
import net.thomilist.dimensionalinventories.module.version.StorageVersion;
import net.thomilist.dimensionalinventories.util.ItemStackListHelper;
import net.thomilist.dimensionalinventories.util.NbtConversionHelper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * @deprecated This is only intended for use during migration of old data of storage version 1. When loading or saving
 * new data, use {@link InventoryModule} instead.
 */
@Deprecated
public final class InventoryModule_SV1
    extends ModuleBase
    implements StatefulPlayerModule<InventoryModuleState>
{
    private static final String MODULE_ID = "inventory";
    private static final String DESCRIPTION = "Items in inventory, hotbar, offhand & armour slots.";

    private static final StorageVersion[] STORAGE_VERSIONS = {
        StorageVersion.V1
    };

    private final InventoryModuleState state = new InventoryModuleState();

    public InventoryModule_SV1( final String groupId )
    {
        super(
            InventoryModule_SV1.STORAGE_VERSIONS,
            groupId,
            InventoryModule_SV1.MODULE_ID,
            InventoryModule_SV1.DESCRIPTION
        );
    }

    @Override
    public InventoryModuleState newInstance( final ServerPlayerEntity player )
    {
        return new InventoryModuleState();
    }

    @Override
    public InventoryModuleState state()
    {
        return this.state;
    }

    @Override
    public InventoryModuleState defaultState()
    {
        return new InventoryModuleState();
    }

    @Override
    public void load( final ServerPlayerEntity player, final DimensionPool dimensionPool )
    {
        final Path saveFile = ModuleHelper_SV1.saveFile( dimensionPool, player );
        final List<String> lines;

        try
        {
            lines = Files.readAllLines( saveFile );
        }
        catch ( final IOException e )
        {
            LostAndFound.log( "Failed to read inventory data file", e );
            return;
        }

        int lineIndex = 0;

        final InventoryModuleState inventoryModuleState = new InventoryModuleState();

        for ( final InventorySection label : InventorySection.list() )
        {
            try ( final LostAndFoundScope LAF = LostAndFound.push( label ) )
            {
                final DefaultedList<ItemStack> items = DefaultedList.ofSize(
                    inventoryModuleState
                        .section( label )
                        .size(), ItemStack.EMPTY
                );

                for ( int i = 0; i < items.size(); i++ )
                {
                    final NbtCompound nbt;

                    try
                    {
                        nbt = NbtHelper.fromNbtProviderString( lines.get( lineIndex++ ) );
                    }
                    catch ( final CommandSyntaxException e )
                    {
                        LostAndFound.log( "Failed to parse NBT string for " + label + '[' + i + ']', e );
                        return;
                    }

                    items.set( i, NbtConversionHelper.fromNbt( nbt ) );
                }

                if ( !items.isEmpty() )
                {
                    ItemStackListHelper.assignItemStacks( items, inventoryModuleState.section( label ) );
                }
            }
        }

        inventoryModuleState.applyToPlayer( player );
    }

    @Override
    public void save( final ServerPlayerEntity player, final DimensionPool dimensionPool )
    {
        // Intentionally not implemented
        ModuleHelper_SV1.ThrowOnDeprecatedSave( InventoryModule.class );
    }
}
