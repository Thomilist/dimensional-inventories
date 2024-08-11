package net.thomilist.dimensionalinventories.gametest.util;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.test.TestContext;

import java.util.stream.IntStream;

public class BlockPlacement
{
    public static void Fill(
        TestContext context,
        int xa, int ya, int za,
        int xb, int yb, int zb,
        Block block)
    {
        int[] xValues = IntStream.rangeClosed(xa, xb).toArray();
        int[] yValues = IntStream.rangeClosed(ya, yb).toArray();
        int[] zValues = IntStream.rangeClosed(za, zb).toArray();

        for (int x : xValues)
        {
            for (int y : yValues)
            {
                for (int z : zValues)
                {
                    context.setBlockState(x, y, z, block);
                }
            }
        }
    }

    public static void PlaceFloor(TestContext context, int y, Block block)
    {
        BlockPlacement.Fill(context, 0, y, 0, 7, y, 7, block);
    }

    public static void PlaceFloor(TestContext context)
    {
        BlockPlacement.PlaceFloor(context, 1, Blocks.SMOOTH_STONE);
    }
}
