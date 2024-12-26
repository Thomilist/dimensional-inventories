package net.thomilist.dimensionalinventories.util;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.MathHelper;

// Code from sf-inc/xp_storage (MIT licence)
// https://github.com/sf-inc/xp_storage/blob/master/src/main/java/com/github/charlyb01/xpstorage/Utils.java

// Code from Meridanus/fabric_xp_storage_1.18 (MIT license)
// https://github.com/Meridanus/fabric_xp_storage_1.18/blob/ee109ef98654efefa92111effcc06101d13d330d/src/main/java/com/notker/xp_storage/XpFunctions.java#L34

public final class ExperienceHelper
{
    private ExperienceHelper()
    { }

    public static void setExperience( final ServerPlayerEntity player, final int experience )
    {
        player.totalExperience = MathHelper.clamp( experience, 0, Integer.MAX_VALUE );
        player.experienceLevel = 0;
        player.addExperienceLevels( ExperienceHelper.getLevelFromExperience_sfinc( experience ) );
        final int deltaExperience = player.totalExperience -
                                    ExperienceHelper.getExperienceToLevel_sfinc( player.experienceLevel );
        player.experienceProgress = deltaExperience / (float) player.getNextLevelExperience();
    }

    public static int getTotalExperience_Meridanus( final ServerPlayerEntity player )
    {
        return ExperienceHelper.getTotalExperience_Meridanus(
            player.experienceLevel,
            player.getNextLevelExperience(),
            player.experienceProgress
        );
    }

    public static int getTotalExperience_Meridanus( final int level,
                                                    final int nextLevelExperience,
                                                    final float experienceProgress )
    {
        return ExperienceHelper.getExperienceFromLevel_Meridanus( level ) +
               ExperienceHelper.getExperienceFromBar_Meridanus( nextLevelExperience, experienceProgress );
    }

    public static int getExperienceFromLevel_sfinc( final int level )
    {
        if ( level >= 30 )
        {
            return 112 + ((level - 30) * 9);
        }
        else
        {
            return (level >= 15) ? (37 + ((level - 15) * 5)) : (7 + (level * 2));
        }
    }

    public static int getExperienceFromLevel_Meridanus( final int level )
    {
        if ( (level >= 1) && (level <= 16) )
        {
            return (int) (Math.pow( level, 2 ) + (6 * level));
        }
        else if ( (level >= 17) && (level <= 31) )
        {
            return (int) (((2.5 * Math.pow( level, 2 )) - (40.5 * level)) + 360);
        }
        else if ( level >= 32 )
        {
            return (int) (((4.5 * Math.pow( level, 2 )) - (162.5 * level)) + 2220);
        }
        else
        {
            return 0;
        }
    }

    public static int getExperienceFromBar_Meridanus( final int nextLevelExperience, final float experienceProgress )
    {
        return (int) (nextLevelExperience * experienceProgress);
    }

    private static int getExperienceFromLevelToLevel_sfinc( final int fromLevel, final int toLevel )
    {
        int experience = 0;

        for ( int i = fromLevel; i < toLevel; i++ )
        {
            experience += ExperienceHelper.getExperienceFromLevel_sfinc( i );
        }

        return experience;
    }

    private static int getExperienceToLevel_sfinc( final int toLevel )
    {
        return ExperienceHelper.getExperienceFromLevelToLevel_sfinc( 0, toLevel );
    }

    private static int getLevelFromExperience_sfinc( final int experience )
    {
        if ( experience <= 0 )
        {
            return 0;
        }

        int level = 0;
        int xp = 0;

        while ( xp < experience )
        {
            xp += ExperienceHelper.getExperienceFromLevel_sfinc( level++ );
        }

        return (xp == experience) ? level : (level - 1);
    }
}
