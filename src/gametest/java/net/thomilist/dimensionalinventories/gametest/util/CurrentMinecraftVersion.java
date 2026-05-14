package net.thomilist.dimensionalinventories.gametest.util;

import net.fabricmc.loader.api.SemanticVersion;
import net.fabricmc.loader.api.Version;
import net.fabricmc.loader.api.VersionParsingException;
import net.fabricmc.loader.impl.game.minecraft.McVersionLookup;
import net.minecraft.DetectedVersion;
import net.minecraft.WorldVersion;

public final class CurrentMinecraftVersion
{
    private CurrentMinecraftVersion()
    { }

    public static WorldVersion get()
    {
        return DetectedVersion.tryDetectVersion();
    }

    public static SemanticVersion asSemanticVersion()
        throws VersionParsingException
    {
        return CurrentMinecraftVersion.normalize( CurrentMinecraftVersion.get().id() );
    }

    public static boolean isNewerThan( final String version )
        throws VersionParsingException
    {
        return CurrentMinecraftVersion.compareTo( version ) > 0;
    }

    public static boolean isNewerThanOrEqualTo( final String version )
        throws VersionParsingException
    {
        return CurrentMinecraftVersion.compareTo( version ) >= 0;
    }

    public static boolean isOlderThan( final String version )
        throws VersionParsingException
    {
        return CurrentMinecraftVersion.compareTo( version ) < 0;
    }

    public static boolean isOlderThanOrEqualTo( final String version )
        throws VersionParsingException
    {
        return CurrentMinecraftVersion.compareTo( version ) <= 0;
    }

    public static int compareTo( final String version )
        throws VersionParsingException
    {
        return CurrentMinecraftVersion.asSemanticVersion().compareTo( (Version) CurrentMinecraftVersion.normalize( version ) );
    }

    private static SemanticVersion normalize( final String version )
        throws VersionParsingException
    {
        final String release = McVersionLookup.getRelease( version );
        final String normalized = McVersionLookup.normalizeVersion( version, release );
        return SemanticVersion.parse( normalized );
    }
}
