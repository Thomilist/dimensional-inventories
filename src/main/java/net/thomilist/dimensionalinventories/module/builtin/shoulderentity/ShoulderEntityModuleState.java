package net.thomilist.dimensionalinventories.module.builtin.shoulderentity;

import net.minecraft.entity.data.TrackedData;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.thomilist.dimensionalinventories.mixin.PlayerEntityAccessor;
import net.thomilist.dimensionalinventories.module.base.player.PlayerModuleState;

import java.lang.reflect.Type;

public class ShoulderEntityModuleState implements PlayerModuleState
{
    public static TrackedData<NbtCompound> LEFT_SHOULDER_ENTITY = PlayerEntityAccessor.getLeftShoulderEntity();
    public static TrackedData<NbtCompound> RIGHT_SHOULDER_ENTITY = PlayerEntityAccessor.getRightShoulderEntity();

    public NbtCompound leftShoulderEntity = new NbtCompound();
    public NbtCompound rightShoulderEntity = new NbtCompound();
    public long shoulderEntityAddedTime = 0;

    public ShoulderEntityModuleState()
    { }

    public ShoulderEntityModuleState(ServerPlayerEntity player)
    {
        loadFromPlayer(player);
    }

    @Override
    public void applyToPlayer(ServerPlayerEntity player)
    {
        player.getDataTracker().set(ShoulderEntityModuleState.LEFT_SHOULDER_ENTITY, leftShoulderEntity);
        player.getDataTracker().set(ShoulderEntityModuleState.RIGHT_SHOULDER_ENTITY, rightShoulderEntity);
        ((PlayerEntityAccessor) player).setShoulderEntityAddedTime(shoulderEntityAddedTime);
    }

    @Override
    public void loadFromPlayer(ServerPlayerEntity player)
    {
        leftShoulderEntity = player.getShoulderEntityLeft();
        rightShoulderEntity = player.getShoulderEntityRight();
        shoulderEntityAddedTime = ((PlayerEntityAccessor) player).getShoulderEntityAddedTime();
    }

    @Override
    public Type type()
    {
        return ShoulderEntityModuleState.class;
    }
}
