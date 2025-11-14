package net.thomilist.dimensionalinventories.util;

import net.minecraft.nbt.AbstractNbtList;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtHelper;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashSet;

public final class NbtUtils
{
    private NbtUtils()
    { }

    public static boolean isEffectivelyEmpty( final @Nullable NbtElement nbtElement )
    {
        return switch ( nbtElement )
        {
            case final AbstractNbtList nbtList -> nbtList.isEmpty();
            case final NbtCompound nbtCompound ->
                nbtCompound.isEmpty() || nbtCompound.values().stream().allMatch( NbtUtils::isEffectivelyEmpty );
            case null -> true;
            default -> false;
        };
    }

    public static boolean areEffectivelyEqual( final @Nullable NbtElement left, final @Nullable NbtElement right )
    {
        if ( NbtHelper.matches( left, right, true ) )
        {
            return true;
        }

        if ( NbtUtils.isEffectivelyEmpty( left ) && NbtUtils.isEffectivelyEmpty( right ) )
        {
            return true;
        }

        if ( (left instanceof final NbtCompound leftCompound) && (right instanceof final NbtCompound rightCompound) )
        {
            final Collection<String> combinedKeys = new HashSet<>();
            combinedKeys.addAll( leftCompound.getKeys() );
            combinedKeys.addAll( rightCompound.getKeys() );

            for ( final String key : combinedKeys )
            {
                final NbtElement leftElement = leftCompound.get( key );
                final NbtElement rightElement = rightCompound.get( key );

                if ( !NbtUtils.areEffectivelyEqual( leftElement, rightElement ) )
                {
                    return false;
                }
            }
        }

        return true;
    }
}
