package net.thomilist.dimensionalinventories.gametest.util;

import net.minecraft.nbt.AbstractNbtList;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtHelper;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;

public final class NbtUtils
{
    public static boolean isEffectivelyEmpty( @Nullable final NbtElement nbtElement )
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

    public static boolean areEffectivelyEqual( @Nullable final NbtElement left, @Nullable final NbtElement right )
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
            final HashSet<String> combinedKeys = new HashSet<String>();
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
