package net.thomilist.dimensionalinventories.gametest.mixin;

import net.minecraft.world.entity.animal.parrot.Parrot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin( Parrot.class )
public interface ParrotAccessor
{
    @Invoker("setVariant")
    void invokeSetVariant( Parrot.Variant variant );
}
