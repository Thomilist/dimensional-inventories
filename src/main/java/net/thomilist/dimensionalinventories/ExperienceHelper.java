package net.thomilist.dimensionalinventories;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.MathHelper;

// Code from sf-inc/xp_storage (MIT license)
// https://github.com/sf-inc/xp_storage/blob/master/src/main/java/com/github/charlyb01/xpstorage/Utils.java

public class ExperienceHelper
{
    public static void setExperience(ServerPlayerEntity player, int experience)
    {
        player.totalExperience = MathHelper.clamp(experience, 0, Integer.MAX_VALUE);
        player.experienceLevel = 0;
        player.addExperienceLevels(ExperienceHelper.getLevelFromExperience(experience));
        int deltaExperience = player.totalExperience - ExperienceHelper.getExperienceToLevel(player.experienceLevel);
        player.experienceProgress = deltaExperience / (float)player.getNextLevelExperience();
        return;
    }
    
    private static int getExperienceFromLevel(final int level)
    {
        if (level >= 30)
        {
            return 112 + (level - 30) * 9;
        }
        else
        {
            return level >= 15 ? 37 + (level - 15) * 5 : 7 + level * 2;
        }
    }

    private static int getExperienceFromLevelToLevel(final int fromLevel, final int toLevel)
    {
        int experience = 0;

        for (int i = fromLevel; i < toLevel; i++)
        {
            experience += getExperienceFromLevel(i);
        }

        return experience;
    }

    private static int getExperienceToLevel(final int toLevel)
    {
        return getExperienceFromLevelToLevel(0, toLevel);
    }

    private static int getLevelFromExperience(final int experience)
    {
        if (experience <= 0)
        {
            return 0;
        }

        int level = 0;
        int xp = 0;

        while (xp < experience)
        {
            xp += getExperienceFromLevel(level++);
        }

        return xp == experience ? level : level - 1;
    }
}
