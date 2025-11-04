package net.thomilist.dimensionalinventories.mixin;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.nbt.NbtCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin( ServerPlayerEntity.class )
public interface ServerPlayerEntityAccessor
{
    @Invoker
    void invokeDropShoulderEntities();

    @Invoker( "setLeftShoulderNbt" )
    void dimensionalinventories$setLeftShoulderNbt(NbtCompound leftShoulderNbt);

    @Invoker( "setRightShoulderNbt" )
    void dimensionalinventories$setRightShoulderNbt(NbtCompound rightShoulderNbt);

    @Accessor( "shoulderMountTime" )
    long getShoulderMountTime();

    @Accessor ( "shoulderMountTime" )
    void setShoulderMountTime(long time);
}
