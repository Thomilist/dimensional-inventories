package net.thomilist.dimensionalinventories.gametest;

import net.fabricmc.fabric.api.gametest.v1.CustomTestMethodInvoker;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.storage.LevelResource;
import net.thomilist.dimensionalinventories.DimensionalInventories;
import net.thomilist.dimensionalinventories.gametest.util.TestState;
import net.thomilist.dimensionalinventories.util.StringHelper;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.time.Instant;

public abstract class DimensionalInventoriesGameTest
    implements CustomTestMethodInvoker
{
    public static final Logger LOGGER = LoggerFactory.getLogger(
        DimensionalInventories.PROPERTIES.namePascal() + "GameTest" );

    public static final int MAX_TICKS = 200;

    private static boolean RUNNING = false;
    private String packageName;
    private String className;
    private String methodName;
    private int ticks;

    protected DimensionalInventoriesGameTest()
    { }

    protected static void initializeSampleData( final GameTestHelper context,
                                                final String resourcePath,
                                                final String worldDestinationPath )
    {
        final Path sampleDataPath = FabricLoader
            .getInstance()
            .getModContainer( "dimensional-inventories-gametest" )
            .orElseThrow()
            .findPath( resourcePath )
            .orElseThrow();

        final Path legacyDataPath = context
            .getLevel()
            .getServer()
            .getWorldPath( LevelResource.ROOT )
            .resolve( worldDestinationPath );

        final File sampleDataDirectory = TestState.toFileExtractZip( sampleDataPath );
        final File legacyDataDirectory = legacyDataPath.toFile();

        try
        {
            if ( legacyDataDirectory.exists() )
            {
                FileUtils.deleteDirectory( legacyDataDirectory );
            }

            FileUtils.copyDirectory( sampleDataDirectory, legacyDataDirectory );
        }
        catch ( final IOException e )
        {
            throw new UncheckedIOException( e );
        }
    }

    @Override
    public void invokeTestMethod( final GameTestHelper context, final Method method )
        throws ReflectiveOperationException
    {
        if ( DimensionalInventoriesGameTest.RUNNING )
        {
            context.runAfterDelay(
                1, () -> {
                    try
                    {
                        this.invokeTestMethod( context, method );
                    }
                    catch ( final ReflectiveOperationException e )
                    {
                        throw context.assertionException( Component.nullToEmpty(
                            "Failed to run game test " + "(ReflectiveOperationException): " + e.getMessage() ) );
                    }
                }
            );
        }
        else
        {
            try
            {
                this.ticks = context.getLevel().getServer().getTickCount();
                this.begin( method );
                method.invoke( this, context );
            }
            catch ( final Exception e )
            {
                DimensionalInventoriesGameTest.LOGGER.error( "An error occurred while running the test:", e );
                throw e;
            }
            finally
            {
                this.ticks = context.getLevel().getServer().getTickCount();
                this.end();
            }
        }
    }

    private void begin( final Method method )
    {
        DimensionalInventoriesGameTest.RUNNING = true;

        this.packageName = method.getDeclaringClass().getPackageName();
        this.className = method.getDeclaringClass().getSimpleName();
        this.methodName = method.getName();

        this.logTestBegin();
        TestState.stashLatestModData();
        TestState.setLatestBatchId( Instant.now().toEpochMilli() + "_" + this.getClass().getSimpleName() );
    }

    private void end()
    {
        this.logTestEnd();
        TestState.stashLatestModData();

        DimensionalInventoriesGameTest.RUNNING = false;
    }

    private void logTestStage( final String header )
    {
        final String scopeLine = StringHelper.joinAndWrapScopes( this.className, this.methodName );
        final String packageLine = "(in " + this.packageName + ')';

        final int longestLineLength = Integer.max(
            header.length() + 2,
            Integer.max( scopeLine.length(), packageLine.length() )
        );

        final int dashCount = Integer.max( (longestLineLength - (header.length() + 2)) / 2, 4 );

        final String dashes = "-".repeat( dashCount );

        DimensionalInventoriesGameTest.LOGGER.info( "{} {} {}", dashes, header, dashes );
        DimensionalInventoriesGameTest.LOGGER.info( "@ tick {}", this.ticks );
        DimensionalInventoriesGameTest.LOGGER.info( scopeLine );
        DimensionalInventoriesGameTest.LOGGER.info( packageLine );
    }

    private void logTestBegin()
    {
        this.logTestStage( "BEGIN GAME TEST" );
    }

    private void logTestEnd()
    {
        this.logTestStage( "END GAME TEST" );
    }
}
