package net.thomilist.dimensionalinventories.module.builtin.status;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.server.network.ServerPlayerEntity;
import net.thomilist.dimensionalinventories.module.base.player.PlayerModuleState;
import net.thomilist.dimensionalinventories.util.ExperienceHelper;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;

public class StatusModuleState implements PlayerModuleState
{
    public int experiencePoints = 0;
    public int score = 0;
    public int foodLevel = 20;
    public float saturationLevel = 5.0f;
    public float exhaustion = 0.0f;
    public float health = 20.0f;
    public Collection<StatusEffectInstance> statusEffects = new ArrayList<>();

    public StatusModuleState()
    { }

    public StatusModuleState(ServerPlayerEntity player)
    {
        loadFromPlayer(player);
    }

    @Override
    public Type type()
    {
        return StatusModuleState.class;
    }

    @Override
    public void applyToPlayer(ServerPlayerEntity player)
    {
        ExperienceHelper.setExperience(player, experiencePoints);
        player.setScore(score);
        player.getHungerManager().setFoodLevel(foodLevel);
        player.getHungerManager().setSaturationLevel(saturationLevel);
        player.getHungerManager().setExhaustion(exhaustion);
        player.setHealth(health);

        player.clearStatusEffects();

        for (StatusEffectInstance statusEffect : statusEffects)
        {
            player.addStatusEffect(statusEffect);
        }
    }

    @Override
    public void loadFromPlayer(ServerPlayerEntity player)
    {
        experiencePoints = ExperienceHelper.getTotalExperience_Meridanus(player);
        score = player.getScore();
        foodLevel = player.getHungerManager().getFoodLevel();
        saturationLevel = player.getHungerManager().getSaturationLevel();
        exhaustion = player.getHungerManager().getExhaustion();
        health = player.getHealth();

        statusEffects = player.getStatusEffects();
    }
}
