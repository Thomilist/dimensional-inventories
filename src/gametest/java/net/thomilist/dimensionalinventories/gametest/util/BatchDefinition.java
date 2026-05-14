package net.thomilist.dimensionalinventories.gametest.util;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.gametest.framework.TestEnvironmentDefinition;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ExtraCodecs;

public record BatchDefinition( int batchNumber )
    implements TestEnvironmentDefinition
{
    public static final MapCodec<BatchDefinition> CODEC = RecordCodecBuilder.mapCodec( instance -> instance
        .group( ExtraCodecs.NON_NEGATIVE_INT.fieldOf( "time" ).forGetter( BatchDefinition::batchNumber ) )
        .apply( instance, BatchDefinition::new ) );

    @Override
    public void setup( final ServerLevel world )
    { }

    @Override
    public MapCodec<BatchDefinition> codec()
    {
        return BatchDefinition.CODEC;
    }
}
