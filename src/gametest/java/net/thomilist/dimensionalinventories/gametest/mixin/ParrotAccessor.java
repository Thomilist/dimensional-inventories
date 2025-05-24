package net.thomilist.dimensionalinventories.gametest.mixin;

import net.minecraft.entity.passive.ParrotEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin( ParrotEntity.class )
public interface ParrotAccessor
{
    @Invoker("setVariant")
    void invokeSetVariant( ParrotEntity.Variant variant );
}
