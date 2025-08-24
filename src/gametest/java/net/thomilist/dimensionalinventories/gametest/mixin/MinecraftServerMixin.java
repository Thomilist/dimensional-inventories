package net.thomilist.dimensionalinventories.gametest.mixin;

import net.minecraft.server.MinecraftServer;
import net.minecraft.test.TestServer;
import net.minecraft.util.UserCache;
import net.thomilist.dimensionalinventories.gametest.DimensionPoolChangeOnRespawnTest;
import net.thomilist.dimensionalinventories.gametest.util.DummyUserCache;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Arrays;

@Mixin( MinecraftServer.class )
public class MinecraftServerMixin
{
    @Unique
    private static boolean relevantTestInStackTrace( final StackTraceElement stackFrame )
    {
        return stackFrame.getClassName().equals( DimensionPoolChangeOnRespawnTest.class.getName() );
    }

    /**
     * Gets the user cache of this server, with special cases for certain game tests.
     * <p>
     * For {@link TestServer} instances, {@link MinecraftServer#getUserCache()} returns {@code null}. However, for
     * game tests that depend on the user cache being present without caring about the actual cache contents, this
     * replaces the return value with a {@link DummyUserCache} instance.
     *
     * @param cir The callback info returnable used to override the return value
     */
    @Inject( at = @At( "HEAD" ),
             method = "getUserCache()Lnet/minecraft/util/UserCache;",
             cancellable = true )
    public void getUserCache( final CallbackInfoReturnable<UserCache> cir )
    {
        if ( !((Object) this instanceof TestServer) )
        {
            return;
        }

        final StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();

        if ( Arrays.stream( stackTrace ).noneMatch( MinecraftServerMixin::relevantTestInStackTrace ) )
        {
            return;
        }

        cir.setReturnValue( new DummyUserCache() );
        cir.cancel();
    }
}
