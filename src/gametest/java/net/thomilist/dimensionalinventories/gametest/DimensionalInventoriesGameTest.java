package net.thomilist.dimensionalinventories.gametest;

import net.thomilist.dimensionalinventories.DimensionalInventories;
import net.thomilist.dimensionalinventories.gametest.util.TestState;
import net.thomilist.dimensionalinventories.util.StringHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;

public abstract class DimensionalInventoriesGameTest
{
    public static final Logger LOGGER = LoggerFactory.getLogger(
        DimensionalInventories.PROPERTIES.namePascal() + "GameTest" );

    private String packageName;
    private String className;
    private String methodName;

    protected void begin()
    {
        final StackWalker.StackFrame caller = StackWalker
            .getInstance()
            .walk( frames -> frames.skip( 1 ).findFirst() )
            .orElseThrow();

        final int splitIndex = caller.getClassName().lastIndexOf( '.' );
        this.packageName = caller.getClassName().substring( 0, splitIndex );
        this.className = caller.getClassName().substring( splitIndex + 1 );
        this.methodName = caller.getMethodName();

        this.logTestBegin();
        TestState.stashLatestModData();
        TestState.setLatestBatchId( Instant.now().toEpochMilli() + "_" + this.getClass().getSimpleName() );
    }

    protected void end()
    {
        this.logTestEnd();
        TestState.stashLatestModData();
    }

    private void logTestStage( final String header )
    {
        final String scopeLine = StringHelper.joinAndWrapScopes( this.className, this.methodName );
        final String packageLine = "(in " + this.packageName + ')';

        final int longestLineLength = Integer.max(
            header.length() + 2, Integer.max(
                scopeLine.length(),
                packageLine.length()
            )
        );

        final int dashCount = Integer.max( (longestLineLength - (header.length() + 2)) / 2, 4 );

        final String dashes = "-".repeat( dashCount );

        DimensionalInventoriesGameTest.LOGGER.info( "{} {} {}", dashes, header, dashes );
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
