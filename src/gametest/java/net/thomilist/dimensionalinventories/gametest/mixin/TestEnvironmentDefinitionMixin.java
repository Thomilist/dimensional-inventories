package net.thomilist.dimensionalinventories.gametest.mixin;

import com.mojang.serialization.MapCodec;
import net.minecraft.registry.Registry;
import net.minecraft.test.TestEnvironmentDefinition;
import net.thomilist.dimensionalinventories.gametest.util.BatchDefinition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin( TestEnvironmentDefinition.class )
public interface TestEnvironmentDefinitionMixin
{
    @Inject( at = @At( "HEAD" ),
             method = "registerAndGetDefault(Lnet/minecraft/registry/Registry;)Lcom/mojang/serialization/MapCodec;" )
    private static void registerAndGetDefault( final Registry<MapCodec<? extends TestEnvironmentDefinition>> registry,
                                               final CallbackInfoReturnable<MapCodec<?
                                                   extends TestEnvironmentDefinition>> cir )
    {
        Registry.register( registry, "batch_number", BatchDefinition.CODEC );
    }
}
