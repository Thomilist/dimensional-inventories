package net.thomilist.dimensionalinventories.lostandfound;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.thomilist.dimensionalinventories.DimensionalInventories;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.NoSuchElementException;

public class LostAndFound
{
    private static final String FILE_EXT = ".log";
    private static final DateTimeFormatter TIMESTAMP_FORMAT_TEXT = DateTimeFormatter.ofPattern(
        "yyyy-MM-dd'T'HH:mm:ss.SSSXXX" );
    private static final DateTimeFormatter TIMESTAMP_FORMAT_FILE = DateTimeFormatter.ofPattern(
        "yyyy-MM-dd'T'HH_mm_ss.SSSXX" );
    private static final String BEGIN_METADATA = "--- BEGIN LOST+FOUND METADATA ---";
    private static final String END_METADATA = "--- END LOST+FOUND METADATA ---";
    private static final String BEGIN_CONTENT = "--- BEGIN LOST+FOUND CONTENT ---";
    private static final String END_CONTENT = "--- END LOST+FOUND CONTENT ---";
    private static final String BEGIN_EXCEPTION = "--- BEGIN LOST+FOUND EXCEPTION ---";
    private static final String END_EXCEPTION = "--- END LOST+FOUND EXCEPTION ---";
    private static final Component DATA_LOSS_MESSAGE = Component
        .literal(
            "Some data was lost when crossing dimension pools. Consult server staff for more details and, possibly, " +
            "data recovery." )
        .withStyle( ChatFormatting.RED );
    public static LostAndFoundContext CONTEXT = LostAndFoundContext.create();

    public static LostAndFoundContext init( final Object... scopes )
    {
        final LostAndFoundContext context = LostAndFoundContext.create( scopes );
        LostAndFound.CONTEXT = context;
        DimensionalInventories.LOGGER.debug( LostAndFound.CONTEXT.toString() );
        return context;
    }

    public static LostAndFoundScope push( final Object... layers )
    {
        final LostAndFoundScope pushed = LostAndFound.CONTEXT.push( layers );
        DimensionalInventories.LOGGER.debug( LostAndFound.CONTEXT.toString() );
        return pushed;
    }

    public static void log( final String cause, final String content )
    {
        LostAndFound.log( cause, content, null );
    }

    public static void log( final String cause, final String content, final Exception exception )
    {
        LostAndFound.informPlayer();

        final LocalDateTime now = LocalDateTime.now();
        final ArrayList<String> lines = new ArrayList<>();

        lines.add( LostAndFound.BEGIN_METADATA );
        lines.add( now.format( LostAndFound.TIMESTAMP_FORMAT_TEXT ) );
        lines.add( cause );
        lines.add( LostAndFound.CONTEXT.toString() );
        lines.add( LostAndFound.END_METADATA );

        if ( content != null )
        {
            lines.add( "" );

            lines.add( LostAndFound.BEGIN_CONTENT );
            lines.add( content );
            lines.add( LostAndFound.END_CONTENT );
        }

        if ( exception != null )
        {
            lines.add( "" );

            lines.add( LostAndFound.BEGIN_EXCEPTION );
            lines.add( exception.toString() );
            lines.add( LostAndFound.END_EXCEPTION );
        }

        final String entry = String.join( "\n", lines );
        final Path outputDirectory = LostAndFound.CONTEXT.outputDirectory();
        final Path outputFile = outputDirectory.resolve(
            now.format( LostAndFound.TIMESTAMP_FORMAT_FILE ) + LostAndFound.FILE_EXT );

        DimensionalInventories.LOGGER.error( cause );
        DimensionalInventories.LOGGER.error( "Context: {}", LostAndFound.CONTEXT );

        try
        {
            Files.createDirectories( outputDirectory );
            Files.writeString( outputFile, entry );
            DimensionalInventories.LOGGER.error( "Details have been written to lost+found" );
            DimensionalInventories.LOGGER.error( "File: '{}'", outputFile );
        }
        catch ( final IOException e )
        {
            DimensionalInventories.LOGGER.error( "Failed to save lost+found entry:\n{}", entry );
            DimensionalInventories.LOGGER.error( "Caused by:", e );
        }
    }

    private static void informPlayer()
    {
        try
        {
            LostAndFound.CONTEXT.getPlayer().sendSystemMessage( LostAndFound.DATA_LOSS_MESSAGE );
        }
        catch ( final NoSuchElementException e )
        {
            // Only inform the player if the context actually includes a player
        }
    }

    public static void log( final String cause, final Exception exception )
    {
        LostAndFound.log( cause, null, exception );
    }
}
