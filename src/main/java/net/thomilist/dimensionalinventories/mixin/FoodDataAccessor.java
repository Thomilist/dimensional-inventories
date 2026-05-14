package net.thomilist.dimensionalinventories.mixin;

import net.minecraft.world.food.FoodData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin( FoodData.class )
public interface FoodDataAccessor
{
    @Accessor
    float getExhaustionLevel();

    @Accessor( "exhaustionLevel" )
    void setExhaustionLevel( float exhaustion );
}
