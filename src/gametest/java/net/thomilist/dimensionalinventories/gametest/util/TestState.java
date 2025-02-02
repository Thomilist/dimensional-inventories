package net.thomilist.dimensionalinventories.gametest.util;

import net.thomilist.dimensionalinventories.util.SavePaths;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public final class TestState
{
    public static final Path WORLD_SAVE_PATH = SavePaths.saveDirectory().getParent();
    public static final Path GAMETEST_STASH_PATH = TestState.WORLD_SAVE_PATH.resolve( ".gametest" );

    public static void stashModData( final String batchId )
    {
        final List<@NotNull Path> modDataPaths = List.of(
            SavePaths.saveDirectory(),
            TestState.WORLD_SAVE_PATH.resolve( "dimensionalinventories" )
        );

        File modDataDirectory;

        for ( final Path modDataPath : modDataPaths )
        {
            try
            {
                modDataDirectory = modDataPath.toFile();

                FileUtils.copyDirectory( modDataDirectory, TestState.GAMETEST_STASH_PATH.resolve( batchId ).toFile() );
                FileUtils.deleteDirectory( modDataDirectory );
            }
            catch ( final IOException ignored )
            {
                // Probably doesn't exist; just continue to the next one
            }
        }
    }
}
