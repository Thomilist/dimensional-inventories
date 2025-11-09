package net.thomilist.dimensionalinventories.compatibility;

import net.minecraft.server.MinecraftServer;
import net.thomilist.dimensionalinventories.compatibility.java.collection.ListCompatWrapper;
import net.thomilist.dimensionalinventories.compatibility.java.collection.ListCompatWrapper_Java_21;
import net.thomilist.dimensionalinventories.compatibility.java.collection.SortedSetCompatWrapper;
import net.thomilist.dimensionalinventories.compatibility.java.collection.SortedSetCompatWrapper_Java_21;
import net.thomilist.dimensionalinventories.compatibility.minecraft.entity.EntityCompatWrapper;
import net.thomilist.dimensionalinventories.compatibility.minecraft.entity.EntityCompatWrapper_Minecraft_1_21_9;
import net.thomilist.dimensionalinventories.compatibility.minecraft.inventory.PlayerInventoryCompatWrapper;
import net.thomilist.dimensionalinventories.compatibility.minecraft.inventory.PlayerInventoryCompatWrapper_Minecraft_1_21_5;
import net.thomilist.dimensionalinventories.compatibility.minecraft.inventory.SimpleInventoryCompatWrapper;
import net.thomilist.dimensionalinventories.compatibility.minecraft.inventory.SimpleInventoryCompatWrapper_Minecraft_1_20_3;
import net.thomilist.dimensionalinventories.compatibility.minecraft.nbt.NbtCompatWrapper;
import net.thomilist.dimensionalinventories.compatibility.minecraft.nbt.NbtCompatWrapper_Minecraft_1_21_6;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * Wrapper for functionality with different implementations across Java or Minecraft versions. The default
 * implementation targets the oldest versions the mod actively supports (currently Java 17 / Minecraft 1.20.1).
 */
public final class Compat
{
    // For Java versions
    public static final ListCompatWrapper LIST = new ListCompatWrapper_Java_21();
    public static final SortedSetCompatWrapper SORTED_SET = new SortedSetCompatWrapper_Java_21();

    // For Minecraft versions
    public static final NbtCompatWrapper NBT = new NbtCompatWrapper_Minecraft_1_21_6();
    public static final SimpleInventoryCompatWrapper SIMPLE_INVENTORY
        = new SimpleInventoryCompatWrapper_Minecraft_1_20_3();
    public static final PlayerInventoryCompatWrapper PLAYER_INVENTORY
        = new PlayerInventoryCompatWrapper_Minecraft_1_21_5();
    public static final EntityCompatWrapper ENTITY = new EntityCompatWrapper_Minecraft_1_21_9();

    private Compat()
    { }

    public static void onServerStarted( final MinecraftServer server )
    {
        for ( final Field field : Compat.class.getFields() )
        {
            if ( !Modifier.isStatic( field.getModifiers() ) )
            {
                continue;
            }

            if ( !CompatWrapper.class.isAssignableFrom( field.getType() ) )
            {
                continue;
            }

            try
            {
                ((CompatWrapper) field.get( null )).onServerStarted( server );
            }
            catch ( final IllegalAccessException e )
            {
                continue;
            }
        }
    }
}
