package net.thomilist.dimensionalinventories.gametest.util;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.thomilist.dimensionalinventories.DimensionalInventories;

import java.util.ArrayList;
import java.util.Objects;

public class NbtUtils
{
    public static final NbtCompound[] EMPTY_NBT;
    private static final NbtCompound EMPTY_FROM_NEW = new NbtCompound();
    private static final NbtCompound EMPTY_FROM_CONVERSION;
    private static final String[] EMPTY_NBT_STRINGS = {
        "{}",
        "{data:[],palette:[]}",
        "{blocks:[],palette:[]}",
        "{\n    data: [],\n    palette: []\n}",
        "{\n    blocks: [],\n    palette: []\n}"
    };

    static
    {
        try
        {
            EMPTY_FROM_CONVERSION
                = NbtHelper.fromNbtProviderString( NbtHelper.toNbtProviderString( new NbtCompound() ) );

            final ArrayList<NbtCompound> emptyNbt = new ArrayList<>();

            emptyNbt.add( NbtUtils.EMPTY_FROM_NEW );
            emptyNbt.add( NbtUtils.EMPTY_FROM_CONVERSION );

            for ( final String nbtString : NbtUtils.EMPTY_NBT_STRINGS )
            {
                emptyNbt.add( NbtHelper.fromNbtProviderString( nbtString ) );
            }

            EMPTY_NBT = emptyNbt.toArray( new NbtCompound[] { } );
        }
        catch ( final CommandSyntaxException e )
        {
            DimensionalInventories.LOGGER.error( "Nbt string", e );
            throw new IllegalStateException();
        }
    }

    public static boolean isEmpty( final NbtCompound nbt )
    {
        if ( nbt.isEmpty() )
        {
            return true;
        }

        final String nbtString = NbtHelper.toNbtProviderString( nbt );

        for ( final NbtCompound emptyNbt : NbtUtils.EMPTY_NBT )
        {
            if ( nbt == emptyNbt )
            {
                return true;
            }

            if ( Objects.equals( nbtString, NbtHelper.toNbtProviderString( emptyNbt ) ) )
            {
                return true;
            }
        }

        return false;
    }
}
