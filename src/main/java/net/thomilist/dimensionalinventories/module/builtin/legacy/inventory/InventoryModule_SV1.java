package net.thomilist.dimensionalinventories.module.builtin.legacy.inventory;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.thomilist.dimensionalinventories.compatibility.Compat;
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
    public InventoryModuleState newInstance( final ServerPlayer player )
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
    public void load( final ServerPlayer player, final DimensionPool dimensionPool )
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
                final NonNullList<ItemStack> items = NonNullList.withSize(
                    inventoryModuleState
                        .section( label )
                        .size(), ItemStack.EMPTY
                );

                for ( int i = 0; i < items.size(); i++ )
                {
                    final CompoundTag nbt;

                    try
                    {
                        nbt = NbtUtils.snbtToStructure( lines.get( lineIndex++ ) );
                    }
                    catch ( final CommandSyntaxException e )
                    {
                        LostAndFound.log( "Failed to parse NBT string for " + label + '[' + i + ']', e );
                        return;
                    }

                    items.set( i, Compat.NBT.toItemStack( nbt ) );
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
    public void save( final ServerPlayer player, final DimensionPool dimensionPool )
    {
        // Intentionally not implemented
        ModuleHelper_SV1.ThrowOnDeprecatedSave( InventoryModule.class );
    }
}
