package net.thomilist.dimensionalinventories.util;

import net.minecraft.nbt.CollectionTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashSet;

public final class NbtUtils
{
    private NbtUtils()
    { }

    public static boolean isEffectivelyEmpty( final @Nullable Tag nbtElement )
    {
        return switch ( nbtElement )
        {
            case final CollectionTag nbtList -> nbtList.isEmpty();
            case final CompoundTag nbtCompound ->
                nbtCompound.isEmpty() || nbtCompound.values().stream().allMatch( NbtUtils::isEffectivelyEmpty );
            case null -> true;
            default -> false;
        };
    }

    public static boolean areEffectivelyEqual( final @Nullable Tag left, final @Nullable Tag right )
    {
        if ( net.minecraft.nbt.NbtUtils.compareNbt( left, right, true ) )
        {
            return true;
        }

        if ( NbtUtils.isEffectivelyEmpty( left ) && NbtUtils.isEffectivelyEmpty( right ) )
        {
            return true;
        }

        if ( (left instanceof final CompoundTag leftCompound) && (right instanceof final CompoundTag rightCompound) )
        {
            final Collection<String> combinedKeys = new HashSet<>();
            combinedKeys.addAll( leftCompound.keySet() );
            combinedKeys.addAll( rightCompound.keySet() );

            for ( final String key : combinedKeys )
            {
                final Tag leftElement = leftCompound.get( key );
                final Tag rightElement = rightCompound.get( key );

                if ( !NbtUtils.areEffectivelyEqual( leftElement, rightElement ) )
                {
                    return false;
                }
            }
        }

        return true;
    }
}
