package net.thomilist.dimensionalinventories.lostandfound;

import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.thomilist.dimensionalinventories.DimensionalInventories;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.NoSuchElementException;

public class LostAndFound
{
    public static LostAndFoundContext CONTEXT = LostAndFoundContext.create();

    private static final String FILE_EXT = ".log";

    private static final String TIMESTAMP_FORMAT_TEXT = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX";
    private static final String TIMESTAMP_FORMAT_FILE = "yyyy-MM-dd'T'HH_mm_ss.SSSXX";

    private static final String BEGIN_METADATA = "--- BEGIN LOST+FOUND METADATA ---";
    private static final String END_METADATA = "--- END LOST+FOUND METADATA ---";
    private static final String BEGIN_CONTENT = "--- BEGIN LOST+FOUND CONTENT ---";
    private static final String END_CONTENT = "--- END LOST+FOUND CONTENT ---";
    private static final String BEGIN_EXCEPTION = "--- BEGIN LOST+FOUND EXCEPTION ---";
    private static final String END_EXCEPTION = "--- END LOST+FOUND EXCEPTION ---";

    public static LostAndFoundContext init(Object... scopes)
    {
        var context = LostAndFoundContext.create(scopes);
        LostAndFound.CONTEXT = context;
        DimensionalInventories.LOGGER.debug(LostAndFound.CONTEXT.toString());
        return context;
    }

    public static LostAndFoundScope push(Object... layers)
    {
        var pushed = LostAndFound.CONTEXT.push(layers);
        DimensionalInventories.LOGGER.debug(LostAndFound.CONTEXT.toString());
        return pushed;
    }

    public static void log(String cause, String content)
    {
        log(cause, content, null);
    }

    public static void log(String cause, Exception exception)
    {
        log(cause, null, exception);
    }

    public static void log(String cause, String content, Exception exception)
    {
        LostAndFound.informPlayer();

        final Date now = new Date();
        final ArrayList<String> lines = new ArrayList<>();

        lines.add(LostAndFound.BEGIN_METADATA);
        lines.add(new SimpleDateFormat(LostAndFound.TIMESTAMP_FORMAT_TEXT).format(now));
        lines.add(cause);
        lines.add(LostAndFound.CONTEXT.toString());
        lines.add(LostAndFound.END_METADATA);

        if (content != null)
        {
            lines.add("");

            lines.add(LostAndFound.BEGIN_CONTENT);
            lines.add(content);
            lines.add(LostAndFound.END_CONTENT);
        }

        if (exception != null)
        {
            lines.add("");

            lines.add(LostAndFound.BEGIN_EXCEPTION);
            lines.add(exception.toString());
            lines.add(LostAndFound.END_EXCEPTION);
        }

        final String entry = String.join("\n", lines);
        final Path outputDirectory = LostAndFound.CONTEXT.outputDirectory();
        final Path outputFile = outputDirectory
            .resolve(new SimpleDateFormat(LostAndFound.TIMESTAMP_FORMAT_FILE).format(now) + LostAndFound.FILE_EXT);

        DimensionalInventories.LOGGER.error(cause);
        DimensionalInventories.LOGGER.error("Context: {}", LostAndFound.CONTEXT);

        try
        {
            Files.createDirectories(outputDirectory);
            Files.writeString(outputFile, entry);
            DimensionalInventories.LOGGER.error("Details have been written to lost+found");
            DimensionalInventories.LOGGER.error("File: '{}'", outputFile);
        }
        catch (IOException e)
        {
            DimensionalInventories.LOGGER.error("Failed to save lost+found entry:\n{}", entry);
            DimensionalInventories.LOGGER.error("Caused by:", e);
        }
    }

    private static void informPlayer()
    {
        try
        {
            LostAndFound.CONTEXT.getPlayer().sendMessage(Text
                .literal("Some data was lost when crossing dimension pools. Consult server staff for more details and, possibly, data recovery.")
                .formatted(Formatting.RED)
            );
        }
        catch (NoSuchElementException e)
        {
            // Only inform the player if the context actually includes a player
        }
    }
}
