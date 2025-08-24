package net.thomilist.dimensionalinventories.gametest.util;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.test.TestEnvironmentDefinition;
import net.minecraft.util.dynamic.Codecs;

public record BatchDefinition( int batchNumber )
    implements TestEnvironmentDefinition
{
    public static final MapCodec<BatchDefinition> CODEC = RecordCodecBuilder.mapCodec( instance -> instance
        .group( Codecs.NON_NEGATIVE_INT.fieldOf( "time" ).forGetter( BatchDefinition::batchNumber ) )
        .apply( instance, BatchDefinition::new ) );

    @Override
    public void setup( final ServerWorld world )
    { }

    @Override
    public MapCodec<BatchDefinition> getCodec()
    {
        return BatchDefinition.CODEC;
    }
}
