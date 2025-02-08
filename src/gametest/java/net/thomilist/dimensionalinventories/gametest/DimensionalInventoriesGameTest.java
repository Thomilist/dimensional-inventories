package net.thomilist.dimensionalinventories.gametest;

import net.thomilist.dimensionalinventories.DimensionalInventories;
import net.thomilist.dimensionalinventories.util.StringHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class DimensionalInventoriesGameTest
{
    public static final Logger LOGGER = LoggerFactory.getLogger(
        DimensionalInventories.PROPERTIES.namePascal() + "GameTest" );

    protected void logTestStart()
    {
        final StackWalker.StackFrame caller = StackWalker
            .getInstance()
            .walk( frames -> frames.skip( 1 ).findFirst() )
            .orElseThrow();

        final int splitIndex = caller.getClassName().lastIndexOf( '.' );
        final String packageName = caller.getClassName().substring( 0, splitIndex );
        final String className = caller.getClassName().substring( splitIndex + 1 );

        DimensionalInventoriesGameTest.LOGGER.info( "----------------- BEGIN GAME TEST -----------------" );
        DimensionalInventoriesGameTest.LOGGER.info( StringHelper.joinAndWrapScopes(
            className,
            caller.getMethodName()
        ) );
        DimensionalInventoriesGameTest.LOGGER.info( "(in {})", packageName);
    }
}
