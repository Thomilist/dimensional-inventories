package net.thomilist.dimensionalinventories.gametest;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.test.AfterBatch;
import net.thomilist.dimensionalinventories.gametest.util.TestState;

public class Batches
{
    public static final String MAIN = "main";
    public static final String MIGRATION = "main.migration";

    @AfterBatch( batchId = Batches.MAIN )
    public void stashMainData( final ServerWorld unused )
    {
        TestState.stashModData( Batches.MAIN );
    }

    @AfterBatch( batchId = Batches.MIGRATION )
    public void stashMigrationData( final ServerWorld unused )
    {
        TestState.stashModData( Batches.MIGRATION );
    }
}
