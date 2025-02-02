package net.thomilist.dimensionalinventories.gametest.util;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.test.TestContext;

import java.util.stream.IntStream;

public final class BlockPlacement
{
    public static void PlaceFloor( final TestContext context )
    {
        BlockPlacement.PlaceFloor( context, 1, Blocks.SMOOTH_STONE );
    }

    public static void PlaceFloor( final TestContext context, final int y, final Block block )
    {
        BlockPlacement.Fill( context, 0, y, 0, 7, y, 7, block );
    }

    public static void Fill( final TestContext context,
                             final int xa,
                             final int ya,
                             final int za,
                             final int xb,
                             final int yb,
                             final int zb,
                             final Block block )
    {
        final int[] xValues = IntStream.rangeClosed( xa, xb ).toArray();
        final int[] yValues = IntStream.rangeClosed( ya, yb ).toArray();
        final int[] zValues = IntStream.rangeClosed( za, zb ).toArray();

        for ( final int x : xValues )
        {
            for ( final int y : yValues )
            {
                for ( final int z : zValues )
                {
                    context.setBlockState( x, y, z, block );
                }
            }
        }
    }
}
