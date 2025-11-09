package net.thomilist.dimensionalinventories.mixin;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin( ServerPlayerEntity.class )
public interface ServerPlayerEntityAccessor
{
    @Accessor
    long getShoulderMountTime();

    @Accessor( "shoulderMountTime" )
    void setShoulderMountTime( long shoulderMountTime );

    @Invoker
    void invokeDropShoulderEntities();

    @Invoker
    void invokeSetLeftShoulderNbt( NbtCompound leftShoulderNbt );

    @Invoker
    void invokeSetRightShoulderNbt( NbtCompound rightShoulderNbt );
}
