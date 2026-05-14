package net.thomilist.dimensionalinventories.gametest.util;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.gametest.framework.TestEnvironmentDefinition;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ExtraCodecs;
import org.jspecify.annotations.NonNull;

public record BatchDefinition( int batchNumber )
    implements TestEnvironmentDefinition<Integer>
{
    public static final MapCodec<BatchDefinition> CODEC = RecordCodecBuilder.mapCodec( instance -> instance
        .group( ExtraCodecs.NON_NEGATIVE_INT.fieldOf( "time" ).forGetter( BatchDefinition::batchNumber ) )
        .apply( instance, BatchDefinition::new ) );

    @Override
    public @NonNull Integer setup( final @NonNull ServerLevel world )
    {
        return this.batchNumber;
    }

    @Override
    public void teardown( final @NonNull ServerLevel level, final @NonNull Integer saveData )
    { }

    @Override
    public @NonNull MapCodec<BatchDefinition> codec()
    {
        return BatchDefinition.CODEC;
    }
}
