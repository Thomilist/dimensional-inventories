package net.thomilist.dimensionalinventories.module.builtin.status;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.thomilist.dimensionalinventories.mixin.FoodDataAccessor;
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
    public Collection<MobEffectInstance> statusEffects = new ArrayList<>();

    public StatusModuleState()
    { }

    public StatusModuleState( final ServerPlayer player )
    {
        this.loadFromPlayer( player );
    }

    @Override
    public Type type()
    {
        return StatusModuleState.class;
    }

    @Override
    public void applyToPlayer( final ServerPlayer player )
    {
        ExperienceHelper.setExperience( player, this.experiencePoints );
        player.setScore( this.score );
        player.getFoodData().setFoodLevel( this.foodLevel );
        player.getFoodData().setSaturation( this.saturationLevel );
        ((FoodDataAccessor) player.getFoodData()).setExhaustionLevel( this.exhaustion );
        player.setHealth( this.health );

        player.removeAllEffects();

        for ( final MobEffectInstance statusEffect : this.statusEffects )
        {
            player.addEffect( statusEffect );
        }
    }

    @Override
    public void loadFromPlayer( final ServerPlayer player )
    {
        this.experiencePoints = ExperienceHelper.getTotalExperience_Meridanus( player );
        this.score = player.getScore();
        this.foodLevel = player.getFoodData().getFoodLevel();
        this.saturationLevel = player.getFoodData().getSaturationLevel();
        this.exhaustion = ((FoodDataAccessor) player.getFoodData()).getExhaustionLevel();
        this.health = player.getHealth();

        this.statusEffects = player.getActiveEffects();
    }
}
