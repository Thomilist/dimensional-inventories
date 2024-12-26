package net.thomilist.dimensionalinventories.util;

import com.google.gson.stream.JsonReader;
import net.fabricmc.loader.api.FabricLoader;
import net.thomilist.dimensionalinventories.exception.PropertyReadException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ModProperties
{
    private final String id;
    private final List<String> authors = new ArrayList<>();
    private String namePretty = "<unknown mod name>";
    private String namePascal = "<unknown mod name>";
    private String version = "<unknown mod version>";
    private String description = "<unknown mod description>";

    public ModProperties( final String modId )
    {
        this.id = modId;

        try
        {
            final Optional<Path> fabricModJsonPath = FabricLoader
                .getInstance()
                .getModContainer( this.id )
                .orElseThrow()
                .findPath( "fabric.mod.json" );

            if ( fabricModJsonPath.isEmpty() )
            {
                throw new PropertyReadException();
            }

            try ( final InputStream stream = Files.newInputStream( fabricModJsonPath.get() ) )
            {
                final InputStreamReader reader = new InputStreamReader( stream, StandardCharsets.UTF_8 );

                try ( final JsonReader jsonReader = new JsonReader( reader ) )
                {
                    jsonReader.beginObject();
                    String name;

                    while ( jsonReader.hasNext() )
                    {
                        name = jsonReader.nextName();

                        switch ( name )
                        {
                            case "version" -> this.version = jsonReader.nextString();
                            case "name" ->
                            {
                                this.namePretty = jsonReader.nextString();
                                this.namePascal = StringHelper.toPascalCase( this.namePretty );
                            }
                            case "description" -> this.description = jsonReader.nextString();
                            case "authors" ->
                            {
                                jsonReader.beginArray();

                                while ( jsonReader.hasNext() )
                                {
                                    this.authors.add( jsonReader.nextString() );
                                }

                                jsonReader.endArray();
                            }
                            default -> jsonReader.skipValue();
                        }
                    }

                    jsonReader.endObject();
                }
            }
        }
        catch ( final IOException e )
        {
            throw new PropertyReadException();
        }
    }

    public String version()
    {
        return this.version;
    }

    public String id()
    {
        return this.id;
    }

    public String namePretty()
    {
        return this.namePretty;
    }

    public String namePascal()
    {
        return this.namePascal;
    }

    public String description()
    {
        return this.description;
    }

    public List<String> authors()
    {
        return this.authors;
    }

    public String authorsPretty()
    {
        if ( this.authors.isEmpty() )
        {
            return "<no authors found>";
        }
        else
        {
            return StringHelper.joinLastDifferent( ", ", " & ", this.authors );
        }
    }
}
