package net.thomilist.dimensionalinventories.module.builtin.shoulderentity;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.thomilist.dimensionalinventories.mixin.ServerPlayerEntityAccessor;
import net.thomilist.dimensionalinventories.module.base.player.PlayerModuleState;

import java.lang.reflect.Type;

public class ShoulderEntityModuleState
    implements PlayerModuleState
{
    public NbtCompound leftShoulderNbt = new NbtCompound();
    public NbtCompound rightShoulderNbt = new NbtCompound();
    public long shoulderMountTime = 0;

    public ShoulderEntityModuleState()
    { }

    public ShoulderEntityModuleState( final ServerPlayerEntity player )
    {
        this.loadFromPlayer( player );
    }

    @Override
    public void applyToPlayer( final ServerPlayerEntity player )
    {
        ((ServerPlayerEntityAccessor) player).dimensionalinventories$setLeftShoulderNbt(this.leftShoulderNbt);
        ((ServerPlayerEntityAccessor) player).dimensionalinventories$setRightShoulderNbt(this.rightShoulderNbt);
        ((ServerPlayerEntityAccessor) player).setShoulderMountTime( this.shoulderMountTime );
    }

    @Override
    public void loadFromPlayer( final ServerPlayerEntity player )
    {
        this.leftShoulderNbt = player.getLeftShoulderNbt();
        this.rightShoulderNbt = player.getRightShoulderNbt();
        this.shoulderMountTime = ((ServerPlayerEntityAccessor) player).getShoulderMountTime();
    }

    @Override
    public Type type()
    {
        return ShoulderEntityModuleState.class;
    }
}
