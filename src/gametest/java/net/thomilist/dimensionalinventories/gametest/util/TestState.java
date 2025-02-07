package net.thomilist.dimensionalinventories.gametest.util;

import net.thomilist.dimensionalinventories.gametest.DimensionalInventoriesGameTest;
import net.thomilist.dimensionalinventories.util.SavePaths;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
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

    public static File toFileExtractZip( final Path path )
    {
        try
        {
            return path.toFile();
        }
        // Calling ZipFile.toFile() throws unconditionally; it needs to be extracted
        catch ( final UnsupportedOperationException ignored )
        {
            try
            {
                final Path tempDirectoryPath = Files.createTempDirectory(
                    DimensionalInventoriesGameTest.class.getSimpleName() + "_extracted_zip" );

                Files.walkFileTree(
                    path, new SimpleFileVisitor<>()
                    {
                        @Override
                        public @NotNull FileVisitResult visitFile( final Path filePath,
                                                                   final @NotNull BasicFileAttributes attributes )
                            throws IOException
                        {
                            final Path relativePathInZip = path.relativize( filePath );
                            final Path targetPath = tempDirectoryPath.resolve( relativePathInZip.toString() );
                            Files.createDirectories( targetPath.getParent() );
                            Files.copy( filePath, targetPath );

                            return FileVisitResult.CONTINUE;
                        }
                    }
                );

                return tempDirectoryPath.toFile();
            }
            catch ( final IOException e )
            {
                throw new UncheckedIOException( "Failed to extract from zip path %s".formatted( path ), e );
            }
        }
    }
}
