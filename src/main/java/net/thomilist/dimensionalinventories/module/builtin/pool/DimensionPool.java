package net.thomilist.dimensionalinventories.module.builtin.pool;

import net.minecraft.world.level.GameType;
import net.thomilist.dimensionalinventories.lostandfound.LostAndFoundFormattable;
import net.thomilist.dimensionalinventories.module.builtin.legacy.pool.DimensionPool_SV1;

import java.util.List;
import java.util.TreeSet;

public final class DimensionPool
    implements LostAndFoundFormattable
{
    private static final List<String> DEFAULT_DIMENSIONS = List.of(
        "minecraft:overworld",
        "minecraft:the_nether",
        "minecraft:the_end"
    );

    private static final String DEFAULT_DIMENSION_POOL_ID = "default";
    private final TreeSet<String> dimensions = new TreeSet<>();
    private String id;
    private String displayName;
    private GameType gameMode = GameType.DEFAULT_MODE;
    private boolean progressAdvancements = true;
    private boolean incrementStatistics = true;

    private DimensionPool()
    { }

    public DimensionPool( final String id )
    {
        this.setId( id );
        this.setDisplayName( id );
    }

    public DimensionPool( final String id, final GameType gameMode )
    {
        this( id );
        this.setGameMode( gameMode );
    }

    public static DimensionPool createDefault()
    {
        final DimensionPool dimensionPool = new DimensionPool( DimensionPool.DEFAULT_DIMENSION_POOL_ID );

        for ( final String dimension : DimensionPool.DEFAULT_DIMENSIONS )
        {
            dimensionPool.addDimension( dimension );
        }

        return dimensionPool;
    }

    @SuppressWarnings( "deprecation" )
    public static DimensionPool fromLegacy( final DimensionPool_SV1 legacyDimensionPool )
    {
        final DimensionPool newDimensionPool = new DimensionPool();

        newDimensionPool.setId( legacyDimensionPool.name() );
        newDimensionPool.setDisplayName( legacyDimensionPool.name() );
        newDimensionPool.setGameMode( legacyDimensionPool.gameMode() );
        newDimensionPool.setProgressAdvancements( legacyDimensionPool.progressAdvancements() );
        newDimensionPool.setIncrementStatistics( legacyDimensionPool.incrementStatistics() );

        for ( final String dimension : legacyDimensionPool.dimensions() )
        {
            newDimensionPool.addDimension( dimension );
        }

        return newDimensionPool;
    }

    public String getId()
    {
        return this.id;
    }

    private void setId( final String id )
    {
        this.id = id;
    }

    public String getDisplayName()
    {
        return this.displayName;
    }

    public void setDisplayName( final String displayName )
    {
        this.displayName = displayName;
    }

    public void addDimension( final String dimension )
    {
        this.dimensions.add( dimension );
    }

    public void removeDimension( final String dimension )
    {
        this.dimensions.remove( dimension );
    }

    public TreeSet<String> getDimensions()
    {
        return this.dimensions;
    }

    public boolean hasDimensions( final String... dimensions )
    {
        for ( final String dimension : dimensions )
        {
            if ( !this.dimensions.contains( dimension ) )
            {
                return false;
            }
        }

        return true;
    }

    public GameType getGameMode()
    {
        return this.gameMode;
    }

    public void setGameMode( final GameType gameMode )
    {
        this.gameMode = gameMode;
    }

    public void setProgressAdvancements( final boolean setting )
    {
        this.progressAdvancements = setting;
    }

    public boolean canProgressAdvancements()
    {
        return this.progressAdvancements;
    }

    public void setIncrementStatistics( final boolean setting )
    {
        this.incrementStatistics = setting;
    }

    public boolean canIncrementStatistics()
    {
        return this.incrementStatistics;
    }

    public String asString()
    {
        final StringBuilder dimensionPoolString = new StringBuilder();

        // Dimension pool header
        dimensionPoolString.append( "\n[" ).append( this.getId() ).append( ']' );

        // Rules
        dimensionPoolString.append( "\n    Rules:" );
        dimensionPoolString.append( "\n        Gamemode: " ).append( this.getGameMode().getSerializedName() );
        dimensionPoolString.append( "\n        Progress advancements: " ).append( this.canProgressAdvancements() );
        dimensionPoolString.append( "\n        Increment statistics: " ).append( this.canIncrementStatistics() );

        // Dimensions
        dimensionPoolString.append( "\n    Dimensions:" );

        for ( final String dimension : this.getDimensions() )
        {
            dimensionPoolString.append( "\n        " ).append( dimension );
        }

        return dimensionPoolString.toString();
    }

    @Override
    public String toLostAndFoundScopeString()
    {
        return this.getDisplayName() + " (" + this.getId() + ')';
    }
}
