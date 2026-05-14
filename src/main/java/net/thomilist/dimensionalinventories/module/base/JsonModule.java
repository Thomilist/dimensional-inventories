package net.thomilist.dimensionalinventories.module.base;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import net.minecraft.nbt.CompoundTag;
import net.thomilist.dimensionalinventories.DimensionalInventories;
import net.thomilist.dimensionalinventories.lostandfound.LostAndFound;
import net.thomilist.dimensionalinventories.module.version.VersionedJsonData;
import net.thomilist.dimensionalinventories.module.version.VersionedJsonDataSerializerPair;
import net.thomilist.dimensionalinventories.util.gson.NbtCompoundSerializerPair;
import net.thomilist.dimensionalinventories.util.gson.OptionalSerializerPair;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.Optional;

public interface JsonModule<T extends ModuleState>
    extends StatefulModule<T>
{
    GsonBuilder GSON_BUILDER = new GsonBuilder()
        .registerTypeAdapter( VersionedJsonData.class, new VersionedJsonDataSerializerPair() )
        .registerTypeAdapter( Optional.class, new OptionalSerializerPair<>() )
        .registerTypeAdapter( CompoundTag.class, new NbtCompoundSerializerPair() )
        .setPrettyPrinting();

    Gson gson();

    default String noSuchFileWarning()
    {
        return "No data found (default data loaded instead)";
    }

    default String saveFileName()
    {
        return this.moduleId() + ".json";
    }

    default T load( final Path saveFile )
    {
        final String json;

        try
        {
            json = Files.readString( saveFile );
        }
        catch ( final NoSuchFileException e )
        {
            DimensionalInventories.LOGGER.warn( this.noSuchFileWarning() );
            DimensionalInventories.LOGGER.warn( "Context: {}", LostAndFound.CONTEXT );
            return this.defaultState();
        }
        catch ( final IOException e )
        {
            LostAndFound.log( "Failed to load data", saveFile.toString(), e );
            return this.state();
        }

        final T data;

        try
        {
            data = this.loadFromJsonString( json );
        }
        catch ( final JsonParseException e )
        {
            LostAndFound.log( "Failed to parse JSON data", json, e );
            return this.state();
        }

        return data;
    }

    default T loadFromJsonString( final String json )
        throws JsonParseException
    {
        final VersionedJsonData versionedData = this.gson().fromJson( json, VersionedJsonData.class );
        return this.loadVersionedData( versionedData );
    }

    default T loadVersionedData( final VersionedJsonData versionedData )
        throws JsonParseException
    {
        return this.loadAsCurrentVersion( versionedData.data() );
    }

    default T loadAsCurrentVersion( final JsonElement data )
        throws JsonParseException
    {
        return this.gson().fromJson( data, this.state().type() );
    }

    default void save( final Path saveFile, final T data )
    {
        final JsonElement dataJson = this.gson().toJsonTree( data );
        final VersionedJsonData versionedData = new VersionedJsonData( this.moduleVersion(), dataJson );
        final String json = this.gson().toJson( versionedData );

        try
        {
            Files.createDirectories( saveFile.getParent() );
            Files.writeString( saveFile, json );
        }
        catch ( final IOException e )
        {
            LostAndFound.log( "Unable to save data", json, e );
        }
    }
}
