package net.thomilist.dimensionalinventories.module.builtin.status;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.server.network.ServerPlayerEntity;
import net.thomilist.dimensionalinventories.mixin.HungerManagerAccessor;
import net.thomilist.dimensionalinventories.module.base.player.PlayerModuleState;
import net.thomilist.dimensionalinventories.util.ExperienceHelper;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;

public class StatusModuleState
    implements PlayerModuleState
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

    public StatusModuleState( final ServerPlayerEntity player )
    {
        this.loadFromPlayer( player );
    }

    @Override
    public Type type()
    {
        return StatusModuleState.class;
    }

    @Override
    public void applyToPlayer( final ServerPlayerEntity player )
    {
        ExperienceHelper.setExperience( player, this.experiencePoints );
        player.setScore( this.score );
        player.getHungerManager().setFoodLevel( this.foodLevel );
        player.getHungerManager().setSaturationLevel( this.saturationLevel );
        ((HungerManagerAccessor) player.getHungerManager()).setExhaustion( this.exhaustion );
        player.setHealth( this.health );

        player.clearStatusEffects();

        for ( final StatusEffectInstance statusEffect : this.statusEffects )
        {
            player.addStatusEffect( statusEffect );
        }
    }

    @Override
    public void loadFromPlayer( final ServerPlayerEntity player )
    {
        this.experiencePoints = ExperienceHelper.getTotalExperience_Meridanus( player );
        this.score = player.getScore();
        this.foodLevel = player.getHungerManager().getFoodLevel();
        this.saturationLevel = player.getHungerManager().getSaturationLevel();
        this.exhaustion = ((HungerManagerAccessor) player.getHungerManager()).getExhaustion();
        this.health = player.getHealth();

        this.statusEffects = player.getStatusEffects();
    }
}
