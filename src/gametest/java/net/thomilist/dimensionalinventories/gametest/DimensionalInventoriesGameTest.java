package net.thomilist.dimensionalinventories.gametest;

import net.fabricmc.fabric.api.gametest.v1.CustomTestMethodInvoker;
import net.minecraft.test.TestContext;
import net.minecraft.text.Text;
import net.thomilist.dimensionalinventories.DimensionalInventories;
import net.thomilist.dimensionalinventories.gametest.util.TestState;
import net.thomilist.dimensionalinventories.util.StringHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
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

    @Override
    public void invokeTestMethod( final TestContext context, final Method method )
        throws ReflectiveOperationException
    {
        if ( DimensionalInventoriesGameTest.RUNNING )
        {
            context.waitAndRun(
                1, () -> {
                    try
                    {
                        this.invokeTestMethod( context, method );
                    }
                    catch ( final ReflectiveOperationException e )
                    {
                        throw context.createError( Text.of(
                            "Failed to run game test " + "(ReflectiveOperationException): " + e.getMessage() ) );
                    }
                }
            );
        }
        else
        {
            try
            {
                this.ticks = context.getWorld().getServer().getTicks();
                this.begin( method );
                method.invoke( this, context );
            }
            finally
            {
                this.ticks = context.getWorld().getServer().getTicks();
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
